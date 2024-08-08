package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

record SmithingTablePattern(PlatformResourceKey template,
                            PlatformResourceKey base,
                            PlatformResourceKey addition,
                            PlatformResourceKey output) implements Pattern {
}
