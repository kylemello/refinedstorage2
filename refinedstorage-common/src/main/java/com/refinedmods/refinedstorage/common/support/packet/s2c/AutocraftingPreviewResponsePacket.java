package com.refinedmods.refinedstorage.common.support.packet.s2c;

import com.refinedmods.refinedstorage.api.autocrafting.AutocraftingPreview;
import com.refinedmods.refinedstorage.api.autocrafting.AutocraftingPreviewItem;
import com.refinedmods.refinedstorage.api.autocrafting.AutocraftingPreviewType;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;
import com.refinedmods.refinedstorage.common.util.ClientPlatformUtil;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.PlatformUtil.enumStreamCodec;

public record AutocraftingPreviewResponsePacket(UUID id, AutocraftingPreview preview) implements CustomPacketPayload {
    public static final Type<AutocraftingPreviewResponsePacket> PACKET_TYPE = new Type<>(
        createIdentifier("autocrafting_preview_response")
    );
    private static final StreamCodec<RegistryFriendlyByteBuf, AutocraftingPreviewItem> PREVIEW_ITEM_STREAM_CODEC =
        StreamCodec.composite(
            ResourceCodecs.STREAM_CODEC, item -> (PlatformResourceKey) item.resource(),
            ByteBufCodecs.VAR_LONG, AutocraftingPreviewItem::available,
            ByteBufCodecs.VAR_LONG, AutocraftingPreviewItem::missing,
            ByteBufCodecs.VAR_LONG, AutocraftingPreviewItem::toCraft,
            AutocraftingPreviewItem::new
        );
    private static final StreamCodec<RegistryFriendlyByteBuf, AutocraftingPreview> PREVIEW_STREAM_CODEC =
        StreamCodec.composite(
            enumStreamCodec(AutocraftingPreviewType.values()), AutocraftingPreview::type,
            ByteBufCodecs.collection(ArrayList::new, PREVIEW_ITEM_STREAM_CODEC), AutocraftingPreview::items,
            AutocraftingPreview::new
        );
    public static final StreamCodec<RegistryFriendlyByteBuf, AutocraftingPreviewResponsePacket> STREAM_CODEC =
        StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, AutocraftingPreviewResponsePacket::id,
            PREVIEW_STREAM_CODEC, AutocraftingPreviewResponsePacket::preview,
            AutocraftingPreviewResponsePacket::new
        );

    public static void handle(final AutocraftingPreviewResponsePacket packet) {
        ClientPlatformUtil.autocraftingPreviewResponseReceived(packet.id, packet.preview);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}

