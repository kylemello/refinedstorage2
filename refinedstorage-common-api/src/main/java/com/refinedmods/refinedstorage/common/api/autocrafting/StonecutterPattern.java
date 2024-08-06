package com.refinedmods.refinedstorage.common.api.autocrafting;

import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.6")
public record StonecutterPattern(PlatformResourceKey input, PlatformResourceKey output) implements Pattern {
}
