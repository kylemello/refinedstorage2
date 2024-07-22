package com.refinedmods.refinedstorage.common.storage;

public interface StorageAccessor {
    long getStored();

    long getCapacity();

    double getProgress();

    boolean hasCapacity();
}
