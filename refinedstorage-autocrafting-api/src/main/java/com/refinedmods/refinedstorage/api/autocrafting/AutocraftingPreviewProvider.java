package com.refinedmods.refinedstorage.api.autocrafting;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.Optional;

public interface AutocraftingPreviewProvider {
    Optional<AutocraftingPreview> getPreview(ResourceKey resource, long amount);

    boolean start(ResourceKey resource, long amount);
}
