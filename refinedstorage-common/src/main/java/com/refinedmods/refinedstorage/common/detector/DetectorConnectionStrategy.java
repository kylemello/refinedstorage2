package com.refinedmods.refinedstorage.common.detector;

import com.refinedmods.refinedstorage.common.api.support.network.ConnectionSink;
import com.refinedmods.refinedstorage.common.support.network.ColoredConnectionStrategy;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock.tryExtractDirection;

class DetectorConnectionStrategy extends ColoredConnectionStrategy {
    DetectorConnectionStrategy(final Supplier<BlockState> blockStateProvider, final BlockPos origin) {
        super(blockStateProvider, origin);
    }

    @Override
    public void addOutgoingConnections(final ConnectionSink sink) {
        final Direction myDirection = tryExtractDirection(blockStateProvider.get());
        if (myDirection == null) {
            return;
        }
        for (final Direction direction : Direction.values()) {
            if (direction == myDirection.getOpposite()) {
                continue;
            }
            sink.tryConnectInSameDimension(origin.relative(direction), direction.getOpposite());
        }
    }

    @Override
    public boolean canAcceptIncomingConnection(final Direction incomingDirection, final BlockState connectingState) {
        if (!colorsAllowConnecting(connectingState)) {
            return false;
        }
        final Direction myDirection = tryExtractDirection(blockStateProvider.get());
        if (myDirection != null) {
            return myDirection != incomingDirection.getOpposite();
        }
        return true;
    }
}
