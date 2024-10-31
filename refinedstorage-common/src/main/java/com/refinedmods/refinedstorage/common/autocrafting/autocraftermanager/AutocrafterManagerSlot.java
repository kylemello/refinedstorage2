package com.refinedmods.refinedstorage.common.autocrafting.autocraftermanager;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

class AutocrafterManagerSlot extends Slot {
    private final int originalY;
    private final int startY;
    private final int endY;
    private final boolean active;

    AutocrafterManagerSlot(final Container container,
                           final int slot,
                           final int x,
                           final int y,
                           final int startY,
                           final int endY,
                           final boolean active) {
        super(container, slot, x, y);
        this.originalY = y;
        this.startY = startY;
        this.endY = endY;
        this.active = active;
    }

    int getOriginalY() {
        return originalY;
    }

    @Override
    public boolean isActive() {
        return y >= startY && y < endY && active;
    }

    @Override
    public boolean isHighlightable() {
        return false; // we render the highlight in the scissor render
    }
}
