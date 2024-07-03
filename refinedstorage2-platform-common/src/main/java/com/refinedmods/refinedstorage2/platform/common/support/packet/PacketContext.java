package com.refinedmods.refinedstorage2.platform.common.support.packet;

import net.minecraft.world.entity.player.Player;

@FunctionalInterface
public interface PacketContext {
    Player getPlayer();
}
