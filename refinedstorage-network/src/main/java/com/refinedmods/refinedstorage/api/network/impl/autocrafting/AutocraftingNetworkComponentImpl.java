package com.refinedmods.refinedstorage.api.network.impl.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.autocrafting.PatternRepository;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.autocrafting.PatternProvider;
import com.refinedmods.refinedstorage.api.network.node.container.NetworkNodeContainer;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.Set;

public class AutocraftingNetworkComponentImpl implements AutocraftingNetworkComponent {
    private final PatternRepository patternRepository;

    public AutocraftingNetworkComponentImpl(final PatternRepository patternRepository) {
        this.patternRepository = patternRepository;
    }

    @Override
    public void onContainerAdded(final NetworkNodeContainer container) {
        if (container.getNode() instanceof PatternProvider provider) {
            provider.getPatterns().forEach(patternRepository::add);
        }
    }

    @Override
    public void onContainerRemoved(final NetworkNodeContainer container) {
        if (container.getNode() instanceof PatternProvider provider) {
            provider.getPatterns().forEach(patternRepository::remove);
        }
    }

    @Override
    public void add(final Pattern pattern) {
        patternRepository.add(pattern);
    }

    @Override
    public void remove(final Pattern pattern) {
        patternRepository.remove(pattern);
    }

    @Override
    public Set<ResourceKey> getOutputs() {
        return patternRepository.getOutputs();
    }
}
