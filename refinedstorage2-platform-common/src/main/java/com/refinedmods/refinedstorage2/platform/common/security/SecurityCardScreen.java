package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.common.support.stretching.AbstractStretchingScreen;
import com.refinedmods.refinedstorage2.platform.common.support.widget.CustomCheckboxWidget;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

public class SecurityCardScreen extends AbstractStretchingScreen<SecurityCardContainerMenu> {
    private static final ResourceLocation TEXTURE = createIdentifier("textures/gui/security_card.png");

    private static final int BINDING_RIGHT_PADDING = 6;
    private static final int BINDING_WIDTH = 80;
    private static final MutableComponent UNBOUND_TITLE = Component.literal("<")
        .append(createTranslation("gui", "security_card.unbound"))
        .append(">");

    private final List<CustomCheckboxWidget> checkboxes = new ArrayList<>();

    public SecurityCardScreen(final SecurityCardContainerMenu menu,
                              final Inventory playerInventory,
                              final Component text) {
        super(menu, playerInventory, text);
        this.inventoryLabelY = 97;
        this.imageWidth = 193;
        this.imageHeight = 176;
    }

    @Override
    protected void init() {
        super.init();

        checkboxes.clear();
        final List<SecurityCardContainerMenu.Permission> permissions = getMenu().getPermissions();
        for (int i = 0; i < permissions.size(); ++i) {
            final CustomCheckboxWidget checkbox = createPermissionCheckbox(permissions.get(i), i);
            addWidget(checkbox);
            checkboxes.add(checkbox);
        }
        updateScrollbar(checkboxes.size());

        final Button playerButton = Button.builder(UNBOUND_TITLE, btn -> toggleBoundPlayer())
            .pos(leftPos + imageWidth - BINDING_RIGHT_PADDING - BINDING_WIDTH, topPos + 4)
            .size(BINDING_WIDTH, 14)
            .build();
        addRenderableWidget(playerButton);
    }

    private CustomCheckboxWidget createPermissionCheckbox(
        final SecurityCardContainerMenu.Permission permission,
        final int index
    ) {
        final CustomCheckboxWidget checkbox = new CustomCheckboxWidget(
            leftPos + 10,
            getPermissionCheckboxY(index),
            permission.name(),
            font,
            false
        );
        checkbox.setTooltip(getPermissionTooltip(permission));
        return checkbox;
    }

    private Tooltip getPermissionTooltip(final SecurityCardContainerMenu.Permission permission) {
        final MutableComponent ownerName = permission.ownerName().copy().withStyle(
            Style.EMPTY.withItalic(true).withColor(ChatFormatting.GRAY)
        );
        return Tooltip.create(permission.description().copy().append("\n").append(ownerName));
    }

    private int getPermissionCheckboxY(final int index) {
        return topPos + 19 + (index * ROW_SIZE) + 3;
    }

    @Override
    protected int getScrollPanePadding() {
        return 4;
    }

    private void toggleBoundPlayer() {
        // todo!
    }

    @Override
    protected void scrollbarChanged(final int rows) {
        final int offset = getScrollbarOffset();
        for (int i = 0; i < checkboxes.size(); ++i) {
            final CustomCheckboxWidget checkbox = checkboxes.get(i);
            final int y = getPermissionCheckboxY(i) - offset;
            checkbox.visible = y >= (topPos + 19 - ROW_SIZE) && y < (topPos + 19 + (rows * ROW_SIZE));
            checkbox.setY(y);
        }
    }

    @Override
    protected void renderRows(final GuiGraphics graphics,
                              final int x,
                              final int y,
                              final int topHeight,
                              final int rows,
                              final int mouseX,
                              final int mouseY) {
        for (final CustomCheckboxWidget checkbox : checkboxes) {
            checkbox.render(graphics, mouseX, mouseY, 0);
        }
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
