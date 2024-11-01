package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;

class SmithingTablePattern implements Pattern {
    private final UUID id;
    private final ItemResource template;
    private final ItemResource base;
    private final ItemResource addition;
    private final ItemResource output;
    private final Set<ResourceKey> inputResources;
    private final Set<ResourceKey> outputResources;

    SmithingTablePattern(final UUID id,
                         final ItemResource template,
                         final ItemResource base,
                         final ItemResource addition,
                         final ItemResource output) {
        this.id = id;
        this.template = template;
        this.base = base;
        this.addition = addition;
        this.output = output;
        this.inputResources = Set.of(template, base, addition);
        this.outputResources = Set.of(output);
    }

    @Override
    public Set<ResourceKey> getOutputResources() {
        return outputResources;
    }

    @Override
    public Set<ResourceKey> getInputResources() {
        return inputResources;
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SmithingTablePattern that = (SmithingTablePattern) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
