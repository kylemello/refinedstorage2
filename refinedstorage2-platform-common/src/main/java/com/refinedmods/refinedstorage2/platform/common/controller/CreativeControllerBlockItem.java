package com.refinedmods.refinedstorage2.platform.common.controller;

import com.refinedmods.refinedstorage2.platform.common.support.NamedBlockItem;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

public class CreativeControllerBlockItem extends NamedBlockItem {
    private static final MutableComponent HELP = createTranslation("item", "creative_controller.help");

    CreativeControllerBlockItem(final Block block, final Component name) {
        super(block, new Item.Properties().stacksTo(1), name, HELP);
    }
}
