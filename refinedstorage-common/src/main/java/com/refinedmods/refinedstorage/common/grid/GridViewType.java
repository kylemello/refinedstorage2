package com.refinedmods.refinedstorage.common.grid;

public enum GridViewType {
    ALL,
    CRAFTABLE,
    NON_CRAFTABLE;

    boolean accepts(final boolean craftable) {
        return switch (this) {
            case ALL -> true;
            case CRAFTABLE -> craftable;
            case NON_CRAFTABLE -> !craftable;
        };
    }
}
