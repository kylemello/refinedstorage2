package com.refinedmods.refinedstorage.api.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.Set;

class SimplePattern implements Pattern {
    private final Set<ResourceKey> outputs;

    SimplePattern(final ResourceKey... outputs) {
        this.outputs = Set.of(outputs);
    }

    @Override
    public Set<ResourceKey> getInputResources() {
        return Set.of();
    }

    @Override
    public Set<ResourceKey> getOutputResources() {
        return outputs;
    }
}
