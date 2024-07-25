package com.refinedmods.refinedstorage.neoforge.api;

import com.refinedmods.refinedstorage.api.core.NullableType;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.5")
public interface RefinedStorageNeoForgeApi {
    RefinedStorageNeoForgeApi INSTANCE = new RefinedStorageNeoForgeApiProxy();

    BlockCapability<NetworkNodeContainerProvider, @NullableType Direction> getNetworkNodeContainerProviderCapability();
}
