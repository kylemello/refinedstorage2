package com.refinedmods.refinedstorage.api.network.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.AutocraftingPreviewProvider;
import com.refinedmods.refinedstorage.api.autocrafting.PatternRepository;
import com.refinedmods.refinedstorage.api.network.NetworkComponent;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.8")
public interface AutocraftingNetworkComponent extends NetworkComponent, PatternRepository, AutocraftingPreviewProvider {
}
