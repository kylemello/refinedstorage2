package com.refinedmods.refinedstorage.neoforge.api;

import com.refinedmods.refinedstorage.api.core.NullableType;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.3.13")
public interface RefinedStorageNeoForgeApi {
    RefinedStorageNeoForgeApi INSTANCE = new ProxyRefinedStorageNeoForgeApi();

    BlockCapability<NetworkNodeContainerProvider, @NullableType Direction> getNetworkNodeContainerProviderCapability();
}
