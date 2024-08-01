package com.refinedmods.refinedstorage.common.support;

import net.minecraft.resources.ResourceLocation;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public final class TextureIds {
    public static final ResourceLocation SIDE_BUTTON_ICONS = createIdentifier("textures/side_button_icons.png");
    public static final ResourceLocation LIGHT_ARROW = createIdentifier("light_arrow");
    public static final ResourceLocation SLOT = createIdentifier("slot");
    public static final int LIGHT_ARROW_WIDTH = 22;
    public static final int LIGHT_ARROW_HEIGHT = 15;
    public static final ResourceLocation WARNING = createIdentifier("warning");
    public static final int WARNING_SIZE = 10;

    private TextureIds() {
    }
}
