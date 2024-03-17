package com.refinedmods.refinedstorage2.platform.common.security;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SecurityCardItem extends Item {
    public SecurityCardItem() {
        super(new Item.Properties().stacksTo(1));
    }

    boolean isActive(final ItemStack stack) {
        return false;
    }
}
