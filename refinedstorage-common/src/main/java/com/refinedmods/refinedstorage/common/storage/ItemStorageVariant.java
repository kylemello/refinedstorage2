package com.refinedmods.refinedstorage.common.storage;

import javax.annotation.Nullable;

public enum ItemStorageVariant {
    ONE_K("1k", 1024L),
    FOUR_K("4k", 1024 * 4L),
    SIXTEEN_K("16k", 1024 * 4 * 4L),
    SIXTY_FOUR_K("64k", 1024 * 4 * 4 * 4L),
    CREATIVE("creative", null);

    private final String name;
    @Nullable
    private final Long capacity;

    ItemStorageVariant(final String name, @Nullable final Long capacity) {
        this.name = name;
        this.capacity = capacity;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public Long getCapacity() {
        return capacity;
    }

    public boolean hasCapacity() {
        return capacity != null;
    }
}
