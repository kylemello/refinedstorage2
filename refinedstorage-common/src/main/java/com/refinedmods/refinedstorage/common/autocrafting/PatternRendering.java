package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.autocrafting.PatternProviderItem;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterScreen;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridScreen;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.util.ClientPlatformUtil;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class PatternRendering {
    private PatternRendering() {
    }

    public static boolean canDisplayOutput(final ItemStack stack) {
        if (!(stack.getItem() instanceof PatternProviderItem)) {
            return false;
        }
        if (Screen.hasShiftDown()) {
            return true;
        }
        return canDisplayOutputInScreen(stack);
    }

    private static boolean canDisplayOutputInScreen(final ItemStack stack) {
        final Screen screen = Minecraft.getInstance().screen;
        return switch (screen) {
            case PatternGridScreen patternGridScreen -> patternGridScreen.getMenu().isPatternInOutput(stack);
            case AutocrafterScreen autocrafterScreen -> autocrafterScreen.getMenu().containsPattern(stack);
            case null, default -> false;
        };
    }

    public static Optional<ItemStack> getOutput(final ItemStack stack) {
        final Level level = ClientPlatformUtil.getClientLevel();
        if (level == null) {
            return Optional.empty();
        }
        return RefinedStorageApi.INSTANCE.getPattern(stack, level).map(pattern -> switch (pattern) {
            case CraftingPattern craftingPattern
                when craftingPattern.getOutput().resource() instanceof ItemResource itemResource ->
                itemResource.toItemStack();
            case ProcessingPattern processingPattern
                when processingPattern.getOutputs().size() == 1
                && processingPattern.getOutputs().getFirst().resource() instanceof ItemResource itemResource ->
                itemResource.toItemStack();
            case StonecutterPattern stonecutterPattern -> stonecutterPattern.getOutput().toItemStack();
            case SmithingTablePattern smithingTablePattern -> smithingTablePattern.getOutput().toItemStack();
            default -> null;
        });
    }
}
