package com.refinedmods.refinedstorage.api.resource;

import com.refinedmods.refinedstorage.api.core.CoreValidations;

import org.apiguardian.api.API;

/**
 * A class representing a resource and a corresponding amount.
 */
@API(status = API.Status.STABLE, since = "2.0.0-milestone.1.2")
public record ResourceAmount(ResourceKey resource, long amount) {
    /**
     * @param resource the resource, must be non-null
     * @param amount   the amount, must be larger than 0
     */
    public ResourceAmount {
        validate(resource, amount);
    }

    public static void validate(final ResourceKey resource, final long amount) {
        CoreValidations.validateLargerThanZero(amount, "Amount must be larger than 0");
        CoreValidations.validateNotNull(resource, "Resource must not be null");
    }
}
