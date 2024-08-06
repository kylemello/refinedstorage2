package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

record StonecutterPattern(PlatformResourceKey input, PlatformResourceKey output) implements Pattern {
}
