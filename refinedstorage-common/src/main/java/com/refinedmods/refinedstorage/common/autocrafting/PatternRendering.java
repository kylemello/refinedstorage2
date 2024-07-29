package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.autocrafting.CraftingPattern;
import com.refinedmods.refinedstorage.common.api.autocrafting.PatternProviderItem;
import com.refinedmods.refinedstorage.common.api.autocrafting.ProcessingPattern;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.util.PlatformUtil;

import java.util.Optional;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class PatternRendering {
    private PatternRendering() {
    }

    public static boolean canDisplayOutput(final ItemStack stack) {
        return stack.getItem() instanceof PatternProviderItem && Screen.hasShiftDown();
    }

    public static Optional<ItemStack> getOutput(final ItemStack stack) {
        final Level level = PlatformUtil.getClientLevel();
        if (level == null) {
            return Optional.empty();
        }
        return RefinedStorageApi.INSTANCE.getPattern(stack, level).map(pattern -> {
            if (pattern instanceof CraftingPattern craftingPattern
                && craftingPattern.output().getResource() instanceof ItemResource itemResource) {
                return itemResource.toItemStack();
            }
            if (pattern instanceof ProcessingPattern processingPattern
                && processingPattern.outputs().size() == 1
                && processingPattern.outputs().getFirst().getResource() instanceof ItemResource itemResource) {
                return itemResource.toItemStack();
            }
            return null;
        });
    }
}
