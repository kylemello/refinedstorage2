package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.content.ContentNames;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

class SecurityCardExtendedMenuProvider extends AbstractSecurityCardExtendedMenuProvider {
    private final SlotReference slotReference;
    private final PlayerSecurityCardModel playerModel;

    SecurityCardExtendedMenuProvider(final SlotReference slotReference, final PlayerSecurityCardModel model) {
        super(slotReference, model);
        this.slotReference = slotReference;
        this.playerModel = model;
    }

    @Override
    public void writeScreenOpeningData(final ServerPlayer player, final FriendlyByteBuf buf) {
        super.writeScreenOpeningData(player, buf);

        final boolean bound = playerModel.getBoundPlayerId() != null && playerModel.getBoundPlayerName() != null;
        buf.writeBoolean(bound);
        if (bound) {
            buf.writeUUID(playerModel.getBoundPlayerId());
            buf.writeUtf(playerModel.getBoundPlayerName());
        }

        final List<ServerPlayer> players = player.getServer() == null
            ? Collections.emptyList()
            : player.getServer().getPlayerList().getPlayers();
        buf.writeInt(players.size());
        for (final ServerPlayer otherPlayer : players) {
            buf.writeUUID(otherPlayer.getUUID());
            buf.writeUtf(otherPlayer.getGameProfile().getName());
        }
    }

    @Override
    public Component getDisplayName() {
        return ContentNames.SECURITY_CARD;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new SecurityCardContainerMenu(syncId, inventory, slotReference);
    }
}
