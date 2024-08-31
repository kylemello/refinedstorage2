package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.AbstractPattern;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.Set;
import java.util.UUID;

class SmithingTablePattern extends AbstractPattern {
    private final ItemResource template;
    private final ItemResource base;
    private final ItemResource addition;
    private final ItemResource output;
    private final Set<ResourceKey> outputResources;

    SmithingTablePattern(final UUID id,
                         final ItemResource template,
                         final ItemResource base,
                         final ItemResource addition,
                         final ItemResource output) {
        super(id);
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.output = output;
        this.outputResources = Set.of(output);
    }

    @Override
    public Set<ResourceKey> getOutputResources() {
        return outputResources;
    }

    ItemResource getTemplate() {
        return template;
    }

    ItemResource getBase() {
        return base;
    }

    ItemResource getAddition() {
        return addition;
    }

    ItemResource getOutput() {
        return output;
    }
}
