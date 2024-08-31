package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.AbstractPattern;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

import java.util.List;
import java.util.Set;
import java.util.UUID;

// TODO: help tooltip for fuzzy mode tooltip
// TODO: help tooltip offset -2
class CraftingPattern extends AbstractPattern {
    private final List<List<PlatformResourceKey>> inputs;
    private final ResourceAmount output;
    private final List<ResourceAmount> byproducts;
    private final Set<ResourceKey> outputResources;

    CraftingPattern(final UUID id,
                    final List<List<PlatformResourceKey>> inputs,
                    final ResourceAmount output,
                    final List<ResourceAmount> byproducts) {
        super(id);
        this.inputs = inputs;
        this.output = output;
        this.outputResources = Set.of(output.resource());
        this.byproducts = byproducts;
    }

    @Override
    public Set<ResourceKey> getOutputResources() {
        return outputResources;
    }

    List<List<PlatformResourceKey>> getInputs() {
        return inputs;
    }

    ResourceAmount getOutput() {
        return output;
    }
}
