package com.refinedmods.refinedstorage.common.autocrafting.preview;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.support.containermenu.AbstractResourceContainerMenu;
import com.refinedmods.refinedstorage.common.support.containermenu.DisabledResourceSlot;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlotType;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;

import java.util.Collections;
import java.util.List;

import net.minecraft.network.chat.Component;

class CraftingPreviewContainerMenu extends AbstractResourceContainerMenu {
    private final List<CraftingRequest> requests;

    private CraftingRequest currentRequest;

    CraftingPreviewContainerMenu(final List<CraftingRequest> requests) {
        super(null, 0);
        final ResourceContainer resourceContainer = ResourceContainerImpl.createForFilter(1);
        resourceContainer.set(0, new ResourceAmount(requests.getFirst().getResource(), 1));
        addSlot(new DisabledResourceSlot(
            resourceContainer,
            0,
            Component.empty(),
            157,
            48,
            ResourceSlotType.FILTER
        ));
        this.requests = Collections.unmodifiableList(requests);
        this.currentRequest = requests.getFirst();
    }

    List<CraftingRequest> getRequests() {
        return requests;
    }

    CraftingRequest getCurrentRequest() {
        return currentRequest;
    }

    void setCurrentRequest(final CraftingRequest request) {
        this.currentRequest = request;
    }
}
