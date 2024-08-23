package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CrafterBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity<SimpleNetworkNode> {
    public CrafterBlockEntity(final BlockPos pos, final BlockState state) {
        super(
            BlockEntities.INSTANCE.getCrafter(),
            pos,
            state,
            new SimpleNetworkNode(Platform.INSTANCE.getConfig().getCrafter().getEnergyUsage())
        );
    }

    @Override
    protected InWorldNetworkNodeContainer createMainContainer(final SimpleNetworkNode networkNode) {
        return RefinedStorageApi.INSTANCE.createNetworkNodeContainer(this, networkNode)
            .connectionStrategy(new CrafterConnectionStrategy(this::getBlockState, getBlockPos()))
            .build();
    }
}
