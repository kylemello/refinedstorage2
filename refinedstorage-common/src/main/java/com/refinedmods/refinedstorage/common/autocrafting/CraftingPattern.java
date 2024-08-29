package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

import java.util.List;
import java.util.Set;

record CraftingPattern(List<List<PlatformResourceKey>> inputs, ResourceAmount output, List<ResourceAmount> byproducts)
    implements Pattern {
    @Override
    public Set<ResourceKey> getOutputs() {
        return Set.of(output.resource());
    }
}
