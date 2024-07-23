package com.refinedmods.refinedstorage.common.networking;

import com.refinedmods.refinedstorage.common.api.support.network.ConnectionSink;
import com.refinedmods.refinedstorage.common.api.support.network.ConnectionStrategy;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

class RelayInputConnectionStrategy implements ConnectionStrategy {
    private final RelayBlockEntity blockEntity;

    RelayInputConnectionStrategy(final RelayBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public void addOutgoingConnections(final ConnectionSink sink) {
        final Direction direction = blockEntity.getDirectionInternal();
        for (final Direction otherDirection : Direction.values()) {
            if (otherDirection != direction || (blockEntity.isPassThrough() && blockEntity.isActiveInternal())) {
                sink.tryConnectInSameDimension(
                    blockEntity.getBlockPos().relative(otherDirection),
                    otherDirection.getOpposite()
                );
            }
        }
    }

    @Override
    public boolean canAcceptIncomingConnection(final Direction incomingDirection, final BlockState connectingState) {
        return incomingDirection != blockEntity.getDirectionInternal()
            || (blockEntity.isPassThrough() && blockEntity.isActiveInternal());
    }
}
