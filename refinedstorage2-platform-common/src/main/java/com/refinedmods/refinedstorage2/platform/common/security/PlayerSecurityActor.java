package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.api.network.security.SecurityActor;

import java.util.UUID;

public record PlayerSecurityActor(UUID playerId) implements SecurityActor {
}
