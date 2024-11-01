package com.refinedmods.refinedstorage.common.support.packet.s2c;

import com.refinedmods.refinedstorage.api.autocrafting.AutocraftingPreview;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.storage.tracked.TrackedResource;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.storage.StorageInfo;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.networking.NetworkTransmitterData;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class S2CPackets {
    private S2CPackets() {
    }

    public static void sendEnergyInfo(final ServerPlayer player, final long stored, final long capacity) {
        Platform.INSTANCE.sendPacketToClient(player, new EnergyInfoPacket(stored, capacity));
    }

    public static void sendWirelessTransmitterData(final ServerPlayer player, final int range, final boolean active) {
        Platform.INSTANCE.sendPacketToClient(player, new WirelessTransmitterDataPacket(range, active));
    }

    public static void sendGridActive(final ServerPlayer player, final boolean active) {
        Platform.INSTANCE.sendPacketToClient(player, new GridActivePacket(active));
    }

    public static void sendAutocrafterManagerActive(final ServerPlayer player, final boolean active) {
        Platform.INSTANCE.sendPacketToClient(player, new AutocrafterManagerActivePacket(active));
    }

    public static void sendGridUpdate(final ServerPlayer player,
                                      final PlatformResourceKey resource,
                                      final long change,
                                      @Nullable final TrackedResource trackedResource) {
        Platform.INSTANCE.sendPacketToClient(player, new GridUpdatePacket(
            resource,
            change,
            Optional.ofNullable(trackedResource)
        ));
    }

    public static void sendGridClear(final ServerPlayer player) {
        Platform.INSTANCE.sendPacketToClient(player, GridClearPacket.INSTANCE);
    }

    public static void sendResourceSlotUpdate(final ServerPlayer player,
                                              @Nullable final ResourceAmount resourceAmount,
                                              final int slotIndex) {
        Platform.INSTANCE.sendPacketToClient(player, new ResourceSlotUpdatePacket(
            slotIndex,
            Optional.ofNullable(resourceAmount)
        ));
    }

    public static void sendStorageInfoResponse(final ServerPlayer player,
                                               final UUID id,
                                               final StorageInfo storageInfo) {
        Platform.INSTANCE.sendPacketToClient(
            player,
            new StorageInfoResponsePacket(id, storageInfo.stored(), storageInfo.capacity())
        );
    }

    public static void sendNetworkTransmitterStatus(final ServerPlayer player, final NetworkTransmitterData status) {
        Platform.INSTANCE.sendPacketToClient(
            player,
            new NetworkTransmitterStatusPacket(status.error(), status.transmitting(), status.message())
        );
    }

    public static void sendNoPermission(final ServerPlayer player, final Component message) {
        Platform.INSTANCE.sendPacketToClient(player, new NoPermissionPacket(message));
    }

    public static void sendPatternGridAllowedAlternativesUpdate(final ServerPlayer player,
                                                                final int index,
                                                                final Set<ResourceLocation> ids) {
        Platform.INSTANCE.sendPacketToClient(player, new PatternGridAllowedAlternativesUpdatePacket(index, ids));
    }

    public static void sendAutocrafterNameUpdate(final ServerPlayer player, final Component name) {
        Platform.INSTANCE.sendPacketToClient(player, new AutocrafterNameUpdatePacket(name));
    }

    public static void sendAutocraftingPreviewResponse(final ServerPlayer player,
                                                       final UUID id,
                                                       final AutocraftingPreview preview) {
        Platform.INSTANCE.sendPacketToClient(player, new AutocraftingPreviewResponsePacket(id, preview));
    }

    public static void sendAutocraftingResponse(final ServerPlayer player,
                                                final UUID id,
                                                final boolean started) {
        Platform.INSTANCE.sendPacketToClient(player, new AutocraftingResponsePacket(id, started));
    }
}
