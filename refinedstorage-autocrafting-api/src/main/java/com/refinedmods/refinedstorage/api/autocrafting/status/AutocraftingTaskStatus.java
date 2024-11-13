package com.refinedmods.refinedstorage.api.autocrafting.status;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.List;
import java.util.UUID;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.10")
public record AutocraftingTaskStatus(Id id, List<Element> elements) {
    public record Id(UUID id, ResourceKey resource, long amount, long startTime) {
    }

    public record Element(
        ElementType type,
        ResourceKey resource,
        long stored,
        long missing,
        long processing,
        long scheduled,
        long crafting
    ) {
    }

    public enum ElementType {
        NORMAL,
        MACHINE_DOES_NOT_ACCEPT_RESOURCE,
        NO_MACHINE_FOUND,
        AUTOCRAFTER_IS_LOCKED
    }
}
