package com.refinedmods.refinedstorage.common.support;

import net.minecraft.world.level.block.state.BlockState;

public record CableShapeCacheKey(boolean north, boolean east, boolean south, boolean west, boolean up, boolean down) {
    public static CableShapeCacheKey of(final BlockState state) {
        return new CableShapeCacheKey(
            state.getValue(CableBlockSupport.NORTH),
            state.getValue(CableBlockSupport.EAST),
            state.getValue(CableBlockSupport.SOUTH),
            state.getValue(CableBlockSupport.WEST),
            state.getValue(CableBlockSupport.UP),
            state.getValue(CableBlockSupport.DOWN)
        );
    }
}
