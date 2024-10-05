package com.refinedmods.refinedstorage.common.autocrafting.preview;

import com.refinedmods.refinedstorage.api.autocrafting.AutocraftingPreview;

import javax.annotation.Nullable;

interface AutocraftingPreviewListener {
    void requestChanged(AutocraftingRequest request);

    void previewChanged(@Nullable AutocraftingPreview preview);

    void requestRemoved(AutocraftingRequest request, boolean last);
}
