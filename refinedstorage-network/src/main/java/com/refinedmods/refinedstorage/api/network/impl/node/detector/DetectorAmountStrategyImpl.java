package com.refinedmods.refinedstorage.api.network.impl.node.detector;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

public class DetectorAmountStrategyImpl extends AbstractDetectorAmountStrategy {
    @Override
    public long getAmount(final Network network, final ResourceKey configuredResource) {
        return getRootStorage(network).get(configuredResource);
    }
}
