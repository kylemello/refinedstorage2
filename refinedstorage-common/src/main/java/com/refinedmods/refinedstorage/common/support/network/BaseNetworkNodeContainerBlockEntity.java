package com.refinedmods.refinedstorage.common.support.network;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.energy.EnergyNetworkComponent;
import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.api.support.network.item.NetworkItemTargetBlockEntity;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.RateLimiter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseNetworkNodeContainerBlockEntity<T extends AbstractNetworkNode>
    extends AbstractNetworkNodeContainerBlockEntity<T> implements NetworkItemTargetBlockEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseNetworkNodeContainerBlockEntity.class);

    private final RateLimiter activenessChangeRateLimiter = RateLimiter.create(1);

    public BaseNetworkNodeContainerBlockEntity(final BlockEntityType<?> type,
                                               final BlockPos pos,
                                               final BlockState state,
                                               final T networkNode) {
        super(type, pos, state, networkNode);
    }

    @Override
    protected InWorldNetworkNodeContainer createMainContainer(final T networkNode) {
        return RefinedStorageApi.INSTANCE.createNetworkNodeContainer(this, networkNode)
            .connectionStrategy(new ColoredConnectionStrategy(this::getBlockState, getBlockPos()))
            .build();
    }

    protected boolean calculateActive() {
        final long energyUsage = mainNetworkNode.getEnergyUsage();
        final boolean hasLevel = level != null && level.isLoaded(worldPosition);
        return hasLevel
            && mainNetworkNode.getNetwork() != null
            && mainNetworkNode.getNetwork().getComponent(EnergyNetworkComponent.class).getStored() >= energyUsage;
    }

    public void updateActiveness(final BlockState state, @Nullable final BooleanProperty activenessProperty) {
        final boolean newActive = calculateActive();
        final boolean nodeActivenessNeedsUpdate = newActive != mainNetworkNode.isActive();
        final boolean blockStateActivenessNeedsUpdate = activenessProperty != null
            && state.getValue(activenessProperty) != newActive;
        final boolean activenessNeedsUpdate = nodeActivenessNeedsUpdate || blockStateActivenessNeedsUpdate;
        if (activenessNeedsUpdate && activenessChangeRateLimiter.tryAcquire()) {
            if (nodeActivenessNeedsUpdate) {
                activenessChanged(newActive);
            }
            if (blockStateActivenessNeedsUpdate) {
                updateActivenessBlockState(state, activenessProperty, newActive);
            }
        }
    }

    protected void activenessChanged(final boolean newActive) {
        LOGGER.debug(
            "Activeness change for node at {}: {} -> {}",
            getBlockPos(),
            mainNetworkNode.isActive(),
            newActive
        );
        mainNetworkNode.setActive(newActive);
    }

    private void updateActivenessBlockState(final BlockState state,
                                            final BooleanProperty activenessProperty,
                                            final boolean active) {
        if (level != null) {
            LOGGER.debug(
                "Sending block update at {} due to activeness change: {} -> {}",
                getBlockPos(),
                state.getValue(activenessProperty),
                active
            );
            level.setBlockAndUpdate(getBlockPos(), state.setValue(activenessProperty, active));
        }
    }

    public void doWork() {
        mainNetworkNode.doWork();
    }

    protected boolean doesBlockStateChangeWarrantNetworkNodeUpdate(
        final BlockState oldBlockState,
        final BlockState newBlockState
    ) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setBlockState(final BlockState newBlockState) {
        final BlockState oldBlockState = getBlockState();
        super.setBlockState(newBlockState);
        if (!doesBlockStateChangeWarrantNetworkNodeUpdate(oldBlockState, newBlockState)) {
            return;
        }
        containers.update(level);
    }

    @Nullable
    @Override
    public Network getNetworkForItem() {
        return mainNetworkNode.getNetwork();
    }
}
