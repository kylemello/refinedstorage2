package com.refinedmods.refinedstorage.common.api.storage.root;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;

import java.util.Collection;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.4")
public interface FuzzyRootStorage extends RootStorage {
    Collection<ResourceAmount> getFuzzy(ResourceKey resource);
}
