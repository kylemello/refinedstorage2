package com.refinedmods.refinedstorage2.api.network.impl.node.security;

import com.refinedmods.refinedstorage2.api.network.impl.storage.AbstractNetworkNode;
import com.refinedmods.refinedstorage2.api.network.security.Permission;
import com.refinedmods.refinedstorage2.api.network.security.SecurityActor;
import com.refinedmods.refinedstorage2.api.network.security.SecurityDecision;
import com.refinedmods.refinedstorage2.api.network.security.SecurityDecisionProvider;

public class SecurityDecisionProviderProxyNetworkNode extends AbstractNetworkNode implements SecurityDecisionProvider {
    private final long energyUsage;
    private final SecurityDecisionProvider delegate;

    public SecurityDecisionProviderProxyNetworkNode(final long energyUsage, final SecurityDecisionProvider delegate) {
        this.energyUsage = energyUsage;
        this.delegate = delegate;
    }

    @Override
    public long getEnergyUsage() {
        return energyUsage;
    }

    @Override
    public SecurityDecision isAllowed(final Permission permission, final SecurityActor actor) {
        return delegate.isAllowed(permission, actor);
    }
}
