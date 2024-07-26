package com.refinedmods.refinedstorage.common.storagemonitor;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.TransferHelper;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.storagemonitor.StorageMonitorExtractionStrategy;
import com.refinedmods.refinedstorage.common.storage.BucketPlayerInventoryInsertableStorage;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;

import net.minecraft.world.entity.player.Player;

public class FluidStorageMonitorExtractionStrategy implements StorageMonitorExtractionStrategy {
    @Override
    public boolean extract(final ResourceKey resource,
                           final boolean fullStack,
                           final Player player,
                           final Actor actor,
                           final Network network) {
        if (!(resource instanceof FluidResource fluidResource)) {
            return false;
        }
        final BucketPlayerInventoryInsertableStorage target = new BucketPlayerInventoryInsertableStorage(
            player.getInventory(),
            network.getComponent(StorageNetworkComponent.class),
            true
        );
        final RootStorage source = network.getComponent(StorageNetworkComponent.class);
        return TransferHelper.transfer(
            fluidResource,
            Platform.INSTANCE.getBucketAmount(),
            actor,
            source,
            target,
            source
        ) > 0;
    }
}
