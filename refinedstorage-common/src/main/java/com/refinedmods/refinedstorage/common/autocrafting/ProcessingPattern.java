package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.autocrafting.Pattern;

import java.util.List;

record ProcessingPattern(List<ResourceAmount> inputs, List<ResourceAmount> outputs) implements Pattern {
}
