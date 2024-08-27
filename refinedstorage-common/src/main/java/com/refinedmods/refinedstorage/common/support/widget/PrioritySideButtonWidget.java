package com.refinedmods.refinedstorage.common.support.widget;

import com.refinedmods.refinedstorage.common.support.amount.PriorityScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class PrioritySideButtonWidget extends AbstractSideButtonWidget {
    private static final MutableComponent TITLE = createTranslation("gui", "priority");
    private static final Component STORAGE_HELP = createTranslation("gui", "priority.storage_help");
    private static final Component CRAFTER_HELP = createTranslation("gui", "priority.crafter_help");
    private static final ResourceLocation SPRITE = createIdentifier("widget/side_button/priority");

    private final ClientProperty<Integer> property;
    private final Component helpText;

    private PrioritySideButtonWidget(final ClientProperty<Integer> property,
                                     final Inventory playerInventory,
                                     final Screen parent,
                                     final Component helpText) {
        super(createPressAction(property, playerInventory, parent));
        this.property = property;
        this.helpText = helpText;
    }

    public static PrioritySideButtonWidget forStorage(final ClientProperty<Integer> property,
                                                      final Inventory playerInventory,
                                                      final Screen parent) {
        return new PrioritySideButtonWidget(property, playerInventory, parent, STORAGE_HELP);
    }

    public static PrioritySideButtonWidget forCrafter(final ClientProperty<Integer> property,
                                                      final Inventory playerInventory,
                                                      final Screen parent) {
        return new PrioritySideButtonWidget(property, playerInventory, parent, CRAFTER_HELP);
    }

    private static OnPress createPressAction(final ClientProperty<Integer> property,
                                             final Inventory playerInventory,
                                             final Screen parent) {
        return btn -> Minecraft.getInstance().setScreen(new PriorityScreen(property, parent, playerInventory));
    }

    @Override
    protected ResourceLocation getSprite() {
        return SPRITE;
    }

    @Override
    protected MutableComponent getTitle() {
        return TITLE;
    }

    @Override
    protected MutableComponent getSubText() {
        return Component.literal(String.valueOf(property.getValue()));
    }

    @Override
    protected Component getHelpText() {
        return helpText;
    }
}
