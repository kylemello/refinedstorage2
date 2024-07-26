package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.autocrafting.CraftingPattern;
import com.refinedmods.refinedstorage.common.api.autocrafting.PatternProviderItem;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.Optional;

import net.minecraft.client.Minecraft;
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
        final Level level = Minecraft.getInstance().level;
        if (level == null) {
            return Optional.empty();
        }
        return RefinedStorageApi.INSTANCE.getPattern(stack, level)
            .filter(CraftingPattern.class::isInstance)
            .map(CraftingPattern.class::cast)
            .map(CraftingPattern::output)
            .map(ResourceAmount::getResource)
            .filter(ItemResource.class::isInstance)
            .map(ItemResource.class::cast)
            .map(ItemResource::toItemStack);
    }
}
