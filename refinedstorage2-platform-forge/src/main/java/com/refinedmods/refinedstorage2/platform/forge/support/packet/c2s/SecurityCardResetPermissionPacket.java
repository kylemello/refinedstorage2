package com.refinedmods.refinedstorage2.platform.forge.support.packet.c2s;

import com.refinedmods.refinedstorage2.platform.common.security.AbstractSecurityCardContainerMenu;
import com.refinedmods.refinedstorage2.platform.common.support.packet.PacketIds;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record SecurityCardResetPermissionPacket(ResourceLocation permissionId) implements CustomPacketPayload {
    public static SecurityCardResetPermissionPacket decode(final FriendlyByteBuf buf) {
        return new SecurityCardResetPermissionPacket(buf.readResourceLocation());
    }

    public static void handle(final SecurityCardResetPermissionPacket packet, final PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            if (player.containerMenu instanceof AbstractSecurityCardContainerMenu securityCardContainerMenu) {
                securityCardContainerMenu.resetPermission(packet.permissionId);
            }
        }));
    }

    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeResourceLocation(permissionId);
    }

    @Override
    public ResourceLocation id() {
        return PacketIds.SECURITY_CARD_RESET_PERMISSION;
    }
}
