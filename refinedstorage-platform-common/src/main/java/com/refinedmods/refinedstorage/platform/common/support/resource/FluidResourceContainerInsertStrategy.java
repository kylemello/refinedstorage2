package com.refinedmods.refinedstorage.platform.common.support.resource;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.platform.api.support.resource.ResourceContainerInsertStrategy;
import com.refinedmods.refinedstorage.platform.common.Platform;

import java.util.Optional;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class FluidResourceContainerInsertStrategy implements ResourceContainerInsertStrategy {
    private static final ItemStack EMPTY_BUCKET = new ItemStack(Items.BUCKET);

    @Override
    public Optional<InsertResult> insert(final ItemStack container, final ResourceAmount resourceAmount) {
        return Platform.INSTANCE.fillContainer(container, resourceAmount).map(
            result -> new InsertResult(result.container(), result.amount())
        );
    }

    @Override
    public Optional<ConversionInfo> getConversionInfo(final ResourceKey resource) {
        if (!(resource instanceof FluidResource fluidResource)) {
            return Optional.empty();
        }
        return Platform.INSTANCE.getFilledBucket(fluidResource).map(filledBucket -> new ConversionInfo(
            EMPTY_BUCKET,
            filledBucket
        ));
    }
}
