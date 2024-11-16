package com.refinedmods.refinedstorage.common.support.packet.c2s;

import com.refinedmods.refinedstorage.api.autocrafting.preview.PreviewProvider;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;
import com.refinedmods.refinedstorage.common.support.packet.s2c.S2CPackets;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

import java.util.UUID;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public record AutocraftingRequestPacket(UUID id,
                                        PlatformResourceKey resource,
                                        long amount) implements CustomPacketPayload {
    public static final Type<AutocraftingRequestPacket> PACKET_TYPE = new Type<>(
        createIdentifier("autocrafting_request")
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AutocraftingRequestPacket> STREAM_CODEC =
        StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, AutocraftingRequestPacket::id,
            ResourceCodecs.STREAM_CODEC, AutocraftingRequestPacket::resource,
            ByteBufCodecs.VAR_LONG, AutocraftingRequestPacket::amount,
            AutocraftingRequestPacket::new
        );

    public static void handle(final AutocraftingRequestPacket packet, final PacketContext ctx) {
        if (ctx.getPlayer().containerMenu instanceof PreviewProvider provider) {
            final boolean started = provider.startTask(packet.resource(), packet.amount());
            S2CPackets.sendAutocraftingResponse((ServerPlayer) ctx.getPlayer(), packet.id, started);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}
