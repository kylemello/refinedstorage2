package com.refinedmods.refinedstorage2.api.network.security;

import com.refinedmods.refinedstorage2.api.network.NetworkComponent;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.3.5")
public interface SecurityNetworkComponent extends NetworkComponent {
    boolean isAllowed(Permission permission, SecurityActor actor);

    boolean contains(SecurityNetworkComponent component);
}
