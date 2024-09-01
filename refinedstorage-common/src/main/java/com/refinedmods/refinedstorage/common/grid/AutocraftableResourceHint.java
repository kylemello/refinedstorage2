package com.refinedmods.refinedstorage.common.grid;

public enum AutocraftableResourceHint {
    AUTOCRAFTABLE(0xBF9F7F50),
    PATTERN_IN_INVENTORY(0xBFFFD9A8);

    private final int color;

    AutocraftableResourceHint(final int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
