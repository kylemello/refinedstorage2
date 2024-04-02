package com.refinedmods.refinedstorage2.api.network.security;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.3.5")
@FunctionalInterface
public interface SecurityDecisionProvider {
    /**
     * Returns the {@link SecurityDecision} for the given {@link Permission} and {@link SecurityActor}.
     * If there is no {@link SecurityPolicy} for the given {@link SecurityActor}, the decision should
     * be {@link SecurityDecision#PASS}.
     *
     * @param permission the permission
     * @param actor      the actor
     * @return the security decision
     */
    SecurityDecision isAllowed(Permission permission, SecurityActor actor);
}
