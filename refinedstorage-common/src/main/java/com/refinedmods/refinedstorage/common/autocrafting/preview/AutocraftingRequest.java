package com.refinedmods.refinedstorage.common.autocrafting.preview;

import com.refinedmods.refinedstorage.api.autocrafting.AutocraftingPreview;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.packet.c2s.C2SPackets;

import java.util.UUID;
import javax.annotation.Nullable;

public class AutocraftingRequest {
    private final UUID id;
    private final ResourceKey resource;
    private final double amount;
    @Nullable
    private AutocraftingPreview preview;
    private long pendingPreviewAmount;

    private AutocraftingRequest(final UUID id, final ResourceKey resource, final double amount) {
        this.id = id;
        this.resource = resource;
        this.amount = amount;
    }

    public static AutocraftingRequest of(final ResourceAmount resourceAmount) {
        final double displayAmount = resourceAmount.resource() instanceof PlatformResourceKey platformResourceKey
            ? platformResourceKey.getResourceType().getDisplayAmount(resourceAmount.amount())
            : resourceAmount.amount();
        return new AutocraftingRequest(UUID.randomUUID(), resourceAmount.resource(), displayAmount);
    }

    boolean trySendRequest(final double amountRequested) {
        if (!(resource instanceof PlatformResourceKey resourceKey)) {
            return false;
        }
        final long normalizedAmount = resourceKey.getResourceType().normalizeAmount(amountRequested);
        if (normalizedAmount == pendingPreviewAmount) {
            return false;
        }
        this.preview = null;
        this.pendingPreviewAmount = normalizedAmount;
        C2SPackets.sendAutocraftingPreviewRequest(id, resourceKey, normalizedAmount);
        return true;
    }

    void start(final double amountRequested) {
        if (!(resource instanceof PlatformResourceKey resourceKey)) {
            return;
        }
        final long normalizedAmount = resourceKey.getResourceType().normalizeAmount(amountRequested);
        C2SPackets.sendAutocraftingRequest(id, resourceKey, normalizedAmount);
    }

    ResourceKey getResource() {
        return resource;
    }

    double getAmount() {
        return amount;
    }

    @Nullable
    AutocraftingPreview getPreview() {
        return preview;
    }

    boolean previewReceived(final UUID idReceived, final AutocraftingPreview previewReceived) {
        if (id.equals(idReceived)) {
            pendingPreviewAmount = 0;
            preview = previewReceived;
            return true;
        }
        return false;
    }

    void clearPreview() {
        pendingPreviewAmount = 0;
        preview = null;
    }

    boolean isStarted(final UUID startedId) {
        return id.equals(startedId);
    }
}
