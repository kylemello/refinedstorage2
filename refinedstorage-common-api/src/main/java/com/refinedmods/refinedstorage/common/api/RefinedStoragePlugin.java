package com.refinedmods.refinedstorage.common.api;

import org.apiguardian.api.API;

// TODO: Introduce NetworkNodeContainerProvider
// TODO: Use platform to get access to NetworkNodeProvider instead of instanceof checks.
// TODO: Rename to RS entrypoint and move to fabric-api.
@API(status = API.Status.STABLE, since = "2.0.0-milestone.3.13")
public interface RefinedStoragePlugin {
    void onApiAvailable(RefinedStorageApi api);
}
