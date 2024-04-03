package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.api.network.security.SecurityActor;

import java.util.UUID;

import net.minecraft.server.level.ServerPlayer;

public record PlayerSecurityActor(UUID playerId) implements SecurityActor {
    public static PlayerSecurityActor of(final ServerPlayer player) {
        return new PlayerSecurityActor(player.getGameProfile().getId());
    }
}
