package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.AbstractFilterScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.RedstoneModeSideButtonWidget;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslationAsHeading;

public class CrafterScreen extends AbstractBaseScreen<CrafterContainerMenu> {
    private static final ClientTooltipComponent EMPTY_PATTERN_SLOT = ClientTooltipComponent.create(
        createTranslationAsHeading("gui", "crafter.empty_pattern_slot").getVisualOrderText()
    );

    public CrafterScreen(final CrafterContainerMenu menu, final Inventory playerInventory, final Component title) {
        super(menu, playerInventory, title);
        this.inventoryLabelY = 42;
        this.imageWidth = 210;
        this.imageHeight = 137;
    }

    @Override
    protected void init() {
        super.init();
        addSideButton(new RedstoneModeSideButtonWidget(getMenu().getProperty(PropertyTypes.REDSTONE_MODE)));
    }

    @Override
    protected void renderTooltip(final GuiGraphics graphics, final int x, final int y) {
        if (hoveredSlot instanceof PatternSlot patternSlot
            && !patternSlot.hasItem()
            && getMenu().getCarried().isEmpty()) {
            Platform.INSTANCE.renderTooltip(graphics, List.of(EMPTY_PATTERN_SLOT), x, y);
            return;
        }
        super.renderTooltip(graphics, x, y);
    }

    @Override
    protected ResourceLocation getTexture() {
        return AbstractFilterScreen.TEXTURE;
    }
}
