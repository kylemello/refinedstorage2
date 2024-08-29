package com.refinedmods.refinedstorage.api.network.impl.node.patternprovider;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.autocrafting.PatternProvider;
import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class PatternProviderNetworkNode extends SimpleNetworkNode implements PatternProvider {
    private final Pattern[] patterns;

    public PatternProviderNetworkNode(final long energyUsage, final int patterns) {
        super(energyUsage);
        this.patterns = new Pattern[patterns];
    }

    public void setPattern(final int index, @Nullable final Pattern pattern) {
        final Pattern oldPattern = patterns[index];
        if (oldPattern != null && network != null) {
            network.getComponent(AutocraftingNetworkComponent.class).remove(oldPattern);
        }
        patterns[index] = pattern;
        if (pattern != null && network != null) {
            network.getComponent(AutocraftingNetworkComponent.class).add(pattern);
        }
    }

    @Override
    protected void onActiveChanged(final boolean newActive) {
        super.onActiveChanged(newActive);
        if (!newActive && network != null) {
            final AutocraftingNetworkComponent component = network.getComponent(AutocraftingNetworkComponent.class);
            for (final Pattern pattern : patterns) {
                if (pattern != null) {
                    component.remove(pattern);
                }
            }
        } else if (newActive && network != null) {
            final AutocraftingNetworkComponent component = network.getComponent(AutocraftingNetworkComponent.class);
            for (final Pattern pattern : patterns) {
                if (pattern != null) {
                    component.add(pattern);
                }
            }
        }
    }

    @Override
    public Set<Pattern> getPatterns() {
        return Arrays.stream(patterns).filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
