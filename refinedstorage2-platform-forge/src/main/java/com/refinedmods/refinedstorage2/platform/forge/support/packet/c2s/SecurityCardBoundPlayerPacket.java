package com.refinedmods.refinedstorage2.platform.forge.support.packet.c2s;

import com.refinedmods.refinedstorage2.platform.common.security.SecurityCardContainerMenu;
import com.refinedmods.refinedstorage2.platform.common.support.packet.PacketIds;

import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record SecurityCardBoundPlayerPacket(UUID playerId) implements CustomPacketPayload {
    public static SecurityCardBoundPlayerPacket decode(final FriendlyByteBuf buf) {
        return new SecurityCardBoundPlayerPacket(buf.readUUID());
    }

    public static void handle(final SecurityCardBoundPlayerPacket packet, final PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            if (player.getServer() == null) {
                return;
            }
            if (player.containerMenu instanceof SecurityCardContainerMenu securityCardContainerMenu) {
                securityCardContainerMenu.setBoundPlayer(player.getServer(), packet.playerId);
            }
        }));
    }

    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeUUID(playerId);
    }

    @Override
    public ResourceLocation id() {
        return PacketIds.SECURITY_CARD_BOUND_PLAYER;
    }
}
