package com.refinedmods.refinedstorage2.platform.common.support.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

// A custom checkbox so we can change the font color.
public class CustomCheckboxWidget extends AbstractButton {
    private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE = new ResourceLocation(
        "widget/checkbox_selected_highlighted"
    );
    private static final ResourceLocation CHECKBOX_SELECTED_SPRITE = new ResourceLocation(
        "widget/checkbox_selected"
    );
    private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE = new ResourceLocation(
        "widget/checkbox_highlighted"
    );
    private static final ResourceLocation CHECKBOX_SPRITE = new ResourceLocation("widget/checkbox");

    private static final int BOX_SIZE = 9 + 8;

    private boolean selected;

    public CustomCheckboxWidget(final int x,
                                final int y,
                                final Component text,
                                final Font font,
                                final boolean selected) {
        super(x, y, BOX_SIZE + 4 + font.width(text), BOX_SIZE, text);
        this.selected = selected;
    }

    public void onPress() {
        this.selected = !this.selected;
    }

    public boolean selected() {
        return this.selected;
    }

    public void updateWidgetNarration(final NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, this.createNarrationMessage());
        if (this.active) {
            if (this.isFocused()) {
                output.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.focused"));
            } else {
                output.add(NarratedElementType.USAGE, Component.translatable("narration.checkbox.usage.hovered"));
            }
        }
    }

    public void renderWidget(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        final Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.enableDepthTest();
        final Font font = minecraft.font;
        graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        final ResourceLocation sprite;
        if (selected) {
            sprite = isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
        } else {
            sprite = isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;
        }
        final int x = getX() + BOX_SIZE + 4;
        final int y = (getY() + (height >> 1)) - (9 >> 1);
        graphics.blitSprite(sprite, getX(), getY(), BOX_SIZE, BOX_SIZE);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.drawString(font, getMessage(), x, y, 4210752, false);
    }
}
