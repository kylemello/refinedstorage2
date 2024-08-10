package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

record SmithingTablePattern(ItemResource template, ItemResource base, ItemResource addition, ItemResource output)
    implements Pattern {
}
