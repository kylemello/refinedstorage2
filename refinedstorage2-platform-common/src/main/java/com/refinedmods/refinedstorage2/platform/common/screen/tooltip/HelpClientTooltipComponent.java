package com.refinedmods.refinedstorage2.platform.common.screen.tooltip;

import com.refinedmods.refinedstorage2.platform.common.screen.TextureIds;

import java.util.List;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.joml.Matrix4f;

public class HelpClientTooltipComponent extends SmallTextClientTooltipComponent {
    private static final Style STYLE = Style.EMPTY.withColor(0xFF129ED9);

    public HelpClientTooltipComponent(final List<MutableComponent> components) {
        super(components);
        components.forEach(c -> c.setStyle(STYLE));
    }

    @Override
    public int getHeight() {
        return Math.max(24, super.getHeight());
    }

    @Override
    public int getWidth(final Font font) {
        return super.getWidth(font) + 20 + 4;
    }

    @Override
    public void renderText(final Font font,
                           final int x,
                           final int y,
                           final Matrix4f pose,
                           final MultiBufferSource.BufferSource buffer) {
        super.renderText(font, x + 20 + 4, y + 4, pose, buffer);
    }

    @Override
    public void renderImage(final Font font, final int x, final int y, final GuiGraphics graphics) {
        graphics.blit(TextureIds.ICONS, x, y + 2, 236, 158, 20, 20);
    }
}
