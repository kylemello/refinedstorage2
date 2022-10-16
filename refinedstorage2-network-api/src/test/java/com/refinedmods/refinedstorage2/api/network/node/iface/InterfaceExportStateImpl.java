package com.refinedmods.refinedstorage2.api.network.node.iface;

import com.refinedmods.refinedstorage2.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage2.api.storage.channel.StorageChannel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

public class InterfaceExportStateImpl implements InterfaceExportState<String> {
    private final Map<Integer, ResourceAmount<String>> requested = new HashMap<>();
    private final Map<Integer, ResourceAmount<String>> current = new HashMap<>();

    private final int slots;

    public InterfaceExportStateImpl(final int slots) {
        this.slots = slots;
    }

    public void setRequestedResource(final int index, final String resource, final long amount) {
        requested.put(index, new ResourceAmount<>(resource, amount));
    }

    @Override
    public int getSlots() {
        return slots;
    }

    @Override
    public Collection<String> expandExportCandidates(final StorageChannel<String> storageChannel,
                                                     final String resource) {
        if ("A".equals(resource)) {
            final List<String> candidates = new ArrayList<>();
            // simulate the behavior from FuzzyStorageChannel
            if (storageChannel.get("A1").isPresent()) {
                candidates.add("A1");
            }
            if (storageChannel.get("A2").isPresent()) {
                candidates.add("A2");
            }
            return candidates;
        }
        return Collections.singletonList(resource);
    }

    @Override
    public boolean isCurrentlyExportedResourceValid(final String want, final String got) {
        if ("A".equals(want)) {
            return got.startsWith("A");
        }
        return got.equals(want);
    }

    @Nullable
    @Override
    public String getRequestedResource(final int index) {
        final ResourceAmount<String> resourceAmount = this.requested.get(index);
        if (resourceAmount == null) {
            return null;
        }
        return resourceAmount.getResource();
    }

    @Override
    public long getRequestedResourceAmount(final int index) {
        final ResourceAmount<String> resourceAmount = this.requested.get(index);
        if (resourceAmount == null) {
            return 0L;
        }
        return resourceAmount.getAmount();
    }

    @Nullable
    @Override
    public String getCurrentlyExportedResource(final int index) {
        final ResourceAmount<String> resourceAmount = this.current.get(index);
        if (resourceAmount == null) {
            return null;
        }
        return resourceAmount.getResource();
    }

    @Override
    public long getCurrentlyExportedResourceAmount(final int index) {
        final ResourceAmount<String> resourceAmount = this.current.get(index);
        if (resourceAmount == null) {
            return 0L;
        }
        return resourceAmount.getAmount();
    }

    @Override
    public void setCurrentlyExported(final int index, final String resource, final long amount) {
        current.put(index, new ResourceAmount<>(resource, amount));
    }

    @Override
    public void decrementCurrentlyExportedAmount(final int index, final long amount) {
        final ResourceAmount<String> resourceAmount = this.current.get(index);
        if (resourceAmount.getAmount() - amount <= 0) {
            this.current.remove(index);
        } else {
            resourceAmount.decrement(amount);
        }
    }

    @Override
    public void incrementCurrentlyExportedAmount(final int index, final long amount) {
        final ResourceAmount<String> resourceAmount = this.current.get(index);
        resourceAmount.increment(amount);
    }
}
