package com.refinedmods.refinedstorage.common.detector;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.impl.node.detector.AbstractDetectorAmountStrategy;
import com.refinedmods.refinedstorage.api.network.impl.node.detector.DetectorAmountStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;
import com.refinedmods.refinedstorage.common.api.storage.root.FuzzyRootStorage;

class FuzzyDetectorAmountStrategy extends AbstractDetectorAmountStrategy {
    private final DetectorAmountStrategy fallback;

    FuzzyDetectorAmountStrategy(final DetectorAmountStrategy fallback) {
        this.fallback = fallback;
    }

    @Override
    public long getAmount(final Network network, final ResourceKey configuredResource) {
        final RootStorage rootStorage = getRootStorage(network);
        if (!(rootStorage instanceof FuzzyRootStorage fuzzyRootStorage)) {
            return fallback.getAmount(network, configuredResource);
        }
        return fuzzyRootStorage.getFuzzy(configuredResource)
            .stream()
            .flatMap(resource -> rootStorage.get(resource).stream())
            .mapToLong(ResourceAmount::getAmount)
            .sum();
    }
}
