package com.refinedmods.refinedstorage.api.network.autocrafting;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.8")
public interface PatternProvider {
    void onAddedIntoContainer(ParentContainer parentContainer);

    void onRemovedFromContainer(ParentContainer parentContainer);

    default boolean contains(AutocraftingNetworkComponent component) {
        return false;
    }
}
