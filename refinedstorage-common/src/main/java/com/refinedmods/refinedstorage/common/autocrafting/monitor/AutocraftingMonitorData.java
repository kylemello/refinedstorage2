package com.refinedmods.refinedstorage.common.autocrafting.monitor;

import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AutocraftingMonitorData(List<TaskStatus> statuses) {
    public static final StreamCodec<RegistryFriendlyByteBuf, AutocraftingMonitorData> STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, AutocraftingMonitorStreamCodecs.STATUS_STREAM_CODEC),
            AutocraftingMonitorData::statuses,
            AutocraftingMonitorData::new
        );
}
