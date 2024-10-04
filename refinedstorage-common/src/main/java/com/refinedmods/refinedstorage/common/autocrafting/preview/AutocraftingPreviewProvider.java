package com.refinedmods.refinedstorage.common.autocrafting.preview;

import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

public interface AutocraftingPreviewProvider {
    AutocraftingPreview getPreview(PlatformResourceKey resource, long amount);

    boolean start(PlatformResourceKey resource, long amount);
}
