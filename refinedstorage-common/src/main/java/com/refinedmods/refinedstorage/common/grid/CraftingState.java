package com.refinedmods.refinedstorage.common.grid;

import com.refinedmods.refinedstorage.api.core.NullableType;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.support.CraftingMatrix;
import com.refinedmods.refinedstorage.common.util.ContainerUtil;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class CraftingState {
    private static final String TAG_CRAFTING_MATRIX = "matrix";

    private final Runnable listener;
    private final Supplier<@NullableType Level> levelSupplier;
    private final CraftingMatrix craftingMatrix = new CraftingMatrix(this::craftingMatrixChanged, 3, 3);
    private final ResultContainer craftingResult = new ResultContainer();

    @Nullable
    private CraftingRecipe currentRecipe;

    public CraftingState(final Runnable listener, final Supplier<@NullableType Level> levelSupplier) {
        this.listener = listener;
        this.levelSupplier = levelSupplier;
    }

    private void craftingMatrixChanged() {
        final Level level = levelSupplier.get();
        if (level == null) {
            return;
        }
        updateResult(level);
        listener.run();
    }

    public void updateResult(final Level level) {
        if (level.isClientSide()) {
            return;
        }
        final CraftingInput input = craftingMatrix.asCraftInput();
        if (currentRecipe == null || !currentRecipe.matches(input, level)) {
            currentRecipe = loadRecipe(level);
        }
        if (currentRecipe == null) {
            setResult(ItemStack.EMPTY);
        } else {
            setResult(currentRecipe.assemble(input, level.registryAccess()));
        }
    }

    public CraftingMatrix getCraftingMatrix() {
        return craftingMatrix;
    }

    public ResultContainer getCraftingResult() {
        return craftingResult;
    }

    public boolean hasCraftingResult() {
        return !craftingResult.getItem(0).isEmpty();
    }

    private void setResult(final ItemStack result) {
        craftingResult.setItem(0, result);
    }

    @Nullable
    private CraftingRecipe loadRecipe(final Level level) {
        return level
            .getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, craftingMatrix.asCraftInput(), level)
            .map(RecipeHolder::value)
            .orElse(null);
    }

    NonNullList<ItemStack> getRemainingItems(@Nullable final Level level,
                                             final Player player,
                                             final CraftingInput input) {
        if (level == null || currentRecipe == null) {
            return NonNullList.create();
        }
        return Platform.INSTANCE.getRemainingCraftingItems(player, currentRecipe, input);
    }

    public void writeToTag(final CompoundTag tag, final HolderLookup.Provider provider) {
        tag.put(TAG_CRAFTING_MATRIX, ContainerUtil.write(craftingMatrix, provider));
    }

    public void readFromTag(final CompoundTag tag, final HolderLookup.Provider provider) {
        if (tag.contains(TAG_CRAFTING_MATRIX)) {
            ContainerUtil.read(tag.getCompound(TAG_CRAFTING_MATRIX), craftingMatrix, provider);
        }
    }
}
