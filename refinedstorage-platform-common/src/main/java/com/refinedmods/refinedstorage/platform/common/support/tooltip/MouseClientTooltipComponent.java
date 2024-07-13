package com.refinedmods.refinedstorage.platform.common.support.tooltip;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.platform.api.PlatformApi;
import com.refinedmods.refinedstorage.platform.common.Platform;
import com.refinedmods.refinedstorage.platform.common.support.TextureIds;
import com.refinedmods.refinedstorage.platform.common.support.resource.FluidResource;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

public class MouseClientTooltipComponent implements ClientTooltipComponent {
    private static final int PADDING = 4;

    private final Type type;
    private final IconRenderer iconRenderer;
    @Nullable
    private final String amount;

    private MouseClientTooltipComponent(final Type type,
                                        final IconRenderer iconRenderer,
                                        @Nullable final String amount) {
        this.type = type;
        this.iconRenderer = iconRenderer;
        this.amount = amount;
    }

    public static ClientTooltipComponent fluid(final Type type,
                                               final FluidResource fluidResource,
                                               @Nullable final String amount) {
        return new MouseClientTooltipComponent(
            type,
            (graphics, x, y) -> Platform.INSTANCE.getFluidRenderer().render(
                graphics.pose(),
                x,
                y,
                fluidResource
            ),
            amount
        );
    }

    public static ClientTooltipComponent item(final Type type,
                                              final ItemStack stack,
                                              @Nullable final String amount) {
        return new MouseClientTooltipComponent(
            type,
            (graphics, x, y) -> graphics.renderItem(stack, x, y),
            amount
        );
    }

    public static ClientTooltipComponent itemWithDecorations(final Type type,
                                                             final ItemStack stack,
                                                             @Nullable final String amount) {
        return new MouseClientTooltipComponent(
            type,
            (graphics, x, y) -> {
                graphics.renderItem(stack, x, y);
                graphics.renderItemDecorations(Minecraft.getInstance().font, stack, x, y);
            },
            amount
        );
    }

    public static ClientTooltipComponent itemConversion(final Type type,
                                                        final ItemStack from,
                                                        final ItemStack to,
                                                        @Nullable final String amount) {
        return new MouseClientTooltipComponent(
            type,
            new IconRenderer() {
                @Override
                public void render(final GuiGraphics graphics, final int x, final int y) {
                    graphics.renderItem(from, x, y);
                    graphics.blit(TextureIds.ICONS, x + 16 + 2, y, 234, 133, 22, 15);
                    graphics.renderItem(to, x + 16 + 2 + 22 + 2, y);
                }

                @Override
                public int getWidth() {
                    return 16 + 2 + 22 + 2 + 16;
                }
            },
            amount
        );
    }

    public static ClientTooltipComponent resource(final Type type,
                                                  final ResourceKey resource,
                                                  @Nullable final String amount) {
        return new MouseClientTooltipComponent(
            type,
            (graphics, x, y) -> PlatformApi.INSTANCE.getResourceRendering(resource).render(resource, graphics, x, y),
            amount
        );
    }

    @Override
    public int getHeight() {
        return 18;
    }

    @Override
    public int getWidth(final Font font) {
        return 9 + PADDING + iconRenderer.getWidth();
    }

    @Override
    public void renderImage(final Font font, final int x, final int y, final GuiGraphics graphics) {
        graphics.blit(TextureIds.ICONS, x + type.leftPad, y, type.x, type.y, type.width, type.height);
        iconRenderer.render(graphics, x + 9 + PADDING, y);
        if (amount != null) {
            final PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(0.0F, 0.0F, 200.0F);
            graphics.drawString(font, amount, x + 9 + PADDING + 16 - font.width(amount), y + 9, 16777215, true);
            poseStack.popPose();
        }
    }

    private interface IconRenderer {
        void render(GuiGraphics graphics, int x, int y);

        default int getWidth() {
            return 16;
        }
    }

    public enum Type {
        LEFT(247, 0, 180, 9, 13),
        RIGHT(238, 2, 180, 9, 13);

        private final int x;
        private final int leftPad;
        private final int y;
        private final int width;
        private final int height;

        Type(final int x, final int leftPad, final int y, final int width, final int height) {
            this.x = x;
            this.leftPad = leftPad;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
}
