package com.refinedmods.refinedstorage.common.storage;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.storage.StorageType;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

public final class StorageTypes {
    public static final StorageType ITEM = new SameTypeStorageType<>(
        ResourceCodecs.ITEM_CODEC,
        ItemResource.class::isInstance,
        ItemResource.class::cast,
        1,
        64
    );
    public static final StorageType FLUID = new SameTypeStorageType<>(
        ResourceCodecs.FLUID_CODEC,
        FluidResource.class::isInstance,
        FluidResource.class::cast,
        Platform.INSTANCE.getBucketAmount(),
        Platform.INSTANCE.getBucketAmount() * 16
    );

    private StorageTypes() {
    }
}
