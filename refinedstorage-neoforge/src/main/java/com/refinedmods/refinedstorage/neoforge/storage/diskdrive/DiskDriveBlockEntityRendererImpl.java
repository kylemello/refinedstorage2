package com.refinedmods.refinedstorage.neoforge.storage.diskdrive;

import com.refinedmods.refinedstorage.common.storage.Disk;
import com.refinedmods.refinedstorage.common.storage.diskdrive.AbstractDiskDriveBlockEntity;
import com.refinedmods.refinedstorage.common.storage.diskdrive.AbstractDiskDriveBlockEntityRenderer;
import com.refinedmods.refinedstorage.neoforge.support.render.RenderTypes;

public class DiskDriveBlockEntityRendererImpl<T extends AbstractDiskDriveBlockEntity>
    extends AbstractDiskDriveBlockEntityRenderer<T> {
    public DiskDriveBlockEntityRendererImpl() {
        super(RenderTypes.DISK_LED);
    }

    @Override
    protected Disk[] getDisks(final AbstractDiskDriveBlockEntity blockEntity) {
        return blockEntity.getModelData().get(ForgeDiskDriveBlockEntity.DISKS_PROPERTY);
    }
}
