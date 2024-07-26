package com.refinedmods.refinedstorage.common.support.resource;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceFactory;

import java.util.Optional;

import net.minecraft.world.item.ItemStack;

public class FluidResourceFactory implements ResourceFactory {
    @Override
    public Optional<ResourceAmount> create(final ItemStack stack) {
        return Platform.INSTANCE.drainContainer(stack).map(result -> new ResourceAmount(
            result.fluid(),
            Platform.INSTANCE.getBucketAmount()
        ));
    }

    @Override
    public boolean isValid(final ResourceKey resource) {
        return resource instanceof FluidResource;
    }
}
