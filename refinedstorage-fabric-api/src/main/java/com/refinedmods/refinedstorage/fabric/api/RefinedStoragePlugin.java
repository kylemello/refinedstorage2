package com.refinedmods.refinedstorage.fabric.api;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.3.13")
@FunctionalInterface
public interface RefinedStoragePlugin {
    void onApiAvailable(RefinedStorageApi api);
}
