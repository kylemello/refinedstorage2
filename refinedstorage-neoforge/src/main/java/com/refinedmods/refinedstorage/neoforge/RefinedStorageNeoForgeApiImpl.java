package com.refinedmods.refinedstorage.neoforge;

import com.refinedmods.refinedstorage.api.core.NullableType;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public class RefinedStorageNeoForgeApiImpl implements RefinedStorageNeoForgeApi {
    private final BlockCapability<NetworkNodeContainerProvider, @NullableType Direction>
        networkNodeContainerProviderCapability = BlockCapability.create(
        createIdentifier("network_node_container_provider"),
        NetworkNodeContainerProvider.class,
        Direction.class
    );

    @Override
    public BlockCapability<NetworkNodeContainerProvider, Direction> getNetworkNodeContainerProviderCapability() {
        return networkNodeContainerProviderCapability;
    }
}
