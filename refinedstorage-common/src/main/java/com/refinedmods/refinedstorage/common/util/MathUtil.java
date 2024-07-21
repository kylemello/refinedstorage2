package com.refinedmods.refinedstorage.common.util;

public final class MathUtil {
    private MathUtil() {
    }

    public static long clamp(final long value, final long min, final long max) {
        return Math.min(Math.max(value, min), max);
    }
}
