package com.refinedmods.refinedstorage.common.storage.portablegrid;

import com.refinedmods.refinedstorage.api.storage.StateTrackedStorage;
import com.refinedmods.refinedstorage.api.storage.StorageState;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;
import com.refinedmods.refinedstorage.api.storage.root.RootStorageImpl;

class PortableGridStorage {
    private final RootStorage rootStorage;
    private final StateTrackedStorage diskStorage;

    PortableGridStorage(final StateTrackedStorage diskStorage) {
        this.rootStorage = new RootStorageImpl();
        this.diskStorage = diskStorage;
        this.rootStorage.addSource(diskStorage);
    }

    StorageState getState() {
        return diskStorage.getState();
    }

    RootStorage getRootStorage() {
        return rootStorage;
    }
}
