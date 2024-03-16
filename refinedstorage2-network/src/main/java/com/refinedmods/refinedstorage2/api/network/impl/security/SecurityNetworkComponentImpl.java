package com.refinedmods.refinedstorage2.api.network.impl.security;

import com.refinedmods.refinedstorage2.api.network.node.container.NetworkNodeContainer;
import com.refinedmods.refinedstorage2.api.network.security.Operation;
import com.refinedmods.refinedstorage2.api.network.security.SecurityActor;
import com.refinedmods.refinedstorage2.api.network.security.SecurityDecision;
import com.refinedmods.refinedstorage2.api.network.security.SecurityDecisionProvider;
import com.refinedmods.refinedstorage2.api.network.security.SecurityNetworkComponent;

import java.util.HashSet;
import java.util.Set;

public class SecurityNetworkComponentImpl implements SecurityNetworkComponent {
    private final Set<SecurityDecisionProvider> providers = new HashSet<>();

    @Override
    public void onContainerAdded(final NetworkNodeContainer container) {
        if (container instanceof SecurityDecisionProvider provider) {
            providers.add(provider);
        }
    }

    @Override
    public void onContainerRemoved(final NetworkNodeContainer container) {
        if (container instanceof SecurityDecisionProvider provider) {
            providers.remove(provider);
        }
    }

    @Override
    public boolean isAllowed(final Operation operation, final SecurityActor actor) {
        for (final SecurityDecisionProvider provider : providers) {
            final SecurityDecision decision = provider.isAllowed(operation, actor);
            if (decision == SecurityDecision.DENY) {
                return false;
            } else if (decision == SecurityDecision.ALLOW) {
                return true;
            }
        }
        return true;
    }
}
