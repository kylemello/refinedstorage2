package com.refinedmods.refinedstorage.common.controller;

import com.refinedmods.refinedstorage.common.support.NetworkNodeBlockItem;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class CreativeControllerBlockItem extends NetworkNodeBlockItem {
    private static final MutableComponent HELP = createTranslation("item", "creative_controller.help");

    CreativeControllerBlockItem(final Block block) {
        super(block, new Item.Properties().stacksTo(1), HELP);
    }
}
