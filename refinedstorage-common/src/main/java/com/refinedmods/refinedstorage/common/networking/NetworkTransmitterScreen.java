package com.refinedmods.refinedstorage.common.networking;

import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.RedstoneModeSideButtonWidget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage.common.support.TextureIds.WARNING;
import static com.refinedmods.refinedstorage.common.support.TextureIds.WARNING_SIZE;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public class NetworkTransmitterScreen extends AbstractBaseScreen<NetworkTransmitterContainerMenu> {
    private static final ResourceLocation TEXTURE = createIdentifier("textures/gui/network_transmitter.png");

    public NetworkTransmitterScreen(final NetworkTransmitterContainerMenu menu,
                                    final Inventory playerInventory,
                                    final Component text) {
        super(menu, playerInventory, text);
        this.inventoryLabelY = 42;
        this.imageWidth = 176;
        this.imageHeight = 137;
    }

    @Override
    protected void init() {
        super.init();
        addSideButton(new RedstoneModeSideButtonWidget(getMenu().getProperty(PropertyTypes.REDSTONE_MODE)));
    }

    @Override
    protected void renderLabels(final GuiGraphics graphics, final int mouseX, final int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        final NetworkTransmitterData status = getMenu().getStatus();
        final int displayTextX = 51;
        if (status.error()) {
            graphics.blitSprite(WARNING, displayTextX, 23, WARNING_SIZE, WARNING_SIZE);
        }
        graphics.drawString(font, status.message(), displayTextX + (status.error() ? (10 + 4) : 0), 25, 4210752, false);
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
