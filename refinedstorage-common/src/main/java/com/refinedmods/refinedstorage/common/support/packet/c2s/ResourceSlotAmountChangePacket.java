package com.refinedmods.refinedstorage.common.support.packet.c2s;

import com.refinedmods.refinedstorage.common.support.containermenu.AbstractResourceContainerMenu;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public record ResourceSlotAmountChangePacket(int slotIndex, long amount) implements CustomPacketPayload {
    public static final Type<ResourceSlotAmountChangePacket> PACKET_TYPE = new Type<>(
        createIdentifier("resource_slot_amount_change")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceSlotAmountChangePacket> STREAM_CODEC = StreamCodec
        .composite(
            ByteBufCodecs.INT, ResourceSlotAmountChangePacket::slotIndex,
            ByteBufCodecs.VAR_LONG, ResourceSlotAmountChangePacket::amount,
            ResourceSlotAmountChangePacket::new
        );

    public static void handle(final ResourceSlotAmountChangePacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof AbstractResourceContainerMenu containerMenu) {
            containerMenu.handleResourceSlotAmountChange(packet.slotIndex, packet.amount);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
