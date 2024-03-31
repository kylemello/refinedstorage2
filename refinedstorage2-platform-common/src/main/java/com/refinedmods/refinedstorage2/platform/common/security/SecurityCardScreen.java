package com.refinedmods.refinedstorage2.platform.common.security;

import javax.annotation.Nullable;

import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

public class SecurityCardScreen extends AbstractSecurityCardScreen<SecurityCardContainerMenu> {
    private static final int BOUND_PLAYER_BUTTON_RIGHT_PADDING = 6;
    private static final int BOUND_PLAYER_BUTTON_WIDTH = 80;
    private static final MutableComponent UNBOUND_TITLE = Component.literal("<")
        .append(createTranslation("gui", "security_card.unbound"))
        .append(">");

    public SecurityCardScreen(final SecurityCardContainerMenu menu,
                              final Inventory playerInventory,
                              final Component text) {
        super(menu, playerInventory, text);
    }

    @Override
    protected void init(final int rows) {
        super.init(rows);
        final Component boundToText = menu.getBoundTo() == null
            ? UNBOUND_TITLE
            : Component.literal(menu.getBoundTo().name());
        final Button boundPlayerButton = Button.builder(boundToText, this::toggleBoundPlayer)
            .pos(leftPos + imageWidth - BOUND_PLAYER_BUTTON_RIGHT_PADDING - BOUND_PLAYER_BUTTON_WIDTH, topPos + 4)
            .size(BOUND_PLAYER_BUTTON_WIDTH, 14)
            .build();
        addRenderableWidget(boundPlayerButton);
    }

    private void toggleBoundPlayer(final Button button) {
        if (menu.getPlayers().isEmpty()) {
            return;
        }
        if (menu.getBoundTo() == null) {
            setBoundPlayer(button, menu.getPlayers().get(0));
            return;
        }
        final int nextIndex = menu.getPlayers().indexOf(menu.getBoundTo()) + 1;
        if (nextIndex >= menu.getPlayers().size()) {
            setBoundPlayer(button, null);
        } else {
            setBoundPlayer(button, menu.getPlayers().get(nextIndex));
        }
    }

    private void setBoundPlayer(final Button button, @Nullable final SecurityCardContainerMenu.Player player) {
        menu.changeBoundPlayer(player);
        button.setMessage(player == null ? UNBOUND_TITLE : Component.literal(player.name()));
    }
}
