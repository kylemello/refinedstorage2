package com.refinedmods.refinedstorage.common.support.network;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.list.ResourceList;
import com.refinedmods.refinedstorage.api.resource.list.ResourceListImpl;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.Comparator;
import java.util.function.Function;
import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class ResourceSorters {
    private ResourceSorters() {
    }

    public static Comparator<ResourceKey> create(@Nullable final Network network, final Inventory playerInventory) {
        return create(network, playerInventory, Function.identity());
    }

    public static <T> Comparator<T> create(@Nullable final Network network,
                                           final Inventory playerInventory,
                                           final Function<T, ResourceKey> resourceExtractor) {
        final ResourceList available = ResourceListImpl.create();
        addNetworkItemsIntoList(network, available);
        addPlayerInventoryItemsIntoList(playerInventory, available);
        return sortByHighestAvailableFirst(available, resourceExtractor);
    }

    private static void addNetworkItemsIntoList(@Nullable final Network network, final ResourceList list) {
        if (network != null) {
            network.getComponent(StorageNetworkComponent.class).getAll().forEach(list::add);
        }
    }

    private static void addPlayerInventoryItemsIntoList(final Inventory playerInventory, final ResourceList list) {
        for (int i = 0; i < playerInventory.getContainerSize(); ++i) {
            final ItemStack playerInventoryStack = playerInventory.getItem(i);
            if (playerInventoryStack.isEmpty()) {
                continue;
            }
            list.add(ItemResource.ofItemStack(playerInventoryStack), playerInventoryStack.getCount());
        }
    }

    private static <T> Comparator<T> sortByHighestAvailableFirst(
        final ResourceList available,
        final Function<T, ResourceKey> resourceExtractor
    ) {
        return Comparator.<T>comparingLong(obj -> available.get(resourceExtractor.apply(obj))).reversed();
    }
}
