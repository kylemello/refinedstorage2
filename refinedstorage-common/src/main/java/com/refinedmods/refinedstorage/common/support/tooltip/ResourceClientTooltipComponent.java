package com.refinedmods.refinedstorage.common.support.tooltip;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceRendering;

import java.util.Objects;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;

public class ResourceClientTooltipComponent implements ClientTooltipComponent {
    private final ResourceAmount resourceAmount;
    private final Component name;

    public ResourceClientTooltipComponent(final ResourceAmount resourceAmount) {
        this.resourceAmount = resourceAmount;
        this.name = getNameWithAmount(resourceAmount);
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public int getWidth(final Font font) {
        return 16 + 4 + font.width(name);
    }

    @Override
    public void renderImage(final Font font, final int x, final int y, final GuiGraphics graphics) {
        RefinedStorageApi.INSTANCE.getResourceRendering(resourceAmount.resource()).render(
            resourceAmount.resource(),
            graphics,
            x,
            y
        );
        graphics.drawString(
            font,
            name,
            x + 16 + 4,
            y + 4,
            Objects.requireNonNullElse(ChatFormatting.GRAY.getColor(), 11184810)
        );
    }

    private static Component getNameWithAmount(final ResourceAmount resourceAmount) {
        final ResourceRendering rendering = RefinedStorageApi.INSTANCE.getResourceRendering(
            resourceAmount.resource()
        );
        final String amount = rendering.getDisplayedAmount(resourceAmount.amount(), true);
        final Component displayName = rendering.getDisplayName(resourceAmount.resource());
        if (amount.isEmpty()) {
            return displayName;
        }
        return displayName.copy().append(" (").append(amount).append(")");
    }
}
