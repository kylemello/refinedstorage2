package com.refinedmods.refinedstorage.common.storage;

import com.refinedmods.refinedstorage.common.Platform;

import javax.annotation.Nullable;

public enum FluidStorageVariant {
    SIXTY_FOUR_B("64b", 64L),
    TWO_HUNDRED_FIFTY_SIX_B("256b", 256L),
    THOUSAND_TWENTY_FOUR_B("1024b", 1024L),
    FOUR_THOUSAND_NINETY_SIX_B("4096b", 4096L),
    CREATIVE("creative", null);

    private final String name;
    @Nullable
    private final Long capacityInBuckets;

    FluidStorageVariant(final String name, @Nullable final Long capacityInBuckets) {
        this.name = name;
        this.capacityInBuckets = capacityInBuckets;
    }

    public String getName() {
        return name;
    }

    @Nullable
    public Long getCapacityInBuckets() {
        return capacityInBuckets;
    }

    @Nullable
    public Long getCapacity() {
        if (capacityInBuckets == null) {
            return null;
        }
        return capacityInBuckets * Platform.INSTANCE.getBucketAmount();
    }

    public boolean hasCapacity() {
        return capacityInBuckets != null;
    }
}
