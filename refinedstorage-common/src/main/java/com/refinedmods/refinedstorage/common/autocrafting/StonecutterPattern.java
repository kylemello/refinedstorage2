package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.AbstractPattern;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.Set;
import java.util.UUID;

class StonecutterPattern extends AbstractPattern {
    private final ItemResource input;
    private final ItemResource output;
    private final Set<ResourceKey> outputResources;

    StonecutterPattern(final UUID id, final ItemResource input, final ItemResource output) {
        super(id);
        this.input = input;
        this.output = output;
        this.outputResources = Set.of(output);
    }

    @Override
    public Set<ResourceKey> getOutputResources() {
        return outputResources;
    }

    ItemResource getInput() {
        return input;
    }

    ItemResource getOutput() {
        return output;
    }
}
