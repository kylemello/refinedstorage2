package com.refinedmods.refinedstorage.common.support.packet.s2c;

import com.refinedmods.refinedstorage.common.autocrafting.preview.AutocraftingPreview;
import com.refinedmods.refinedstorage.common.support.packet.PacketContext;
import com.refinedmods.refinedstorage.common.util.ClientPlatformUtil;

import java.util.UUID;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public record AutocraftingPreviewResponsePacket(UUID id, AutocraftingPreview preview) implements CustomPacketPayload {
    public static final Type<AutocraftingPreviewResponsePacket> PACKET_TYPE = new Type<>(
        createIdentifier("autocrafting_preview_response")
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, AutocraftingPreviewResponsePacket> STREAM_CODEC =
        StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, AutocraftingPreviewResponsePacket::id,
            AutocraftingPreview.STREAM_CODEC, AutocraftingPreviewResponsePacket::preview,
            AutocraftingPreviewResponsePacket::new
        );

    public static void handle(final AutocraftingPreviewResponsePacket packet) {
        ClientPlatformUtil.craftingPreviewReceived(packet.id, packet.preview);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }
}

