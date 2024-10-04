package com.refinedmods.refinedstorage.api.network.impl.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.AutocraftingPreview;
import com.refinedmods.refinedstorage.api.autocrafting.AutocraftingPreviewItem;
import com.refinedmods.refinedstorage.api.autocrafting.AutocraftingPreviewType;
import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.autocrafting.PatternRepository;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.autocrafting.PatternProvider;
import com.refinedmods.refinedstorage.api.network.node.container.NetworkNodeContainer;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    @Override
    public Optional<AutocraftingPreview> getPreview(final ResourceKey resource, final long amount) {
        final List<AutocraftingPreviewItem> items = new ArrayList<>();
        final boolean missing = amount == 404;
        for (int i = 0; i < 31; ++i) {
            items.add(new AutocraftingPreviewItem(
                resource,
                (i + 1),
                (i % 2 == 0 && missing) ? amount : 0,
                i % 2 == 0 ? 0 : amount
            ));
        }
        return Optional.of(new AutocraftingPreview(missing
            ? AutocraftingPreviewType.MISSING_RESOURCES
            : AutocraftingPreviewType.SUCCESS, items));
    }

    @Override
    public boolean start(final ResourceKey resource, final long amount) {
        return true;
    }
}
