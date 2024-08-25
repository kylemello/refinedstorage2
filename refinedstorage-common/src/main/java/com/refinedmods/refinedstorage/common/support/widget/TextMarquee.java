package com.refinedmods.refinedstorage.common.support.widget;

import javax.annotation.Nullable;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TextMarquee {
    private static final int MAX_TOOLTIP_HIGHLIGHT_TICKS = 20;

    private Component text;
    @Nullable
    private Component tooltip;

    private final int maxWidth;

    private int offset;
    private int stateTicks;
    private State state = State.MOVING_LEFT;

    private int hoveringTicks;
    private boolean hoveringIn;

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
            stateTicks = 0;
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
        renderTooltipHighlight(graphics, x, y, font, hovering);
    }

    private void renderTooltipHighlight(final GuiGraphics graphics,
                                        final int x,
                                        final int y,
                                        final Font font,
                                        final boolean hovering) {
        if (hovering && tooltip != null) {
            renderTooltipHighlight(graphics, x, y, font);
        } else {
            hoveringTicks = 0;
            hoveringIn = true;
        }
    }

    private void renderTooltipHighlight(final GuiGraphics graphics, final int x, final int y, final Font font) {
        if (hoveringIn) {
            hoveringTicks++;
            if (hoveringTicks == MAX_TOOLTIP_HIGHLIGHT_TICKS) {
                hoveringIn = false;
            }
        } else {
            hoveringTicks--;
            if (hoveringTicks == 0) {
                hoveringIn = true;
            }
        }
        final int alpha = (int) (hoveringTicks * (63.0 / MAX_TOOLTIP_HIGHLIGHT_TICKS));
        final int color = (alpha << 24) | 0xFFFFFF;
        graphics.fill(x - 1, y - 1, x + getEffectiveWidth(font), y + font.lineHeight, color);
    }

    private void updateMarquee(final int overflow) {
        stateTicks++;
        if (stateTicks % state.ticks == 0) {
            offset = state.updateOffset(offset);
            state = state.nextState(offset, overflow);
            stateTicks = 0;
        }
    }

    public Component getText() {
        return text;
    }

    public void setText(final Component text) {
        this.text = text;
    }

    @Nullable
    public Component getTooltip() {
        return tooltip;
    }

    public void setTooltip(@Nullable final Component tooltip) {
        this.tooltip = tooltip;
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
