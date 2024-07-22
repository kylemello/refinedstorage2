package com.refinedmods.refinedstorage.api.network.impl.node.detector;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;

public abstract class AbstractDetectorAmountStrategy implements DetectorAmountStrategy {
    protected RootStorage getRootStorage(final Network network) {
        return network.getComponent(StorageNetworkComponent.class);
    }
}
