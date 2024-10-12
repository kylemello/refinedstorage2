package com.refinedmods.refinedstorage.api.network.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.AutocraftingPreviewProvider;
import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.network.NetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.Set;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.8")
public interface AutocraftingNetworkComponent extends NetworkComponent, AutocraftingPreviewProvider {
    void addListener(PatternListener listener);

    void removeListener(PatternListener listener);

    Set<Pattern> getPatterns();

    Set<ResourceKey> getOutputs();

    boolean contains(AutocraftingNetworkComponent component);
}
