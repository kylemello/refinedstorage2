package com.refinedmods.refinedstorage.platform.common.support.packet.c2s;

import com.refinedmods.refinedstorage.platform.api.support.slotreference.SlotReference;
import com.refinedmods.refinedstorage.platform.api.support.slotreference.SlotReferenceFactory;
import com.refinedmods.refinedstorage.platform.api.support.slotreference.SlotReferenceHandlerItem;
import com.refinedmods.refinedstorage.platform.common.support.packet.PacketContext;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static com.refinedmods.refinedstorage.platform.common.util.IdentifierUtil.createIdentifier;

public record UseSlotReferencedItemPacket(SlotReference slotReference) implements CustomPacketPayload {
    public static final Type<UseSlotReferencedItemPacket> PACKET_TYPE = new Type<>(
        createIdentifier("use_slot_referenced_item")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, UseSlotReferencedItemPacket> STREAM_CODEC = StreamCodec
        .composite(
            SlotReferenceFactory.STREAM_CODEC, UseSlotReferencedItemPacket::slotReference,
            UseSlotReferencedItemPacket::new
        );

    public static void handle(final UseSlotReferencedItemPacket packet, final PacketContext ctx) {
        final Player player = ctx.getPlayer();
        packet.slotReference.resolve(player).ifPresent(stack -> {
            if (!(stack.getItem() instanceof SlotReferenceHandlerItem handlerItem)) {
                return;
            }
            handlerItem.use((ServerPlayer) player, stack, packet.slotReference);
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
