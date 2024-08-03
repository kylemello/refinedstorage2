package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceRendering;
import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;

import java.util.List;
import java.util.Optional;

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

public class ProcessingPatternClientTooltipComponent implements ClientTooltipComponent {
    private static final int ARROW_SPACING = 8;

    private final ProcessingPatternState state;
    private final int rows;
    private final List<Component> outputs;

    public ProcessingPatternClientTooltipComponent(final ProcessingPatternState state) {
        this.state = state;
        this.rows = calculateMaxRows(state);
        this.outputs = getOutputText(state);
    }

    private static int calculateMaxRows(final ProcessingPatternState state) {
        int lastFilledInputIndex = 0;
        for (int i = 0; i < state.inputs().size(); i++) {
            if (state.inputs().get(i).isPresent()) {
                lastFilledInputIndex = i;
            }
        }
        int lastFilledOutputIndex = 0;
        for (int i = 0; i < state.outputs().size(); i++) {
            if (state.outputs().get(i).isPresent()) {
                lastFilledOutputIndex = i;
            }
        }
        final int lastFilledInputRow = Math.ceilDiv(lastFilledInputIndex + 1, 3);
        final int lastFilledOutputRow = Math.ceilDiv(lastFilledOutputIndex + 1, 3);
        return Math.max(lastFilledInputRow, lastFilledOutputRow);
    }

    private static List<Component> getOutputText(final ProcessingPatternState state) {
        return state.getFlatOutputs()
            .stream()
            .map(ProcessingPatternClientTooltipComponent::getOutputText)
            .toList();
    }

    private static Component getOutputText(final ResourceAmount resourceAmount) {
        final ResourceRendering rendering = RefinedStorageApi.INSTANCE.getResourceRendering(
            resourceAmount.resource()
        );
        final String displayAmount = rendering.getDisplayedAmount(
            resourceAmount.amount(),
            false
        );
        return Component.literal(String.format("%sx ", displayAmount))
            .append(rendering.getDisplayName(resourceAmount.resource()))
            .withStyle(ChatFormatting.GRAY);
    }

    @Override
    public int getHeight() {
        return (outputs.size() * 9) + 2 + (rows * 18) + 3;
    }

    @Override
    public int getWidth(final Font font) {
        return (18 * 3) + ARROW_SPACING + LIGHT_ARROW_WIDTH + ARROW_SPACING + (18 * 3);
    }

    @Override
    public void renderImage(final Font font, final int x, final int y, final GuiGraphics graphics) {
        renderOutputText(font, x, y, graphics);
        final int matrixSlotsY = y + (outputs.size() * 9) + 2;
        renderMatrixSlots(x, matrixSlotsY, true, graphics);
        graphics.blitSprite(
            LIGHT_ARROW,
            x + (18 * 3) + ARROW_SPACING,
            y + (outputs.size() * 9) + 2 + ((rows * 18) / 2) - (LIGHT_ARROW_HEIGHT / 2),
            LIGHT_ARROW_WIDTH,
            LIGHT_ARROW_HEIGHT
        );
        renderMatrixSlots(x, matrixSlotsY, false, graphics);
    }

    private void renderMatrixSlots(final int x,
                                   final int y,
                                   final boolean input,
                                   final GuiGraphics graphics) {
        final List<Optional<ResourceAmount>> slots = input ? state.inputs() : state.outputs();
        for (int row = 0; row < rows; ++row) {
            for (int column = 0; column < 3; ++column) {
                final int slotXOffset = !input ? ((18 * 3) + ARROW_SPACING + LIGHT_ARROW_WIDTH + ARROW_SPACING) : 0;
                final int slotX = x + slotXOffset + column * 18;
                final int slotY = y + row * 18;
                final int idx = row * 3 + column;
                if (idx >= slots.size()) {
                    break;
                }
                graphics.blitSprite(SLOT, slotX, slotY, 18, 18);
                slots.get(idx).ifPresent(resourceAmount -> {
                    final ResourceRendering rendering = RefinedStorageApi.INSTANCE.getResourceRendering(
                        resourceAmount.resource()
                    );
                    rendering.render(resourceAmount.resource(), graphics, slotX + 1, slotY + 1);
                    AbstractBaseScreen.renderResourceAmount(graphics, slotX + 1, slotY + 1, resourceAmount.amount(),
                        rendering);
                });
            }
        }
    }

    private void renderOutputText(final Font font, final int x, final int y, final GuiGraphics graphics) {
        for (int i = 0; i < outputs.size(); ++i) {
            graphics.drawString(
                font,
                outputs.get(i),
                x,
                y + (i * 9),
                requireNonNullElse(ChatFormatting.GRAY.getColor(), 15)
            );
        }
    }
}
