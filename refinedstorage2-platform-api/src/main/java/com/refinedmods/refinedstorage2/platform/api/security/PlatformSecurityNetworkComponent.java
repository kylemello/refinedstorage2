package com.refinedmods.refinedstorage2.platform.api.security;

import com.refinedmods.refinedstorage2.api.network.security.Permission;
import com.refinedmods.refinedstorage2.api.network.security.SecurityNetworkComponent;

import net.minecraft.server.level.ServerPlayer;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.3.5")
public interface PlatformSecurityNetworkComponent extends SecurityNetworkComponent {
    boolean isAllowed(Permission permission, ServerPlayer player);
}
