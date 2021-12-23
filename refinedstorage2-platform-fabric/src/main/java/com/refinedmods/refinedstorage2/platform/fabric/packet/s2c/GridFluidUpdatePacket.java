package com.refinedmods.refinedstorage2.platform.fabric.packet.s2c;

import com.refinedmods.refinedstorage2.api.storage.channel.StorageTracker;
import com.refinedmods.refinedstorage2.platform.fabric.api.resource.FluidResource;
import com.refinedmods.refinedstorage2.platform.fabric.containermenu.grid.FluidGridContainerMenu;
import com.refinedmods.refinedstorage2.platform.fabric.util.PacketUtil;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;

public class GridFluidUpdatePacket implements ClientPlayNetworking.PlayChannelHandler {
    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        FluidResource fluidResource = PacketUtil.readFluidResource(buf);
        long amount = buf.readLong();
        StorageTracker.Entry trackerEntry = PacketUtil.readTrackerEntry(buf);

        client.execute(() -> {
            AbstractContainerMenu screenHandler = client.player.containerMenu;
            if (screenHandler instanceof FluidGridContainerMenu fluidGridScreenHandler) {
                fluidGridScreenHandler.onResourceUpdate(fluidResource, amount, trackerEntry);
            }
        });
    }
}
