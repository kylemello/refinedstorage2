package com.refinedmods.refinedstorage.common.exporter;

import com.refinedmods.refinedstorage.api.network.impl.node.exporter.ExporterTransferStrategyImpl;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;
import com.refinedmods.refinedstorage.common.api.storage.root.FuzzyRootStorage;

import java.util.Collection;

public class FuzzyExporterTransferStrategy extends ExporterTransferStrategyImpl {
    public FuzzyExporterTransferStrategy(final InsertableStorage destination, final long transferQuota) {
        super(destination, transferQuota);
    }

    @Override
    protected Collection<ResourceKey> expand(final ResourceKey resource, final RootStorage rootStorage) {
        if (rootStorage instanceof FuzzyRootStorage fuzzyRootStorage) {
            return fuzzyRootStorage.getFuzzy(resource);
        }
        return super.expand(resource, rootStorage);
    }
}
