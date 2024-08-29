package com.refinedmods.refinedstorage.api.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.Set;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.6")
public interface Pattern {
    Set<ResourceKey> getOutputs();
}
