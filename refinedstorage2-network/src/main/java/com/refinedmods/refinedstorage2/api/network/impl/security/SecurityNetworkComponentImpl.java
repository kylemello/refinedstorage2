package com.refinedmods.refinedstorage2.api.network.impl.security;

import com.refinedmods.refinedstorage2.api.core.CoreValidations;
import com.refinedmods.refinedstorage2.api.network.node.container.NetworkNodeContainer;
import com.refinedmods.refinedstorage2.api.network.security.Permission;
import com.refinedmods.refinedstorage2.api.network.security.SecurityActor;
import com.refinedmods.refinedstorage2.api.network.security.SecurityDecision;
import com.refinedmods.refinedstorage2.api.network.security.SecurityDecisionProvider;
import com.refinedmods.refinedstorage2.api.network.security.SecurityNetworkComponent;
import com.refinedmods.refinedstorage2.api.network.security.SecurityPolicy;

import java.util.LinkedHashSet;
import java.util.Set;

public class SecurityNetworkComponentImpl implements SecurityNetworkComponent {
    private final Set<SecurityDecisionProvider> providers = new LinkedHashSet<>();
    private final SecurityPolicy defaultPolicy;

    public SecurityNetworkComponentImpl(final SecurityPolicy defaultPolicy) {
        this.defaultPolicy = defaultPolicy;
    }

    @Override
    public void onContainerAdded(final NetworkNodeContainer container) {
        if (container.getNode() instanceof SecurityDecisionProvider provider) {
            providers.add(provider);
        }
    }

    @Override
    public void onContainerRemoved(final NetworkNodeContainer container) {
        if (container.getNode() instanceof SecurityDecisionProvider provider) {
            providers.remove(provider);
        }
    }

    @Override
    public boolean isAllowed(final Permission permission, final SecurityActor actor) {
        for (final SecurityDecisionProvider provider : providers) {
            final SecurityDecision decision = CoreValidations.validateNotNull(
                provider.isAllowed(permission, actor),
                "Security decision provider must not return null"
            );
            if (decision == SecurityDecision.DENY) {
                return false;
            } else if (decision == SecurityDecision.ALLOW) {
                return true;
            }
        }
        return defaultPolicy.isAllowed(permission);
    }
}
