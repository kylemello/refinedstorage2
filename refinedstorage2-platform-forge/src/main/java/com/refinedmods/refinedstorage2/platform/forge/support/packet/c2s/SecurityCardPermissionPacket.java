package com.refinedmods.refinedstorage2.platform.forge.support.packet.c2s;

import com.refinedmods.refinedstorage2.platform.common.security.AbstractSecurityCardContainerMenu;
import com.refinedmods.refinedstorage2.platform.common.support.packet.PacketIds;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record SecurityCardPermissionPacket(ResourceLocation permissionId, boolean allowed)
    implements CustomPacketPayload {
    public static SecurityCardPermissionPacket decode(final FriendlyByteBuf buf) {
        return new SecurityCardPermissionPacket(buf.readResourceLocation(), buf.readBoolean());
    }

    public static void handle(final SecurityCardPermissionPacket packet, final PlayPayloadContext ctx) {
        ctx.player().ifPresent(player -> ctx.workHandler().submitAsync(() -> {
            if (player.containerMenu instanceof AbstractSecurityCardContainerMenu securityCardContainerMenu) {
                securityCardContainerMenu.setPermission(packet.permissionId, packet.allowed);
            }
        }));
    }

    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeResourceLocation(permissionId);
        buf.writeBoolean(allowed);
    }

    @Override
    public ResourceLocation id() {
        return PacketIds.SECURITY_CARD_PERMISSION;
    }
}
