package com.refinedmods.refinedstorage.common.api.storagemonitor;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;

import net.minecraft.world.entity.player.Player;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.3.1")
@FunctionalInterface
public interface StorageMonitorExtractionStrategy {
    boolean extract(ResourceKey resource, boolean fullStack, Player player, Actor actor, Network network);
}
