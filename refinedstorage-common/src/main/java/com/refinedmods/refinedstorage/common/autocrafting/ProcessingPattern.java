package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.AbstractPattern;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

class ProcessingPattern extends AbstractPattern {
    private final List<ResourceAmount> inputs;
    private final List<ResourceAmount> outputs;
    private final Set<ResourceKey> outputResources;

    ProcessingPattern(final UUID id, final List<ResourceAmount> inputs, final List<ResourceAmount> outputs) {
        super(id);
        this.inputs = inputs;
        this.outputs = outputs;
        this.outputResources = outputs.stream().map(ResourceAmount::resource).collect(Collectors.toSet());
    }

    @Override
    public Set<ResourceKey> getOutputResources() {
        return outputResources;
    }

    List<ResourceAmount> getInputs() {
        return inputs;
    }

    List<ResourceAmount> getOutputs() {
        return outputs;
    }
}
