package com.refinedmods.refinedstorage.common.support.packet.c2s;

import com.refinedmods.refinedstorage.common.autocrafting.CrafterContainerMenu;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public record CrafterNameChangePacket(String name) implements CustomPacketPayload {
    public static final Type<CrafterNameChangePacket> PACKET_TYPE = new Type<>(
        createIdentifier("crafter_name_change")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, CrafterNameChangePacket> STREAM_CODEC = StreamCodec
        .composite(
            ByteBufCodecs.STRING_UTF8, CrafterNameChangePacket::name,
            CrafterNameChangePacket::new
        );

    public static void handle(final CrafterNameChangePacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof CrafterContainerMenu containerMenu) {
            containerMenu.changeName(packet.name);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
