package com.refinedmods.refinedstorage.common.grid;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.support.RecipeMatrixContainer;
import com.refinedmods.refinedstorage.common.support.network.ResourceSorters;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.Comparator;
import java.util.List;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

class CraftingGridImpl implements CraftingGrid {
    private final CraftingGridBlockEntity blockEntity;

    CraftingGridImpl(final CraftingGridBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public RecipeMatrixContainer getCraftingMatrix() {
        return blockEntity.getCraftingMatrix();
    }

    @Override
    public ResultContainer getCraftingResult() {
        return blockEntity.getCraftingResult();
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(final Player player, final CraftingInput input) {
        return blockEntity.getRemainingItems(player, input);
    }

    @Override
    public CraftingGridRefillContext openRefillContext() {
        return new CraftingGridRefillContextImpl(blockEntity);
    }

    @Override
    public CraftingGridRefillContext openSnapshotRefillContext(final Player player) {
        return new SnapshotCraftingGridRefillContext(player, blockEntity, blockEntity.getCraftingMatrix());
    }

    @Override
    public void acceptQuickCraft(final Player player, final ItemStack craftedStack) {
        if (player.getInventory().add(craftedStack)) {
            return;
        }
        final ItemStack remainder = blockEntity.insert(craftedStack, player);
        if (!remainder.isEmpty()) {
            player.drop(remainder, false);
        }
    }

    @Override
    public boolean clearMatrix(final Player player, final boolean toPlayerInventory) {
        boolean success = true;
        for (int i = 0; i < getCraftingMatrix().getContainerSize(); ++i) {
            final ItemStack matrixStack = getCraftingMatrix().getItem(i);
            if (matrixStack.isEmpty()) {
                continue;
            }
            if (toPlayerInventory) {
                if (player.getInventory().add(matrixStack)) {
                    getCraftingMatrix().setItem(i, ItemStack.EMPTY);
                } else {
                    success = false;
                }
            } else {
                final ItemStack remainder = blockEntity.insert(matrixStack, player);
                if (!remainder.isEmpty()) {
                    success = false;
                }
                getCraftingMatrix().setItem(i, remainder);
            }
        }
        return success;
    }

    @Override
    public void transferRecipe(final Player player, final List<List<ItemResource>> recipe) {
        final boolean clearToPlayerInventory = blockEntity.getNetwork().isEmpty();
        if (!clearMatrix(player, clearToPlayerInventory)) {
            return;
        }
        final Comparator<ResourceKey> sorter = ResourceSorters.create(
            blockEntity.getNetwork().orElse(null),
            player.getInventory()
        );
        for (int i = 0; i < getCraftingMatrix().getContainerSize(); ++i) {
            if (i >= recipe.size()) {
                break;
            }
            final List<ItemResource> possibilities = recipe.get(i);
            possibilities.sort(sorter);
            doTransferRecipe(i, possibilities, player);
        }
    }

    private void doTransferRecipe(final int index, final List<ItemResource> sortedPossibilities, final Player player) {
        for (final ItemResource possibility : sortedPossibilities) {
            boolean extracted = blockEntity.extract(possibility, player) == 1;
            if (!extracted) {
                extracted = extractFromPlayerInventory(player, possibility);
            }
            if (extracted) {
                getCraftingMatrix().setItem(index, possibility.toItemStack());
                return;
            }
        }
    }

    private boolean extractFromPlayerInventory(final Player player, final ItemResource possibility) {
        final ItemStack possibilityStack = possibility.toItemStack();
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            final ItemStack playerStack = player.getInventory().getItem(i);
            if (ItemStack.isSameItemSameComponents(playerStack, possibilityStack)) {
                player.getInventory().removeItem(i, 1);
                return true;
            }
        }
        return false;
    }
}
