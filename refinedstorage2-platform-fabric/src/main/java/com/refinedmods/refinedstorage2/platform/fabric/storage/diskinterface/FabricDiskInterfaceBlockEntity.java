package com.refinedmods.refinedstorage2.platform.fabric.storage.diskinterface;

import com.refinedmods.refinedstorage2.platform.common.storage.diskinterface.AbstractDiskInterfaceBlockEntity;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class FabricDiskInterfaceBlockEntity extends AbstractDiskInterfaceBlockEntity {
    public FabricDiskInterfaceBlockEntity(final BlockPos pos, final BlockState state) {
        super(pos, state);
    }

    @Override
    @Nullable
    public Object getRenderData() {
        return disks;
    }
}
