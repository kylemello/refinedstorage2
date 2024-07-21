package com.refinedmods.refinedstorage.common.grid;

import com.refinedmods.refinedstorage.common.api.grid.GridSynchronizer;
import com.refinedmods.refinedstorage.common.support.TextureIds;

import net.minecraft.resources.ResourceLocation;

public abstract class AbstractGridSynchronizer implements GridSynchronizer {
    @Override
    public ResourceLocation getTextureIdentifier() {
        return TextureIds.ICONS;
    }

    @Override
    public int getYTexture() {
        return 96;
    }
}
