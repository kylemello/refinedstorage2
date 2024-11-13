package com.refinedmods.refinedstorage.common.autocrafting.monitor;

import com.refinedmods.refinedstorage.api.autocrafting.status.AutocraftingTaskStatus;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;
import com.refinedmods.refinedstorage.common.util.PlatformUtil;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AutocraftingMonitorData(List<AutocraftingTaskStatus> statuses) {
    private static final StreamCodec<RegistryFriendlyByteBuf, AutocraftingTaskStatus.Element>
        STATUS_ELEMENT_STREAM_CODEC = new StatusElementStreamCodec();
    private static final StreamCodec<RegistryFriendlyByteBuf, AutocraftingTaskStatus.Id> ID_STREAM_CODEC =
        StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, AutocraftingTaskStatus.Id::id,
            ResourceCodecs.STREAM_CODEC, s -> (PlatformResourceKey) s.resource(),
            ByteBufCodecs.VAR_LONG, AutocraftingTaskStatus.Id::amount,
            ByteBufCodecs.VAR_LONG, AutocraftingTaskStatus.Id::startTime,
            AutocraftingTaskStatus.Id::new
        );
    private static final StreamCodec<RegistryFriendlyByteBuf, AutocraftingTaskStatus> STATUS_STREAM_CODEC =
        StreamCodec.composite(
            ID_STREAM_CODEC, AutocraftingTaskStatus::id,
            ByteBufCodecs.collection(ArrayList::new, STATUS_ELEMENT_STREAM_CODEC), AutocraftingTaskStatus::elements,
            AutocraftingTaskStatus::new
        );
    public static final StreamCodec<RegistryFriendlyByteBuf, AutocraftingMonitorData> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, STATUS_STREAM_CODEC), AutocraftingMonitorData::statuses,
            AutocraftingMonitorData::new
        );

    private static class StatusElementStreamCodec
        implements StreamCodec<RegistryFriendlyByteBuf, AutocraftingTaskStatus.Element> {
        private static final StreamCodec<ByteBuf, AutocraftingTaskStatus.ElementType> ELEMENT_TYPE_STREAM_CODEC
            = PlatformUtil.enumStreamCodec(AutocraftingTaskStatus.ElementType.values());

        @Override
        public AutocraftingTaskStatus.Element decode(final RegistryFriendlyByteBuf buf) {
            return new AutocraftingTaskStatus.Element(
                ELEMENT_TYPE_STREAM_CODEC.decode(buf),
                ResourceCodecs.STREAM_CODEC.decode(buf),
                buf.readLong(),
                buf.readLong(),
                buf.readLong(),
                buf.readLong(),
                buf.readLong()
            );
        }

        @Override
        public void encode(final RegistryFriendlyByteBuf buf, final AutocraftingTaskStatus.Element element) {
            ELEMENT_TYPE_STREAM_CODEC.encode(buf, element.type());
            ResourceCodecs.STREAM_CODEC.encode(buf, (PlatformResourceKey) element.resource());
            buf.writeLong(element.stored());
            buf.writeLong(element.missing());
            buf.writeLong(element.processing());
            buf.writeLong(element.scheduled());
            buf.writeLong(element.crafting());
        }
    }
}
