package com.refinedmods.refinedstorage.common.support.network;

import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.task.TaskExecutor;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.support.FilterWithFuzzyMode;
import com.refinedmods.refinedstorage.common.support.SchedulingMode;
import com.refinedmods.refinedstorage.common.support.SchedulingModeType;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractSchedulingNetworkNodeContainerBlockEntity<T extends AbstractNetworkNode, C>
    extends BaseNetworkNodeContainerBlockEntity<T>
    implements NetworkNodeExtendedMenuProvider<ResourceContainerData> {
    protected final FilterWithFuzzyMode filter;
    private final SchedulingMode<C> schedulingMode;

    protected AbstractSchedulingNetworkNodeContainerBlockEntity(
        final BlockEntityType<?> type,
        final BlockPos pos,
        final BlockState state,
        final T node
    ) {
        super(type, pos, state, node);
        this.schedulingMode = new SchedulingMode<>(this::setChanged, this::setTaskExecutor);
        this.filter = FilterWithFuzzyMode.createAndListenForFilters(
            ResourceContainerImpl.createForFilter(),
            this::setChanged,
            this::setFilters
        );
    }

    @Override
    public void writeConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.writeConfiguration(tag, provider);
        schedulingMode.writeToTag(tag);
        filter.save(tag, provider);
    }

    @Override
    public void readConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.readConfiguration(tag, provider);
        schedulingMode.load(tag);
        filter.load(tag, provider);
    }

    public void setSchedulingModeType(final SchedulingModeType type) {
        schedulingMode.setType(type);
    }

    public SchedulingModeType getSchedulingModeType() {
        return schedulingMode.getType();
    }

    public boolean isFuzzyMode() {
        return filter.isFuzzyMode();
    }

    public void setFuzzyMode(final boolean fuzzyMode) {
        filter.setFuzzyMode(fuzzyMode);
        if (level instanceof ServerLevel serverLevel) {
            initialize(serverLevel);
        }
    }

    @Override
    public ResourceContainerData getMenuData() {
        return ResourceContainerData.of(filter.getFilterContainer());
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, ResourceContainerData> getMenuCodec() {
        return ResourceContainerData.STREAM_CODEC;
    }

    protected abstract void setTaskExecutor(TaskExecutor<C> taskExecutor);

    protected abstract void setFilters(List<ResourceKey> filters);
}
