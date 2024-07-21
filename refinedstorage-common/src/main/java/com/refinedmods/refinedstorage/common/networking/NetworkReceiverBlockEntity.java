package com.refinedmods.refinedstorage.common.networking;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.PlatformApi;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.support.network.BaseNetworkNodeContainerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class NetworkReceiverBlockEntity extends BaseNetworkNodeContainerBlockEntity<SimpleNetworkNode> {
    public NetworkReceiverBlockEntity(final BlockPos pos, final BlockState state) {
        super(
            BlockEntities.INSTANCE.getNetworkReceiver(),
            pos,
            state,
            new SimpleNetworkNode(Platform.INSTANCE.getConfig().getNetworkReceiver().getEnergyUsage())
        );
    }

    @Override
    protected InWorldNetworkNodeContainer createMainContainer(final SimpleNetworkNode node) {
        return PlatformApi.INSTANCE.createInWorldNetworkNodeContainer(
            this,
            node,
            MAIN_CONTAINER_NAME,
            0,
            this,
            () -> new NetworkReceiverKey(mainContainer.getPosition())
        );
    }
}
