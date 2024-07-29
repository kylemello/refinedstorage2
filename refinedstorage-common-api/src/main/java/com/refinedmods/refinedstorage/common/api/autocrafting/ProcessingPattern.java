package com.refinedmods.refinedstorage.common.api.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;

import java.util.List;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.6")
public record ProcessingPattern(List<ResourceAmount> inputs, List<ResourceAmount> outputs) implements Pattern {
}
