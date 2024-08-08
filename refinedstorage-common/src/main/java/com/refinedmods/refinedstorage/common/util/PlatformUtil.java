package com.refinedmods.refinedstorage.common.util;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;

public final class PlatformUtil {
    private PlatformUtil() {
    }

    @Nullable
    public static Level getClientLevel() { // avoids classloading issues
        return Minecraft.getInstance().level;
    }
}
