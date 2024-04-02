package com.refinedmods.refinedstorage2.api.network.impl.security;

import com.refinedmods.refinedstorage2.api.network.security.Permission;
import com.refinedmods.refinedstorage2.api.network.security.SecurityActor;
import com.refinedmods.refinedstorage2.api.network.security.SecurityDecision;
import com.refinedmods.refinedstorage2.api.network.security.SecurityDecisionProvider;
import com.refinedmods.refinedstorage2.api.network.security.SecurityPolicy;

import java.util.HashMap;
import java.util.Map;

public class SecurityDecisionProviderImpl implements SecurityDecisionProvider {
    private final Map<SecurityActor, SecurityPolicy> policyByActor = new HashMap<>();

    public SecurityDecisionProviderImpl setPolicy(final SecurityActor actor, final SecurityPolicy policy) {
        policyByActor.put(actor, policy);
        return this;
    }

    @Override
    public SecurityDecision isAllowed(final Permission permission, final SecurityActor actor) {
        final SecurityPolicy policy = policyByActor.get(actor);
        if (policy == null) {
            return SecurityDecision.PASS;
        }
        return policy.isAllowed(permission) ? SecurityDecision.ALLOW : SecurityDecision.DENY;
    }
}
