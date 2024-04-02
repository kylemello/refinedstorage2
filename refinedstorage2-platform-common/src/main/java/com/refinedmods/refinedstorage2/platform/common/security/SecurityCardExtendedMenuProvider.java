package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.api.network.security.SecurityPolicy;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.content.ContentNames;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

class SecurityCardExtendedMenuProvider extends AbstractSecurityCardExtendedMenuProvider {
    private final SlotReference slotReference;
    private final UUID boundPlayerId;
    private final String boundPlayerName;

    SecurityCardExtendedMenuProvider(final SlotReference slotReference,
                                     final SecurityPolicy securityPolicy,
                                     final Set<PlatformPermission> dirtyPermissions,
                                     final UUID boundPlayerId,
                                     final String boundPlayerName) {
        super(slotReference, securityPolicy, dirtyPermissions);
        this.slotReference = slotReference;
        this.boundPlayerId = boundPlayerId;
        this.boundPlayerName = boundPlayerName;
    }

    @Override
    public void writeScreenOpeningData(final ServerPlayer player, final FriendlyByteBuf buf) {
        super.writeScreenOpeningData(player, buf);

        buf.writeUUID(boundPlayerId);
        buf.writeUtf(boundPlayerName);

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
