package com.refinedmods.refinedstorage.common.support;

import com.refinedmods.refinedstorage.common.Platform;

import java.io.File;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.saveddata.SavedData;

public abstract class AbstractPlatformSavedData extends SavedData {
    @Override
    public final void save(final File file, final HolderLookup.Provider provider) {
        Platform.INSTANCE.saveSavedData(this, file, provider, super::save);
    }
}
