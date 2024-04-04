package com.refinedmods.refinedstorage2.platform.fabric.packet.c2s;

import com.refinedmods.refinedstorage2.platform.common.security.AbstractSecurityCardContainerMenu;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class SecurityCardResetPermissionPacket implements ServerPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(final MinecraftServer server,
                        final ServerPlayer player,
                        final ServerGamePacketListenerImpl handler,
                        final FriendlyByteBuf buf,
                        final PacketSender responseSender) {
        final ResourceLocation permissionId = buf.readResourceLocation();
        if (player.containerMenu instanceof AbstractSecurityCardContainerMenu securityCardContainerMenu) {
            server.execute(() -> securityCardContainerMenu.resetPermission(permissionId));
        }
    }
}
