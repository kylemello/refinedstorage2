package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

record StonecutterPattern(ItemResource input, ItemResource output) implements Pattern {
}
