package com.refinedmods.refinedstorage.common.autocrafting.preview;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

record CraftingPreviewItem(ResourceKey resource, long available, long missing, long toCraft) {
}
