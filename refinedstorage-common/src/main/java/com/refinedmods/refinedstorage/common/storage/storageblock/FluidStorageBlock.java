package com.refinedmods.refinedstorage.common.storage.storageblock;

import com.refinedmods.refinedstorage.common.content.BlockConstants;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.storage.FluidStorageVariant;
import com.refinedmods.refinedstorage.common.support.network.NetworkNodeBlockEntityTicker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FluidStorageBlock extends AbstractStorageBlock<FluidStorageBlockBlockEntity> {
    private final FluidStorageVariant variant;

    public FluidStorageBlock(final FluidStorageVariant variant) {
        super(
            BlockConstants.PROPERTIES,
            new NetworkNodeBlockEntityTicker<>(() -> BlockEntities.INSTANCE.getFluidStorageBlock(variant))
        );
        this.variant = variant;
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new FluidStorageBlockBlockEntity(pos, state, variant);
    }
}
