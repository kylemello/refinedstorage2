package com.refinedmods.refinedstorage.common.autocrafting.preview;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceRendering;
import com.refinedmods.refinedstorage.common.support.tooltip.SmallText;
import com.refinedmods.refinedstorage.common.support.widget.TextMarquee;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import static com.refinedmods.refinedstorage.common.autocrafting.preview.CraftingPreviewScreen.REQUEST_BUTTON_HEIGHT;
import static com.refinedmods.refinedstorage.common.autocrafting.preview.CraftingPreviewScreen.REQUEST_BUTTON_WIDTH;

class CraftingRequestButton extends AbstractButton {
    private final CraftingRequest request;
    private final TextMarquee text;
    private final Consumer<CraftingRequest> onPress;

    CraftingRequestButton(final int x,
                          final int y,
                          final CraftingRequest request,
                          final Consumer<CraftingRequest> onPress) {
        super(x, y, REQUEST_BUTTON_WIDTH, REQUEST_BUTTON_HEIGHT, Component.empty());
        this.request = request;
        final ResourceKey resource = request.getResource();
        final long normalizedAmount = resource instanceof PlatformResourceKey platformResourceKey
            ? platformResourceKey.getResourceType().normalizeAmount(request.getAmount())
            : 0;
        final ResourceRendering rendering = RefinedStorageApi.INSTANCE.getResourceRendering(resource.getClass());
        this.text = new TextMarquee(Component.literal(rendering.formatAmount(normalizedAmount, true))
            .append(" ")
            .append(rendering.getDisplayName(resource)),
            REQUEST_BUTTON_WIDTH - 16 - 4 - 4 - 4,
            0xFFFFFF,
            true,
            true);
        this.onPress = onPress;
    }

    CraftingRequest getRequest() {
        return request;
    }

    @Override
    protected void renderWidget(final GuiGraphics graphics,
                                final int mouseX,
                                final int mouseY,
                                final float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        final ResourceKey resource = request.getResource();
        final ResourceRendering rendering = RefinedStorageApi.INSTANCE.getResourceRendering(resource.getClass());
        rendering.render(resource, graphics, getX() + 3, getY() + 4);
        final int yOffset = SmallText.isSmall() ? 8 : 5;
        text.render(graphics, getX() + 3 + 16 + 3, getY() + yOffset, Minecraft.getInstance().font, isHovered);
    }

    @Override
    public void onPress() {
        onPress.accept(request);
    }

    @Override
    protected void updateWidgetNarration(final NarrationElementOutput narrationElementOutput) {
        // no op
    }
}
