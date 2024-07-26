package com.refinedmods.refinedstorage.common.support.network;

import com.refinedmods.refinedstorage.common.api.support.network.ConnectionSink;
import com.refinedmods.refinedstorage.common.api.support.network.ConnectionStrategy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleConnectionStrategy implements ConnectionStrategy {
    protected final BlockPos origin;

    public SimpleConnectionStrategy(final BlockPos origin) {
        this.origin = origin;
    }

    @Override
    public void addOutgoingConnections(final ConnectionSink sink) {
        for (final Direction direction : Direction.values()) {
            sink.tryConnectInSameDimension(origin.relative(direction), direction.getOpposite());
        }
    }

    @Override
    public boolean canAcceptIncomingConnection(final Direction incomingDirection, final BlockState connectingState) {
        return true;
    }
}
