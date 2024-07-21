package com.refinedmods.refinedstorage.common.support.packet;

import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface PacketContext {
    Player getPlayer();
}
