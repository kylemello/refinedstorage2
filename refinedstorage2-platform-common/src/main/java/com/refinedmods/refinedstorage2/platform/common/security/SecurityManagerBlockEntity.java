package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage2.platform.common.Platform;
import com.refinedmods.refinedstorage2.platform.common.content.BlockEntities;
import com.refinedmods.refinedstorage2.platform.common.support.network.AbstractRedstoneModeNetworkNodeContainerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class SecurityManagerBlockEntity extends AbstractRedstoneModeNetworkNodeContainerBlockEntity<SimpleNetworkNode> {
    public SecurityManagerBlockEntity(final BlockPos pos, final BlockState state) {
        super(
            BlockEntities.INSTANCE.getSecurityManager(),
            pos,
            state,
            new SimpleNetworkNode(Platform.INSTANCE.getConfig().getSecurityManager().getEnergyUsage())
        );
    }
}
