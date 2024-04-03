package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.Platform;
import com.refinedmods.refinedstorage2.platform.common.content.Menus;
import com.refinedmods.refinedstorage2.platform.common.support.stretching.ScreenSizeListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class SecurityCardContainerMenu extends AbstractSecurityCardContainerMenu implements ScreenSizeListener {
    private final List<Player> players = new ArrayList<>();
    private Player boundTo;

    public SecurityCardContainerMenu(final int syncId,
                                     final Inventory playerInventory,
                                     final FriendlyByteBuf buf) {
        super(Menus.INSTANCE.getSecurityCard(), syncId, playerInventory, buf);
        this.boundTo = new Player(buf.readUUID(), buf.readUtf());
        final int amountOfPlayers = buf.readInt();
        for (int i = 0; i < amountOfPlayers; ++i) {
            final UUID id = buf.readUUID();
            final String name = buf.readUtf();
            players.add(new Player(id, name));
        }
    }

    SecurityCardContainerMenu(final int syncId, final Inventory playerInventory, final SlotReference disabledSlot) {
        super(Menus.INSTANCE.getSecurityCard(), syncId, playerInventory, disabledSlot);
        this.boundTo = new Player(UUID.randomUUID(), "");
    }

    List<Player> getPlayers() {
        return players;
    }

    Player getBoundTo() {
        return boundTo;
    }

    public void setBoundPlayer(final MinecraftServer server, final UUID playerId) {
        if (disabledSlot == null) {
            return;
        }
        disabledSlot.resolve(playerInventory.player).ifPresent(stack -> setBoundPlayer(server, playerId, stack));
    }

    private void setBoundPlayer(final MinecraftServer server, final UUID playerId, final ItemStack stack) {
        if (stack.getItem() instanceof SecurityCardItem securityCardItem) {
            final ServerPlayer player = server.getPlayerList().getPlayer(playerId);
            if (player == null) {
                return;
            }
            securityCardItem.setBoundPlayer(player, stack);
        }
    }

    void changeBoundPlayer(final Player player) {
        Platform.INSTANCE.getClientToServerCommunications().sendSecurityCardBoundPlayer(player.id());
        this.boundTo = player;
    }

    record Player(UUID id, String name) {
    }
}
