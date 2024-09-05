package com.refinedmods.refinedstorage.common.autocrafting.preview;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class CraftingRequest {
    private final ResourceKey resource;
    private final double amount;
    @Nullable
    private CraftingPreview preview;

    private CraftingRequest(final ResourceKey resource, final double amount) {
        this.resource = resource;
        this.amount = amount;
        final List<CraftingPreviewItem> items = new ArrayList<>();
        for (int i = 0; i < 31; ++i) {
            items.add(new CraftingPreviewItem(resource, i, i % 2 == 0 ? 999 : 0, i % 2 == 0 ? 0 : 1000));
        }
        this.preview = new CraftingPreview(true, items);
    }

    public static CraftingRequest of(final ResourceAmount resourceAmount) {
        final double displayAmount = resourceAmount.resource() instanceof PlatformResourceKey platformResourceKey
            ? platformResourceKey.getResourceType().getDisplayAmount(resourceAmount.amount())
            : resourceAmount.amount();
        return new CraftingRequest(resourceAmount.resource(), displayAmount);
    }

    ResourceKey getResource() {
        return resource;
    }

    double getAmount() {
        return amount;
    }

    @Nullable
    CraftingPreview getPreview() {
        return preview;
    }
}
