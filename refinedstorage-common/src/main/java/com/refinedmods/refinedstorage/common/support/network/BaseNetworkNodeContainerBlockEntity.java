package com.refinedmods.refinedstorage.common.support.network;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.energy.EnergyNetworkComponent;
import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.configurationcard.ConfigurationCardTarget;
import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.api.support.network.item.NetworkItemTargetBlockEntity;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.RateLimiter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock.tryExtractDirection;

public class BaseNetworkNodeContainerBlockEntity<T extends AbstractNetworkNode>
    extends AbstractNetworkNodeContainerBlockEntity<T>
    implements NetworkItemTargetBlockEntity, ConfigurationCardTarget {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseNetworkNodeContainerBlockEntity.class);
    private static final String TAG_CUSTOM_NAME = "CustomName";

    private final RateLimiter activenessChangeRateLimiter = RateLimiter.create(1);

    @Nullable
    private Component name;

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
        if (level instanceof ServerLevel serverLevel) {
            initialize(serverLevel);
        }
        if (!doesBlockStateChangeWarrantNetworkNodeUpdate(oldBlockState, newBlockState)) {
            return;
        }
        containers.update(level);
    }

    @Override
    public void setLevel(final Level level) {
        super.setLevel(level);
        if (level instanceof ServerLevel serverLevel) {
            initialize(serverLevel);
        }
    }

    protected final void initialize(final ServerLevel level) {
        final Direction direction = tryExtractDirection(getBlockState());
        if (direction == null) {
            LOGGER.warn(
                "Failed to initialize: could not extract direction from block at {}, state is {}",
                worldPosition,
                getBlockState()
            );
            return;
        }
        initialize(level, direction);
    }

    protected void initialize(final ServerLevel level, final Direction direction) {
        // no op
    }

    @Nullable
    @Override
    public Network getNetworkForItem() {
        return mainNetworkNode.getNetwork();
    }

    @Override
    public void saveAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        writeConfiguration(tag, provider);
    }

    @Override
    public void loadAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        readConfiguration(tag, provider);
    }

    @Override
    public void writeConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        if (name != null) {
            tag.putString(TAG_CUSTOM_NAME, Component.Serializer.toJson(name, provider));
        }
    }

    @Override
    public void readConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        if (tag.contains(TAG_CUSTOM_NAME, Tag.TAG_STRING)) {
            this.name = parseCustomNameSafe(tag.getString(TAG_CUSTOM_NAME), provider);
        }
    }

    @Override
    protected void applyImplicitComponents(final BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.name = componentInput.get(DataComponents.CUSTOM_NAME);
    }

    @Override
    protected void collectImplicitComponents(final DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, name);
    }

    protected final Component getName(final Component defaultName) {
        return name == null ? defaultName : name;
    }
}
