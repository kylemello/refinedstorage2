package com.refinedmods.refinedstorage2.platform.forge.support.packet.s2c;

import com.refinedmods.refinedstorage2.platform.common.support.packet.PacketIds;
import com.refinedmods.refinedstorage2.platform.common.util.SecurityToastHelper;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record NoPermissionPacket(Component component) implements CustomPacketPayload {
    public static NoPermissionPacket decode(final FriendlyByteBuf buf) {
        return new NoPermissionPacket(buf.readComponent());
    }

    public static void handle(final NoPermissionPacket packet, final PlayPayloadContext ctx) {
        ctx.workHandler().submitAsync(() -> SecurityToastHelper.addNoPermissionToast(packet.component));
    }

    @Override
    public void write(final FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeComponent(component);
    }

    @Override
    public ResourceLocation id() {
        return PacketIds.NO_PERMISSION;
    }
}
