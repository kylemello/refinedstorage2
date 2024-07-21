package com.refinedmods.refinedstorage.common.storage.storageblock;

import com.refinedmods.refinedstorage.common.content.BlockConstants;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.storage.ItemStorageVariant;
import com.refinedmods.refinedstorage.common.support.network.NetworkNodeBlockEntityTicker;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ItemStorageBlock extends AbstractStorageBlock<ItemStorageBlockBlockEntity> {
    private final ItemStorageVariant variant;

    public ItemStorageBlock(final ItemStorageVariant variant) {
        super(
            BlockConstants.PROPERTIES,
            new NetworkNodeBlockEntityTicker<>(() -> BlockEntities.INSTANCE.getItemStorageBlock(variant))
        );
        this.variant = variant;
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new ItemStorageBlockBlockEntity(pos, state, variant);
    }
}
