package com.refinedmods.refinedstorage.common.security;

import com.refinedmods.refinedstorage.api.network.security.SecurityActor;

import java.util.UUID;

public record PlayerSecurityActor(UUID playerId) implements SecurityActor {
}
