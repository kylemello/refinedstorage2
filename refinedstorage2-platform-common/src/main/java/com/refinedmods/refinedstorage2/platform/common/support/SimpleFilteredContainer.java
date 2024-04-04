package com.refinedmods.refinedstorage2.platform.common.support;

import java.util.function.Predicate;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public final class SimpleFilteredContainer extends SimpleContainer {
    private final Predicate<ItemStack> filter;

    public SimpleFilteredContainer(final int size, final Predicate<ItemStack> filter) {
        super(size);
        this.filter = filter;
    }

    @Override
    public boolean canPlaceItem(final int slot, final ItemStack stack) {
        return filter.test(stack);
    }
}
