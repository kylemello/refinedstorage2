package com.refinedmods.refinedstorage.api.network.impl.node.exporter;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.node.exporter.ExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.api.storage.TransferHelper;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;

import java.util.Collection;
import java.util.Collections;

public class ExporterTransferStrategyImpl implements ExporterTransferStrategy {
    private final InsertableStorage destination;
    private final long transferQuota;

    public ExporterTransferStrategyImpl(final InsertableStorage destination, final long transferQuota) {
        this.destination = destination;
        this.transferQuota = transferQuota;
    }

    /**
     * @param resource    the resource to expand
     * @param rootStorage the storage belonging to the resource
     * @return the list of expanded resources, will be tried out in the order of the list. Can be empty.
     */
    protected Collection<ResourceKey> expand(final ResourceKey resource, final RootStorage rootStorage) {
        return Collections.singletonList(resource);
    }

    @Override
    public boolean transfer(final ResourceKey resource, final Actor actor, final Network network) {
        final RootStorage rootStorage = network.getComponent(StorageNetworkComponent.class);
        final Collection<ResourceKey> expanded = expand(resource, rootStorage);
        return tryTransferExpanded(actor, rootStorage, expanded);
    }

    private boolean tryTransferExpanded(final Actor actor,
                                        final RootStorage rootStorage,
                                        final Collection<ResourceKey> expanded) {
        for (final ResourceKey resource : expanded) {
            if (tryTransfer(actor, rootStorage, resource)) {
                return true;
            }
        }
        return false;
    }

    private boolean tryTransfer(final Actor actor, final RootStorage rootStorage, final ResourceKey resource) {
        final long transferred = TransferHelper.transfer(
            resource,
            transferQuota,
            actor,
            rootStorage,
            destination,
            rootStorage
        );
        return transferred > 0;
    }
}
