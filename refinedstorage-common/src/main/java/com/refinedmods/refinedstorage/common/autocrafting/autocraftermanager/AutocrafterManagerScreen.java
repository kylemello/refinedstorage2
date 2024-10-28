package com.refinedmods.refinedstorage.common.autocrafting.autocraftermanager;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.support.Sprites;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.stretching.AbstractStretchingScreen;
import com.refinedmods.refinedstorage.common.support.widget.History;
import com.refinedmods.refinedstorage.common.support.widget.RedstoneModeSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.widget.SearchFieldWidget;
import com.refinedmods.refinedstorage.common.support.widget.TextMarquee;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import static com.refinedmods.refinedstorage.common.support.Sprites.SEARCH_SIZE;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public class AutocrafterManagerScreen extends AbstractStretchingScreen<AutocrafterManagerContainerMenu> {
    private static final ResourceLocation TEXTURE = createIdentifier("textures/gui/autocrafter_manager.png");
    private static final List<String> SEARCH_FIELD_HISTORY = new ArrayList<>();
    private static final ResourceLocation AUTOCRAFTER_NAME = createIdentifier("autocrafter_manager/autocrafter_name");
    private static final int COLUMNS = 9;

    @Nullable
    private SearchFieldWidget searchField;

    public AutocrafterManagerScreen(final AutocrafterManagerContainerMenu menu,
                                    final Inventory playerInventory,
                                    final Component title) {
        super(menu, playerInventory, new TextMarquee(title, 70));
        this.inventoryLabelY = 75;
        this.imageWidth = 193;
        this.imageHeight = 176;
    }

    @Override
    protected void init(final int rows) {
        super.init(rows);

        if (searchField == null) {
            searchField = new SearchFieldWidget(
                font,
                leftPos + 94 + 1,
                topPos + 6 + 1,
                73 - 6,
                new History(SEARCH_FIELD_HISTORY)
            );
        } else {
            searchField.setX(leftPos + 94 + 1);
            searchField.setY(topPos + 6 + 1);
        }
        updateScrollbar();

        addWidget(searchField);

        addSideButton(new RedstoneModeSideButtonWidget(getMenu().getProperty(PropertyTypes.REDSTONE_MODE)));
    }

    private void updateScrollbar() {
        final int totalRows = menu.getViewItems()
            .stream()
            .map(AutocrafterManagerContainerMenu.Item::getRowsIncludingTitle)
            .reduce(0, Integer::sum);
        updateScrollbar(totalRows);
    }

    @Override
    protected void scrollbarChanged(final int rows) {
        super.scrollbarChanged(rows);
        final int scrollbarOffset = getScrollbarOffset();
        for (int i = 0; i < menu.getAutocrafterSlots().size(); ++i) {
            final AutocrafterManagerSlot slot = menu.getAutocrafterSlots().get(i);
            Platform.INSTANCE.setSlotY(slot, slot.getOriginalY() - scrollbarOffset);
        }
    }

    @Override
    protected void renderBg(final GuiGraphics graphics, final float delta, final int mouseX, final int mouseY) {
        super.renderBg(graphics, delta, mouseX, mouseY);
        graphics.blitSprite(Sprites.SEARCH, leftPos + 79, topPos + 5, SEARCH_SIZE, SEARCH_SIZE);
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (searchField != null) {
            searchField.render(graphics, 0, 0, 0);
        }
    }

    @Override
    public boolean charTyped(final char unknown1, final int unknown2) {
        return (searchField != null && searchField.charTyped(unknown1, unknown2))
            || super.charTyped(unknown1, unknown2);
    }

    @Override
    public boolean keyPressed(final int key, final int scanCode, final int modifiers) {
        if (searchField != null && searchField.keyPressed(key, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    protected void renderRows(final GuiGraphics graphics,
                              final int x,
                              final int y,
                              final int topHeight,
                              final int rows,
                              final int mouseX,
                              final int mouseY) {
        renderRowTitlesAndSlots(graphics, x, y, topHeight, rows);
        renderSlotContents(graphics, mouseX, mouseY, y, topHeight, rows);
    }

    private void renderRowTitlesAndSlots(final GuiGraphics graphics,
                                         final int x,
                                         final int y,
                                         final int topHeight,
                                         final int rows) {
        final int rowX = x + 7;
        int rowY = y + topHeight - getScrollbarOffset();
        for (final AutocrafterManagerContainerMenu.Item item : menu.getViewItems()) {
            if (!isOutOfFrame(y, topHeight, rows, rowY)) {
                graphics.blitSprite(AUTOCRAFTER_NAME, rowX, rowY, 162, ROW_SIZE);
                graphics.drawString(font, item.name(), rowX + 4, rowY + 6, 4210752, false);
            }
            for (int i = 0; i < item.slotCount(); i++) {
                final int slotX = rowX + ((i % COLUMNS) * 18);
                final int slotY = rowY + 18 + ((i / COLUMNS) * 18);
                if (!isOutOfFrame(y, topHeight, rows, slotY)) {
                    graphics.blitSprite(Sprites.SLOT, slotX, slotY, 18, 18);
                }
            }
            rowY += item.getRowsIncludingTitle() * ROW_SIZE;
        }
    }

    private void renderSlotContents(final GuiGraphics graphics,
                                    final int mouseX,
                                    final int mouseY,
                                    final int y,
                                    final int topHeight,
                                    final int rows) {
        graphics.pose().pushPose();
        graphics.pose().translate(leftPos, topPos, 0);
        for (final Slot slot : menu.getAutocrafterSlots()) {
            if (isOutOfFrame(y, topHeight, rows, topPos + slot.y)) {
                continue;
            }
            super.renderSlot(graphics, slot);
            final boolean hovering = mouseX >= slot.x + leftPos
                && mouseX < slot.x + leftPos + 16
                && mouseY >= slot.y + topPos
                && mouseY < slot.y + topPos + 16;
            if (hovering) {
                renderSlotHighlight(graphics, slot.x, slot.y, 0);
            }
        }
        graphics.pose().popPose();
    }

    @Override
    protected void renderSlot(final GuiGraphics guiGraphics, final Slot slot) {
        if (slot instanceof AutocrafterManagerSlot) {
            return;
        }
        super.renderSlot(guiGraphics, slot);
    }

    private static boolean isOutOfFrame(final int y,
                                        final int topHeight,
                                        final int rows,
                                        final int rowY) {
        return (rowY < y + topHeight - ROW_SIZE)
            || (rowY > y + topHeight + (ROW_SIZE * rows));
    }

    @Override
    protected void renderStretchingBackground(final GuiGraphics graphics, final int x, final int y, final int rows) {
        for (int row = 0; row < rows; ++row) {
            int textureY = 37;
            if (row == 0) {
                textureY = 19;
            } else if (row == rows - 1) {
                textureY = 55;
            }
            graphics.blit(getTexture(), x, y + (ROW_SIZE * row), 0, textureY, imageWidth, ROW_SIZE);
        }
    }

    @Override
    protected int getBottomHeight() {
        return 99;
    }

    @Override
    protected int getBottomV() {
        return 73;
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
