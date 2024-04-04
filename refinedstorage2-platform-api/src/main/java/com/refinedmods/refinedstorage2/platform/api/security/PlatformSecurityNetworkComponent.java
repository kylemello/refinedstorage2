package com.refinedmods.refinedstorage2.platform.api.security;

import com.refinedmods.refinedstorage2.api.network.NetworkComponent;
import com.refinedmods.refinedstorage2.api.network.security.Permission;

import net.minecraft.server.level.ServerPlayer;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.3.5")
@FunctionalInterface
public interface PlatformSecurityNetworkComponent extends NetworkComponent {
    boolean isAllowed(Permission permission, ServerPlayer player);
}
