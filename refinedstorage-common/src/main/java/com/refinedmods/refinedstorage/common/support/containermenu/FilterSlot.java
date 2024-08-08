package com.refinedmods.refinedstorage.common.support.containermenu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class FilterSlot extends Slot {
    public FilterSlot(final Container container, final int index, final int x, final int y) {
        super(container, index, x, y);
    }

    @Override
    public void set(final ItemStack stack) {
        if (!stack.isEmpty()) {
            stack.setCount(1);
        }
        super.set(stack);
    }

    @Override
    public boolean mayPickup(final Player player) {
        return false;
    }
}
