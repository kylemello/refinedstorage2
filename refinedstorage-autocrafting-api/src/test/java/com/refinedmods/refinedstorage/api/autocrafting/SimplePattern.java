package com.refinedmods.refinedstorage.api.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.Set;

public class SimplePattern implements Pattern {
    private final Set<ResourceKey> outputs;

    public SimplePattern(final ResourceKey... outputs) {
        this.outputs = Set.of(outputs);
    }

    @Override
    public Set<ResourceKey> getOutputResources() {
        return outputs;
    }
}
