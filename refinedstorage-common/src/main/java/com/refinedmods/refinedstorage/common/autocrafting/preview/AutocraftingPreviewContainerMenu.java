package com.refinedmods.refinedstorage.common.autocrafting.preview;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.support.containermenu.AbstractResourceContainerMenu;
import com.refinedmods.refinedstorage.common.support.containermenu.DisabledResourceSlot;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlotType;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;

public class AutocraftingPreviewContainerMenu extends AbstractResourceContainerMenu {
    private final List<AutocraftingRequest> requests;

    private AutocraftingRequest currentRequest;
    @Nullable
    private AutocraftingPreviewListener listener;

    AutocraftingPreviewContainerMenu(final List<AutocraftingRequest> requests) {
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

    void setListener(final AutocraftingPreviewListener listener) {
        this.listener = listener;
    }

    List<AutocraftingRequest> getRequests() {
        return requests;
    }

    AutocraftingRequest getCurrentRequest() {
        return currentRequest;
    }

    void setCurrentRequest(final AutocraftingRequest request) {
        this.currentRequest = request;
        if (listener != null) {
            listener.requestChanged(request);
        }
    }

    void amountChanged(final double amount) {
        if (currentRequest.trySendRequest(amount) && listener != null) {
            listener.previewChanged(null);
        }
    }

    public void previewReceived(final UUID id, final AutocraftingPreview preview) {
        if (currentRequest.previewReceived(id, preview) && listener != null) {
            listener.previewChanged(preview);
        }
    }

    public void loadCurrentRequest() {
        if (listener != null) {
            currentRequest.clearPreview();
            listener.requestChanged(currentRequest);
        }
    }
}
