package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.support.containermenu.ValidatedSlot;

import net.minecraft.world.Container;
import net.minecraft.world.level.Level;

class PatternSlot extends ValidatedSlot {
    PatternSlot(final Container container, final int index, final int x, final int y, final Level level) {
        super(container, index, x, y, stack -> AutocrafterBlockEntity.isValidPattern(stack, level));
    }
}
