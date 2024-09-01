package com.refinedmods.refinedstorage.common.autocrafting.preview;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.support.containermenu.AbstractResourceContainerMenu;
import com.refinedmods.refinedstorage.common.support.containermenu.DisabledResourceSlot;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlotType;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;

class CraftingPreviewContainerMenu extends AbstractResourceContainerMenu {
    @Nullable
    private CraftingPreview preview;

    CraftingPreviewContainerMenu(final ResourceKey resource) {
        super(null, 0);
        final ResourceContainer resourceContainer = ResourceContainerImpl.createForFilter(1);
        resourceContainer.set(0, new ResourceAmount(resource, 1));
        addSlot(new DisabledResourceSlot(
            resourceContainer,
            0,
            Component.empty(),
            157,
            48,
            ResourceSlotType.FILTER
        ));

        final List<CraftingPreviewItem> items = new ArrayList<>();
        for (int i = 0; i < 31; ++i) {
            items.add(new CraftingPreviewItem(resource, i, i % 2 == 0 ? 999 : 0, i % 2 == 0 ? 0 : 1000));
        }
        preview = new CraftingPreview(true, items);
    }

    @Nullable
    public CraftingPreview getPreview() {
        return preview;
    }
}
