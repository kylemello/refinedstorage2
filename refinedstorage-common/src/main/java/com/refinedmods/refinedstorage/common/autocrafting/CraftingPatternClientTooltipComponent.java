package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.autocrafting.CraftingPattern;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.List;
import java.util.Objects;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public class CraftingPatternClientTooltipComponent implements ClientTooltipComponent {
    private static final long CYCLE_MS = 1000;
    private static long cycleStart = 0;
    private static int currentCycle = 0;

    private static final int ARROW_SPACING = 8;

    private static final ResourceLocation SLOT = createIdentifier("slot");
    private static final ResourceLocation LARGE_SLOT = createIdentifier("large_slot");
    private static final ResourceLocation ARROW = createIdentifier("light_arrow");
    private static final int ARROW_WIDTH = 22;
    private static final int ARROW_HEIGHT = 15;
    private static final int LARGE_SLOT_WIDTH = 26;
    private static final int LARGE_SLOT_HEIGHT = 26;

    private final int width;
    private final int height;
    private final CraftingPattern craftingPattern;

    public CraftingPatternClientTooltipComponent(final int width,
                                                 final int height,
                                                 final CraftingPattern craftingPattern) {
        this.width = width;
        this.height = height;
        this.craftingPattern = craftingPattern;
    }

    @Override
    public int getHeight() {
        return 9 + 2 + height * 18 + 3;
    }

    @Override
    public int getWidth(final Font font) {
        return (width * 18) + ARROW_SPACING + ARROW_WIDTH + ARROW_SPACING + LARGE_SLOT_WIDTH;
    }

    @Override
    public void renderImage(final Font font, final int x, final int y, final GuiGraphics graphics) {
        final long now = System.currentTimeMillis();
        if (cycleStart == 0) {
            cycleStart = now;
        }
        if (now - cycleStart >= CYCLE_MS) {
            currentCycle++;
            cycleStart = now;
        }
        if (craftingPattern.output().getResource() instanceof ItemResource itemResource) {
            graphics.drawString(
                font,
                Component.literal(String.format("%dx ", craftingPattern.output().getAmount()))
                    .append(itemResource.toItemStack().getHoverName())
                    .withStyle(ChatFormatting.GRAY),
                x,
                y,
                Objects.requireNonNullElse(ChatFormatting.GRAY.getColor(), 15)
            );
        }
        renderInputSlots(x, y + 9 + 2, graphics);
        renderArrow(x, y + 9 + 2, graphics);
        renderResultSlot(font, x, y + 9 + 2, graphics);
    }

    private void renderInputSlots(final int x, final int y, final GuiGraphics graphics) {
        for (int sx = 0; sx < width; ++sx) {
            for (int sy = 0; sy < height; ++sy) {
                renderInputSlot(x, y, graphics, sx, sy);
            }
        }
    }

    private void renderInputSlot(final int x, final int y, final GuiGraphics graphics, final int sx, final int sy) {
        graphics.blitSprite(SLOT, x + sx * 18, y + sy * 18, 18, 18);
        final int index = sy * width + sx;
        final List<PlatformResourceKey> inputs = craftingPattern.inputs().get(index);
        if (!inputs.isEmpty()) {
            final int idx = currentCycle % inputs.size();
            final PlatformResourceKey resource = inputs.get(idx);
            RefinedStorageApi.INSTANCE.getResourceRendering(resource).render(
                resource,
                graphics,
                x + sx * 18 + 1,
                y + sy * 18 + 1
            );
        }
    }

    private void renderArrow(final int x, final int y, final GuiGraphics graphics) {
        graphics.blitSprite(
            ARROW,
            x + width * 18 + ARROW_SPACING,
            y + ((height * 18) / 2) - (ARROW_HEIGHT / 2),
            ARROW_WIDTH,
            ARROW_HEIGHT
        );
    }

    private void renderResultSlot(final Font font, final int x, final int y, final GuiGraphics graphics) {
        final int slotX = x + width * 18 + ARROW_SPACING + ARROW_WIDTH + ARROW_SPACING;
        final int slotY = y + ((height * 18) / 2) - (LARGE_SLOT_HEIGHT / 2);
        graphics.blitSprite(LARGE_SLOT, slotX, slotY, LARGE_SLOT_WIDTH, LARGE_SLOT_HEIGHT);
        final ResourceAmount output = craftingPattern.output();
        if (output.getResource() instanceof ItemResource itemResource) {
            final ItemStack resultItemStack = itemResource.toItemStack(output.getAmount());
            final int stackX = slotX + 5;
            final int stackY = slotY + 5;
            graphics.renderItem(resultItemStack, stackX, stackY);
            graphics.renderItemDecorations(font, resultItemStack, stackX, stackY);
        }
    }
}
