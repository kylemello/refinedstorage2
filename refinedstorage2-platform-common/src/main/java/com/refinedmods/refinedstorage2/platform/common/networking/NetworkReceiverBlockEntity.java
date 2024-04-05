package com.refinedmods.refinedstorage2.platform.common.networking;

import com.refinedmods.refinedstorage2.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage2.platform.common.Platform;
import com.refinedmods.refinedstorage2.platform.common.content.BlockEntities;
import com.refinedmods.refinedstorage2.platform.common.support.network.BaseNetworkNodeContainerBlockEntity;

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
