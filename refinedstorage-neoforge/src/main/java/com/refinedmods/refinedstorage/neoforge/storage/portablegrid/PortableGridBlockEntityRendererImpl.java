package com.refinedmods.refinedstorage.neoforge.storage.portablegrid;

import com.refinedmods.refinedstorage.common.storage.Disk;
import com.refinedmods.refinedstorage.common.storage.portablegrid.AbstractPortableGridBlockEntity;
import com.refinedmods.refinedstorage.common.storage.portablegrid.AbstractPortableGridBlockEntityRenderer;
import com.refinedmods.refinedstorage.neoforge.support.render.RenderTypes;

import javax.annotation.Nullable;

public class PortableGridBlockEntityRendererImpl<T extends AbstractPortableGridBlockEntity>
    extends AbstractPortableGridBlockEntityRenderer<T> {
    public PortableGridBlockEntityRendererImpl() {
        super(RenderTypes.DISK_LED);
    }

    @Override
    @Nullable
    protected Disk getDisk(final T blockEntity) {
        return blockEntity.getModelData().get(ForgePortableGridBlockEntity.DISK_PROPERTY);
    }
}
