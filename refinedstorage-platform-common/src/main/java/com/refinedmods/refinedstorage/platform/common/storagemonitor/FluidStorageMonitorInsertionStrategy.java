package com.refinedmods.refinedstorage.platform.common.storagemonitor;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.channel.StorageChannel;
import com.refinedmods.refinedstorage.platform.api.storagemonitor.StorageMonitorInsertionStrategy;
import com.refinedmods.refinedstorage.platform.api.support.resource.FluidOperationResult;
import com.refinedmods.refinedstorage.platform.common.Platform;
import com.refinedmods.refinedstorage.platform.common.support.resource.FluidResource;

import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;

public class FluidStorageMonitorInsertionStrategy implements StorageMonitorInsertionStrategy {
    @Override
    public Optional<ItemStack> insert(
        final ResourceKey configuredResource,
        final ItemStack stack,
        final Actor actor,
        final Network network
    ) {
        if (!(configuredResource instanceof FluidResource configuredFluidResource)) {
            return Optional.empty();
        }
        final StorageChannel fluidStorageChannel = network.getComponent(StorageNetworkComponent.class);
        return Platform.INSTANCE.drainContainer(stack)
            .map(extracted -> tryInsert(actor, configuredFluidResource, extracted, fluidStorageChannel))
            .map(extracted -> doInsert(actor, extracted, fluidStorageChannel));
    }

    @Nullable
    private FluidOperationResult tryInsert(final Actor actor,
                                           final FluidResource configuredResource,
                                           final FluidOperationResult result,
                                           final StorageChannel storageChannel) {
        if (!result.fluid().equals(configuredResource)) {
            return null;
        }
        final long insertedSimulated = storageChannel.insert(
            result.fluid(),
            result.amount(),
            Action.SIMULATE,
            actor
        );
        final boolean insertedSuccessfully = insertedSimulated == result.amount();
        return insertedSuccessfully ? result : null;
    }

    private ItemStack doInsert(final Actor actor,
                               final FluidOperationResult result,
                               final StorageChannel storageChannel) {
        storageChannel.insert(
            result.fluid(),
            result.amount(),
            Action.EXECUTE,
            actor
        );
        return result.container();
    }
}
