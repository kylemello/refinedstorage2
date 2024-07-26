package com.refinedmods.refinedstorage.common.api.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

import java.util.Collection;
import java.util.List;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.6")
public record CraftingPattern(List<List<PlatformResourceKey>> inputs,
                              ResourceAmount output,
                              List<ResourceAmount> byproducts) implements Pattern {
    @Override
    public Collection<ResourceAmount> getOutputs() {
        return List.of(output);
    }
}
