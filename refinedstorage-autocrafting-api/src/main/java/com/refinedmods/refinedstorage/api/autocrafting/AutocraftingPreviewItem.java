package com.refinedmods.refinedstorage.api.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

public record AutocraftingPreviewItem(ResourceKey resource, long available, long missing, long toCraft) {
}
