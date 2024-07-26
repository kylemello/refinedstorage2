package com.refinedmods.refinedstorage.common.support;

import com.refinedmods.refinedstorage.api.resource.filter.FilterMode;

public class FilterModeSettings {
    private static final int BLOCK = 0;
    private static final int ALLOW = 1;

    private FilterModeSettings() {
    }

    public static FilterMode getFilterMode(final int filterMode) {
        if (filterMode == ALLOW) {
            return FilterMode.ALLOW;
        }
        return FilterMode.BLOCK;
    }

    public static int getFilterMode(final FilterMode filterMode) {
        return switch (filterMode) {
            case BLOCK -> BLOCK;
            case ALLOW -> ALLOW;
        };
    }
}
