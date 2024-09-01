package com.refinedmods.refinedstorage.common.autocrafting.preview;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceRendering;
import com.refinedmods.refinedstorage.common.support.amount.AbstractAmountScreen;
import com.refinedmods.refinedstorage.common.support.amount.AmountScreenConfiguration;
import com.refinedmods.refinedstorage.common.support.amount.DoubleAmountOperations;
import com.refinedmods.refinedstorage.common.support.tooltip.SmallText;
import com.refinedmods.refinedstorage.common.support.widget.ScrollbarWidget;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector3f;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class CraftingPreviewScreen extends AbstractAmountScreen<CraftingPreviewContainerMenu, Double> {
    private static final ResourceLocation TEXTURE = createIdentifier("textures/gui/crafting_preview.png");
    private static final MutableComponent TITLE = Component.translatable("container.crafting");
    private static final MutableComponent START = createTranslation("gui", "crafting_preview.start");
    private static final MutableComponent MISSING_RESOURCES
        = createTranslation("gui", "crafting_preview.start.missing_resources");
    private static final ResourceLocation ROW = createIdentifier("crafting_preview/row");

    private static final int ROWS_VISIBLE = 4;
    private static final int COLUMNS = 3;
    private static final int PREVIEW_AREA_HEIGHT = 119;

    private static final int ROW_HEIGHT = 30;
    private static final int ROW_WIDTH = 221;

    @Nullable
    private ScrollbarWidget scrollbar;

    public CraftingPreviewScreen(final Screen parent, final Inventory playerInventory, final ResourceKey resource) {
        super(
            new CraftingPreviewContainerMenu(resource),
            parent,
            playerInventory,
            TITLE,
            AmountScreenConfiguration.AmountScreenConfigurationBuilder.<Double>create()
                .withInitialAmount(1D)
                .withIncrementsTop(1, 10, 64)
                .withIncrementsTopStartPosition(new Vector3f(80, 20, 0))
                .withIncrementsBottom(-1, -10, -64)
                .withIncrementsBottomStartPosition(new Vector3f(80, 71, 0))
                .withAmountFieldPosition(new Vector3f(77, 51, 0))
                .withActionButtonsStartPosition(new Vector3f(7, 222, 0))
                .withHorizontalActionButtons(true)
                .withMinAmount(1D)
                .withResetAmount(1D)
                .withConfirmButtonText(START)
                .build(),
            DoubleAmountOperations.INSTANCE
        );
        this.imageWidth = 254;
        this.imageHeight = 249;
    }

    @Override
    protected void init() {
        super.init();
        scrollbar = new ScrollbarWidget(
            leftPos + 235,
            topPos + 98,
            ScrollbarWidget.Type.NORMAL,
            PREVIEW_AREA_HEIGHT
        );
        scrollbar.setEnabled(false);
        if (confirmButton != null) {
            confirmButton.active = false;
        }
        updatePreview();
    }

    private void updatePreview() {
        if (scrollbar == null || confirmButton == null) {
            return;
        }
        final CraftingPreview preview = getMenu().getPreview();
        if (preview == null) {
            scrollbar.setEnabled(false);
            scrollbar.setMaxOffset(0);
            confirmButton.active = false;
            return;
        }
        final int items = getMenu().getPreview().items().size();
        final int rows = Math.ceilDiv(items, COLUMNS) - ROWS_VISIBLE;
        scrollbar.setMaxOffset(scrollbar.isSmoothScrolling() ? rows * ROW_HEIGHT : rows);
        scrollbar.setEnabled(rows > 0);
        confirmButton.active = !preview.missing();
        confirmButton.setTooltip(preview.missing() ? Tooltip.create(MISSING_RESOURCES) : null);
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (scrollbar != null) {
            scrollbar.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void renderBg(final GuiGraphics graphics, final float delta, final int mouseX, final int mouseY) {
        super.renderBg(graphics, delta, mouseX, mouseY);
        final CraftingPreview preview = getMenu().getPreview();
        if (preview == null || scrollbar == null) {
            return;
        }
        final int x = leftPos + 8;
        final int y = topPos + 98;
        graphics.enableScissor(x, y, x + 221, y + PREVIEW_AREA_HEIGHT);
        final List<CraftingPreviewItem> items = getMenu().getPreview().items();
        final int rows = Math.ceilDiv(items.size(), COLUMNS);
        for (int i = 0; i < rows; ++i) {
            final int scrollOffset = scrollbar.isSmoothScrolling()
                ? (int) scrollbar.getOffset()
                : (int) scrollbar.getOffset() * ROW_HEIGHT;
            final int yy = y + (i * ROW_HEIGHT) - scrollOffset;
            renderRow(graphics, x, yy, i, items, mouseX, mouseY);
        }
        graphics.disableScissor();
    }

    private void renderRow(final GuiGraphics graphics,
                           final int x,
                           final int y,
                           final int i,
                           final List<CraftingPreviewItem> items,
                           final double mouseX,
                           final double mouseY) {
        if (y <= topPos + 98 - ROW_HEIGHT || y > topPos + 98 + PREVIEW_AREA_HEIGHT) {
            return;
        }
        graphics.blitSprite(ROW, x, y, ROW_WIDTH, ROW_HEIGHT);
        for (int column = i * COLUMNS; column < Math.min(i * COLUMNS + COLUMNS, items.size()); ++column) {
            final CraftingPreviewItem item = items.get(column);
            final int xx = x + (column % COLUMNS) * 74;
            renderCell(graphics, xx, y, item, mouseX, mouseY);
        }
    }

    private void renderCell(final GuiGraphics graphics,
                            final int x,
                            final int y,
                            final CraftingPreviewItem item,
                            final double mouseX,
                            final double mouseY) {
        if (item.missing() > 0) {
            graphics.fill(x, y, x + 73, y + 29, 0xFFF2DEDE);
        }
        int xx = x + 2;
        final ResourceRendering rendering = RefinedStorageApi.INSTANCE.getResourceRendering(item.resource().getClass());
        int yy = y + 7;
        rendering.render(item.resource(), graphics, xx, yy);
        if (isHovering(xx - leftPos, yy - topPos, 16, 16, mouseX, mouseY)
            && isHoveringOverPreviewArea(mouseX, mouseY)) {
            setTooltipForNextRenderPass(rendering.getTooltip(item.resource()).stream()
                .map(Component::getVisualOrderText)
                .toList());
        }
        if (!SmallText.isSmall()) {
            yy -= 2;
        }
        xx += 16 + 3;
        if (item.missing() > 0) {
            renderCellText(graphics, "missing", rendering, xx, yy, item.missing());
            yy += 7;
        }
        if (item.available() > 0) {
            renderCellText(graphics, "available", rendering, xx, yy, item.available());
            yy += 7;
        }
        if (item.toCraft() > 0) {
            renderCellText(graphics, "to_craft", rendering, xx, yy, item.toCraft());
        }
    }

    private void renderCellText(final GuiGraphics graphics,
                                final String type,
                                final ResourceRendering rendering,
                                final int x,
                                final int y,
                                final long amount) {
        SmallText.render(
            graphics,
            font,
            createTranslation("gui", "crafting_preview." + type, rendering.formatAmount(amount, true))
                .getVisualOrderText(),
            x,
            y,
            0x404040,
            false
        );
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int clickedButton) {
        if (scrollbar != null && scrollbar.mouseClicked(mouseX, mouseY, clickedButton)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    @Override
    public void mouseMoved(final double mx, final double my) {
        if (scrollbar != null) {
            scrollbar.mouseMoved(mx, my);
        }
        super.mouseMoved(mx, my);
    }

    @Override
    public boolean mouseReleased(final double mx, final double my, final int button) {
        return (scrollbar != null && scrollbar.mouseReleased(mx, my, button))
            || super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(final double x, final double y, final double z, final double delta) {
        final boolean didScrollbar = scrollbar != null
            && isHoveringOverPreviewArea(x, y)
            && scrollbar.mouseScrolled(x, y, z, delta);
        return didScrollbar || super.mouseScrolled(x, y, z, delta);
    }

    private boolean isHoveringOverPreviewArea(final double x, final double y) {
        return isHovering(7, 97, 241, 121, x, y);
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    protected void onAmountFieldChanged() {
        if (amountField == null || confirmButton == null) {
            return;
        }
        confirmButton.active = false;
        final boolean valid = getAndValidateAmount().isPresent();
        amountField.setTextColor(valid ? 0xFFFFFF : 0xFF5555);
    }

    @Override
    protected boolean confirm(final Double amount) {
        return false;
    }
}
