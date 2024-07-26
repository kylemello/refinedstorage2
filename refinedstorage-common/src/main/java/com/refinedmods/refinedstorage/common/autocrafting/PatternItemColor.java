package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.Platform;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;

public class PatternItemColor implements ItemColor {
    @Override
    public int getColor(final ItemStack stack, final int tintIndex) {
        if (PatternRendering.canDisplayOutput(stack)) {
            return PatternRendering.getOutput(stack).map(
                output -> Platform.INSTANCE.getItemColor(output, tintIndex)
            ).orElse(-1);
        }
        return -1;
    }
}
