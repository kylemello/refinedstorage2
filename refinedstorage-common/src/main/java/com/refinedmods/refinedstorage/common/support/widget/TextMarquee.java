package com.refinedmods.refinedstorage.common.support.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TextMarquee {
    private final Component text;
    private final int maxWidth;

    private int offset;
    private int ticks;
    private State state;

    public TextMarquee(final Component text, final int maxWidth) {
        this.text = text;
        this.maxWidth = maxWidth;
    }

    public int getEffectiveWidth(final Font font) {
        return Math.min(maxWidth, font.width(text));
    }

    public void render(final GuiGraphics graphics, final int x, final int y, final Font font, final boolean hovering) {
        if (!hovering) {
            offset = 0;
            state = State.MOVING_LEFT;
            ticks = 0;
        }
        final int width = font.width(text);
        if (width > maxWidth) {
            final int overflow = width - maxWidth;
            if (hovering) {
                updateMarquee(overflow);
            }
            graphics.enableScissor(x, y, x + maxWidth, y + font.lineHeight);
            graphics.drawString(font, text, x + offset, y, 4210752, false);
            graphics.disableScissor();
        } else {
            graphics.drawString(font, text, x, y, 4210752, false);
        }
    }

    private void updateMarquee(final int overflow) {
        ticks++;
        if (ticks % state.ticks == 0) {
            offset = state.updateOffset(offset);
            state = state.nextState(offset, overflow);
            ticks = 0;
        }
    }

    enum State {
        MOVING_LEFT(2),
        MOVING_RIGHT(2),
        PAUSE(30);

        private final int ticks;

        State(final int ticks) {
            this.ticks = ticks;
        }

        int updateOffset(final int currentOffset) {
            return switch (this) {
                case MOVING_LEFT -> currentOffset - 1;
                case MOVING_RIGHT -> currentOffset + 1;
                case PAUSE -> currentOffset;
            };
        }

        State nextState(final int currentOffset, final int overflow) {
            return switch (this) {
                case MOVING_LEFT -> currentOffset > -overflow ? MOVING_LEFT : PAUSE;
                case MOVING_RIGHT -> currentOffset < 0 ? MOVING_RIGHT : PAUSE;
                case PAUSE -> currentOffset < 0 ? MOVING_RIGHT : MOVING_LEFT;
            };
        }
    }
}
