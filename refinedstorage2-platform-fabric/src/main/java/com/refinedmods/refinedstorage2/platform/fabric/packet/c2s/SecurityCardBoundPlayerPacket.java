package com.refinedmods.refinedstorage2.platform.fabric.packet.c2s;

import com.refinedmods.refinedstorage2.platform.common.security.SecurityCardContainerMenu;

import java.util.UUID;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class SecurityCardBoundPlayerPacket implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(final MinecraftServer server,
                        final ServerPlayer player,
                        final ServerGamePacketListenerImpl handler,
                        final FriendlyByteBuf buf,
                        final PacketSender responseSender) {
        final boolean hasPlayer = buf.readBoolean();
        final UUID playerId = hasPlayer ? buf.readUUID() : null;
        if (player.containerMenu instanceof SecurityCardContainerMenu securityCardContainerMenu) {
            server.execute(() -> securityCardContainerMenu.setBoundPlayer(server, playerId));
        }
    }
}
