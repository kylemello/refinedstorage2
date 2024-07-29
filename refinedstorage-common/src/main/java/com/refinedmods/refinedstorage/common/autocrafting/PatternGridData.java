package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.grid.GridData;
import com.refinedmods.refinedstorage.common.util.PacketUtil;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record PatternGridData(GridData gridData, PatternType patternType) {
    public static final StreamCodec<RegistryFriendlyByteBuf, PatternGridData> STREAM_CODEC = StreamCodec.composite(
        GridData.STREAM_CODEC, PatternGridData::gridData,
        PacketUtil.enumStreamCodec(PatternType.values()), PatternGridData::patternType,
        PatternGridData::new
    );
}
