package com.refinedmods.refinedstorage.common.support.tooltip;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

public final class SmallText {
    private SmallText() {
    }

    public static float getScale() {
        return isSmall() ? 0.7F : 1F;
    }

    public static boolean isSmall() {
        return !Minecraft.getInstance().isEnforceUnicode();
    }

    public static void render(final Font font,
                              final FormattedCharSequence text,
                              final int x,
                              final int y,
                              final Matrix4f pose,
                              final MultiBufferSource.BufferSource buffer) {
        render(font, text, x, y, -1, pose, buffer);
    }

    public static void render(final Font font,
                              final FormattedCharSequence text,
                              final int x,
                              final int y,
                              final int color,
                              final Matrix4f pose,
                              final MultiBufferSource.BufferSource buffer) {
        final float scale = getScale();
        final Matrix4f scaled = new Matrix4f(pose);
        scaled.scale(scale, scale, 1);
        font.drawInBatch(
            text,
            x / scale,
            (y / scale) + 1,
            color,
            true,
            scaled,
            buffer,
            Font.DisplayMode.NORMAL,
            0,
            15728880
        );
    }
}
