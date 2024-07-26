package com.refinedmods.refinedstorage.common.grid;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.list.ResourceList;
import com.refinedmods.refinedstorage.api.resource.list.ResourceListImpl;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;
import com.refinedmods.refinedstorage.common.api.storage.PlayerActor;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

class SnapshotCraftingGridRefillContext implements CraftingGridRefillContext {
    private final PlayerActor playerActor;
    private final CraftingGridBlockEntity blockEntity;
    private final ResourceList available = new ResourceListImpl();
    private final ResourceList used = new ResourceListImpl();

    SnapshotCraftingGridRefillContext(
        final Player player,
        final CraftingGridBlockEntity blockEntity,
        final CraftingMatrix craftingMatrix
    ) {
        this.playerActor = new PlayerActor(player);
        this.blockEntity = blockEntity;
        addAvailableItems(craftingMatrix);
    }

    private void addAvailableItems(final CraftingMatrix craftingMatrix) {
        blockEntity.getRootStorage().ifPresent(rootStorage -> {
            for (int i = 0; i < craftingMatrix.getContainerSize(); ++i) {
                addAvailableItem(craftingMatrix, rootStorage, i);
            }
        });
    }

    private void addAvailableItem(final CraftingMatrix craftingMatrix,
                                  final RootStorage rootStorage,
                                  final int craftingMatrixSlotIndex) {
        final ItemStack craftingMatrixStack = craftingMatrix.getItem(craftingMatrixSlotIndex);
        if (craftingMatrixStack.isEmpty()) {
            return;
        }
        addAvailableItem(rootStorage, craftingMatrixStack);
    }

    private void addAvailableItem(final RootStorage rootStorage,
                                  final ItemStack craftingMatrixStack) {
        final ItemResource craftingMatrixResource = ItemResource.ofItemStack(craftingMatrixStack);
        // a single resource can occur multiple times in a recipe, only add it once
        if (available.get(craftingMatrixResource).isEmpty()) {
            rootStorage.get(craftingMatrixResource).ifPresent(available::add);
        }
    }

    @Override
    public boolean extract(final ItemResource resource, final Player player) {
        return blockEntity.getNetwork().map(network -> {
            final boolean isAvailable = available.get(resource).isPresent();
            if (isAvailable) {
                available.remove(resource, 1);
                used.add(resource, 1);
            }
            return isAvailable;
        }).orElse(false);
    }

    @Override
    public void close() {
        blockEntity.getRootStorage().ifPresent(this::extractUsedItems);
    }

    private void extractUsedItems(final RootStorage rootStorage) {
        used.getAll().forEach(u -> rootStorage.extract(u.getResource(), u.getAmount(), Action.EXECUTE, playerActor));
    }
}
