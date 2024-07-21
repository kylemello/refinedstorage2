package com.refinedmods.refinedstorage.common.support.resource;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceType;

public final class ResourceTypes {
    public static final ResourceType ITEM = new ItemResourceType();
    public static final ResourceType FLUID = new FluidResourceType();

    private ResourceTypes() {
    }
}
