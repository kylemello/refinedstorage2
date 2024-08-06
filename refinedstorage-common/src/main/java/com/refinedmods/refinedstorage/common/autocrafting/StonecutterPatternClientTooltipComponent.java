package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceRendering;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

import static com.refinedmods.refinedstorage.common.support.TextureIds.LIGHT_ARROW;
import static com.refinedmods.refinedstorage.common.support.TextureIds.LIGHT_ARROW_HEIGHT;
import static com.refinedmods.refinedstorage.common.support.TextureIds.LIGHT_ARROW_WIDTH;
import static com.refinedmods.refinedstorage.common.support.TextureIds.SLOT;
import static java.util.Objects.requireNonNullElse;

class StonecutterPatternClientTooltipComponent implements ClientTooltipComponent {
    private static final int ARROW_SPACING = 8;

    private final Component outputText;
    private final PlatformResourceKey input;
    private final PlatformResourceKey output;

    StonecutterPatternClientTooltipComponent(final PlatformResourceKey input, final PlatformResourceKey output) {
        this.outputText = getOutputText(output);
        this.input = input;
        this.output = output;
    }

    @Override
    public void renderImage(final Font font, final int x, final int y, final GuiGraphics graphics) {
        graphics.drawString(font, outputText, x, y, requireNonNullElse(ChatFormatting.GRAY.getColor(), 15));
        graphics.blitSprite(SLOT, x, y + 9 + 2, 18, 18);
        RefinedStorageApi.INSTANCE.getResourceRendering(input).render(input, graphics, x + 1, y + 9 + 2 + 1);
        graphics.blitSprite(
            LIGHT_ARROW,
            x + 18 + ARROW_SPACING,
            y + 9 + 2 + (18 / 2) - (LIGHT_ARROW_HEIGHT / 2),
            LIGHT_ARROW_WIDTH,
            LIGHT_ARROW_HEIGHT
        );
        graphics.blitSprite(
            VanillaConstants.STONECUTTER_RECIPE_SELECTED_SPRITE,
            x + 18 + ARROW_SPACING + LIGHT_ARROW_WIDTH + ARROW_SPACING,
            y + 9 + 2,
            16,
            18
        );
        RefinedStorageApi.INSTANCE.getResourceRendering(output).render(
            output,
            graphics,
            x + 18 + ARROW_SPACING + LIGHT_ARROW_WIDTH + ARROW_SPACING,
            y + 9 + 2 + 1
        );
    }

    private static Component getOutputText(final PlatformResourceKey resource) {
        final ResourceRendering rendering = RefinedStorageApi.INSTANCE.getResourceRendering(resource);
        return Component.literal("1x ")
            .append(rendering.getDisplayName(resource))
            .withStyle(ChatFormatting.GRAY);
    }

    @Override
    public int getHeight() {
        return 9 + 2 + 18 + 3;
    }

    @Override
    public int getWidth(final Font font) {
        return Math.max(
            font.width(outputText),
            18 + ARROW_SPACING + LIGHT_ARROW_WIDTH + ARROW_SPACING + 16
        );
    }
}
