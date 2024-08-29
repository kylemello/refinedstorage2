package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

record ProcessingPattern(List<ResourceAmount> inputs, List<ResourceAmount> outputs) implements Pattern {
    @Override
    public Set<ResourceKey> getOutputs() {
        return outputs.stream().map(ResourceAmount::resource).collect(Collectors.toSet());
    }
}
