package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

class ProcessingPattern implements Pattern {
    private final UUID id;
    private final List<ResourceAmount> inputs;
    private final List<ResourceAmount> outputs;
    private final Set<ResourceKey> outputResources;

    ProcessingPattern(final UUID id, final List<ResourceAmount> inputs, final List<ResourceAmount> outputs) {
        this.id = id;
        this.inputs = inputs;
        this.outputs = outputs;
        this.outputResources = outputs.stream().map(ResourceAmount::resource).collect(Collectors.toSet());
    }

    @Override
    public Set<ResourceKey> getOutputResources() {
        return outputResources;
    }

    List<ResourceAmount> getOutputs() {
        return outputs;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProcessingPattern that = (ProcessingPattern) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
