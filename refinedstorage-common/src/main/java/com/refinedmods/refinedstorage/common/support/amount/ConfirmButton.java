package com.refinedmods.refinedstorage.common.support.amount;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import static com.refinedmods.refinedstorage.common.support.Sprites.ERROR;
import static com.refinedmods.refinedstorage.common.support.Sprites.ERROR_SIZE;

public class ConfirmButton extends Button {
    private boolean error;

    public ConfirmButton(final int x,
                         final int y,
                         final int width,
                         final int height,
                         final Component message,
                         final OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(final GuiGraphics graphics,
                                final int mouseX,
                                final int mouseY,
                                final float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        if (error) {
            graphics.blitSprite(ERROR, getX() + 4, getY() + 4, ERROR_SIZE, ERROR_SIZE);
        }
    }

    @Override
    protected void renderScrollingString(final GuiGraphics graphics,
                                         final Font font,
                                         final int width,
                                         final int color) {
        final int offset = error ? (ERROR_SIZE - 6) : 0;
        final int start = offset + getX() + width;
        final int end = offset + getX() + getWidth() - width;
        renderScrollingString(graphics, font, getMessage(), start, getY(), end, getY() + getHeight(), color);
    }

    public void setError(final boolean error) {
        this.error = error;
    }
}
