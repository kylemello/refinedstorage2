package com.refinedmods.refinedstorage.common.support;

import net.minecraft.world.item.BlockItem;

@FunctionalInterface
public interface BlockItemProvider<T extends BlockItem> {
    T createBlockItem();
}
