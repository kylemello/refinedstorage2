package com.refinedmods.refinedstorage.common.autocrafting;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record CrafterData(boolean chained) {
    public static final StreamCodec<RegistryFriendlyByteBuf, CrafterData> STREAM_CODEC =
        StreamCodec.composite(ByteBufCodecs.BOOL, CrafterData::chained, CrafterData::new);
}
