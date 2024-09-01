package com.refinedmods.refinedstorage.api.autocrafting;

import com.refinedmods.refinedstorage.api.resource.list.ResourceList;

public record CraftingPreview(ResourceList missing, ResourceList toTake, ResourceList toCraft) {
}
