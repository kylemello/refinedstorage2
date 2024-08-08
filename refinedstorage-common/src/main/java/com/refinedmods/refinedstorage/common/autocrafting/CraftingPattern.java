package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

import java.util.List;

record CraftingPattern(List<List<PlatformResourceKey>> inputs, ResourceAmount output, List<ResourceAmount> byproducts)
    implements Pattern {
}
