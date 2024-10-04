package com.refinedmods.refinedstorage.common.autocrafting.preview;

import javax.annotation.Nullable;

interface AutocraftingPreviewListener {
    void requestChanged(AutocraftingRequest request);

    void previewChanged(@Nullable AutocraftingPreview preview);
}
