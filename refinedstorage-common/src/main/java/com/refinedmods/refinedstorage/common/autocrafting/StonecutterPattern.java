package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

class StonecutterPattern implements Pattern {
    private final UUID id;
    private final ItemResource input;
    private final ItemResource output;
    private final Set<ResourceKey> outputResources;

    StonecutterPattern(final UUID id, final ItemResource input, final ItemResource output) {
        this.id = id;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StonecutterPattern that = (StonecutterPattern) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
