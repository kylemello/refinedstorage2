package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.autocrafting.PatternProviderItem;
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
        final Screen screen = Minecraft.getInstance().screen;
        if (screen instanceof PatternGridScreen patternGridScreen) {
            return patternGridScreen.getMenu().getPatternOutputSlot() != null
                && patternGridScreen.getMenu().getPatternOutputSlot().getItem() == stack;
        }
        if (screen instanceof CrafterScreen crafterScreen) {
            return crafterScreen.getMenu().containsPattern(stack);
        }
        return false;
    }

    public static Optional<ItemStack> getOutput(final ItemStack stack) {
        final Level level = ClientPlatformUtil.getClientLevel();
        if (level == null) {
            return Optional.empty();
        }
        return RefinedStorageApi.INSTANCE.getPattern(stack, level).map(pattern -> switch (pattern) {
            case CraftingPattern craftingPattern
                when craftingPattern.output().resource() instanceof ItemResource itemResource ->
                itemResource.toItemStack();
            case ProcessingPattern processingPattern
                when processingPattern.outputs().size() == 1
                && processingPattern.outputs().getFirst().resource() instanceof ItemResource itemResource ->
                itemResource.toItemStack();
            case StonecutterPattern stonecutterPattern
                when stonecutterPattern.output() instanceof ItemResource itemResource -> itemResource.toItemStack();
            case SmithingTablePattern smithingTablePattern
                when smithingTablePattern.output() instanceof ItemResource itemResource -> itemResource.toItemStack();
            default -> null;
        });
    }
}
