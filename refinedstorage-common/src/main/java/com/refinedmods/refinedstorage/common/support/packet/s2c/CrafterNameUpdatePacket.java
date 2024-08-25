package com.refinedmods.refinedstorage.common.support.packet.s2c;

import com.refinedmods.refinedstorage.common.autocrafting.CrafterContainerMenu;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public record CrafterNameUpdatePacket(Component name) implements CustomPacketPayload {
    public static final Type<CrafterNameUpdatePacket> PACKET_TYPE = new Type<>(
        createIdentifier("crafter_name_update")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, CrafterNameUpdatePacket> STREAM_CODEC =
        StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, CrafterNameUpdatePacket::name,
            CrafterNameUpdatePacket::new
        );

    public static void handle(final CrafterNameUpdatePacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof CrafterContainerMenu containerMenu) {
            containerMenu.nameChanged(packet.name);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
