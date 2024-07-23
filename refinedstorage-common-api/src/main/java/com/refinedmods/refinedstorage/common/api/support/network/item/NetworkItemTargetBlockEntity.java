package com.refinedmods.refinedstorage.common.api.support.network.item;

import com.refinedmods.refinedstorage.api.network.Network;

import javax.annotation.Nullable;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.3.6")
@FunctionalInterface
public interface NetworkItemTargetBlockEntity {
    @Nullable
    Network getNetworkForItem();
}
