package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.Set;

record StonecutterPattern(ItemResource input, ItemResource output) implements Pattern {
    @Override
    public Set<ResourceKey> getOutputs() {
        return Set.of(output);
    }
}
