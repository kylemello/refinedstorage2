package com.refinedmods.refinedstorage.fabric.storage.diskdrive;

import com.refinedmods.refinedstorage.common.storage.diskdrive.AbstractDiskDriveBlockEntity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FabricDiskDriveBlockEntity extends AbstractDiskDriveBlockEntity {
    public FabricDiskDriveBlockEntity(final BlockPos pos, final BlockState state) {
        super(pos, state);
    }

    @Override
    @Nullable
    public Object getRenderData() {
        return disks;
    }
}
