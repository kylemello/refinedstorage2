package com.refinedmods.refinedstorage2.platform.forge.support.packet.c2s;

import com.refinedmods.refinedstorage2.api.grid.operations.GridExtractMode;
import com.refinedmods.refinedstorage2.api.grid.operations.GridInsertMode;
import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.grid.GridScrollMode;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage2.platform.api.support.resource.ResourceType;
import com.refinedmods.refinedstorage2.platform.common.support.ClientToServerCommunications;
import com.refinedmods.refinedstorage2.platform.common.support.containermenu.PropertyType;
import com.refinedmods.refinedstorage2.platform.common.support.resource.ItemResource;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;

public class ClientToServerCommunicationsImpl implements ClientToServerCommunications {
    private void sendToServer(final CustomPacketPayload packet) {
        PacketDistributor.SERVER.noArg().send(packet);
    }

    @Override
    public void sendGridExtract(final PlatformResourceKey resource,
                                final GridExtractMode mode,
                                final boolean cursor) {
        final ResourceType resourceType = resource.getResourceType();
        PlatformApi.INSTANCE.getResourceTypeRegistry().getId(resourceType).ifPresent(id -> sendToServer(
            new GridExtractPacket(resourceType, id, resource, mode, cursor)
        ));
    }

    @Override
    public void sendGridScroll(final PlatformResourceKey resource,
                               final GridScrollMode mode,
                               final int slotIndex) {
        final ResourceType resourceType = resource.getResourceType();
        PlatformApi.INSTANCE.getResourceTypeRegistry().getId(resourceType).ifPresent(id -> sendToServer(
            new GridScrollPacket(resourceType, id, resource, mode, slotIndex)
        ));
    }

    @Override
    public void sendGridInsert(final GridInsertMode mode, final boolean tryAlternatives) {
        sendToServer(new GridInsertPacket(mode == GridInsertMode.SINGLE_RESOURCE, tryAlternatives));
    }

    @Override
    public void sendCraftingGridClear(final boolean toPlayerInventory) {
        sendToServer(new CraftingGridClearPacket(toPlayerInventory));
    }

    @Override
    public void sendCraftingGridRecipeTransfer(final List<List<ItemResource>> recipe) {
        sendToServer(new CraftingGridRecipeTransferPacket(recipe));
    }

    @Override
    public <T> void sendPropertyChange(final PropertyType<T> type, final T value) {
        sendToServer(new PropertyChangePacket(type.id(), type.serializer().apply(value)));
    }

    @Override
    public void sendStorageInfoRequest(final UUID storageId) {
        sendToServer(new StorageInfoRequestPacket(storageId));
    }

    @Override
    public void sendResourceSlotChange(final int slotIndex, final boolean tryAlternatives) {
        sendToServer(new ResourceSlotChangePacket(slotIndex, tryAlternatives));
    }

    @Override
    public void sendResourceFilterSlotChange(final PlatformResourceKey resource, final int slotIndex) {
        final ResourceType resourceType = resource.getResourceType();
        PlatformApi.INSTANCE.getResourceTypeRegistry().getId(resourceType).ifPresent(id -> sendToServer(
            new ResourceFilterSlotChangePacket(slotIndex, resource, resourceType, id)
        ));
    }

    @Override
    public void sendResourceSlotAmountChange(final int slotIndex, final long amount) {
        sendToServer(new ResourceSlotAmountChangePacket(slotIndex, amount));
    }

    @Override
    public void sendSingleAmountChange(final double amount) {
        sendToServer(new SingleAmountChangePacket(amount));
    }

    @Override
    public void sendUseNetworkBoundItem(final SlotReference slotReference) {
        sendToServer(new UseNetworkBoundItemPacket(slotReference));
    }

    @Override
    public void sendSecurityCardPermission(final PlatformPermission permission, final boolean allowed) {
        PlatformApi.INSTANCE.getPermissionRegistry().getId(permission).ifPresent(id -> sendToServer(
            new SecurityCardPermissionPacket(id, allowed)
        ));
    }

    @Override
    public void sendSecurityCardResetPermission(final PlatformPermission permission) {
        PlatformApi.INSTANCE.getPermissionRegistry().getId(permission).ifPresent(id -> sendToServer(
            new SecurityCardResetPermissionPacket(id)
        ));
    }

    @Override
    public void sendSecurityCardBoundPlayer(@Nullable final UUID playerId) {
        sendToServer(new SecurityCardBoundPlayerPacket(playerId));
    }
}
