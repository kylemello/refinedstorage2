package com.refinedmods.refinedstorage.common.support.network;

import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.common.support.PlayerAwareBlockEntity;
import com.refinedmods.refinedstorage.common.support.RedstoneMode;
import com.refinedmods.refinedstorage.common.support.RedstoneModeSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractRedstoneModeNetworkNodeContainerBlockEntity<T extends AbstractNetworkNode>
    extends BaseNetworkNodeContainerBlockEntity<T> implements PlayerAwareBlockEntity {
    private static final String TAG_REDSTONE_MODE = "rm";

    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;

    protected AbstractRedstoneModeNetworkNodeContainerBlockEntity(final BlockEntityType<?> type,
                                                                  final BlockPos pos,
                                                                  final BlockState state,
                                                                  final T node) {
        super(type, pos, state, node);
    }

    @Override
    protected boolean calculateActive() {
        return super.calculateActive()
            && level != null
            && redstoneMode.isActive(level.hasNeighborSignal(worldPosition));
    }

    @Override
    public void writeConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.writeConfiguration(tag, provider);
        tag.putInt(TAG_REDSTONE_MODE, RedstoneModeSettings.getRedstoneMode(getRedstoneMode()));
    }

    @Override
    public void readConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.readConfiguration(tag, provider);
        if (tag.contains(TAG_REDSTONE_MODE)) {
            redstoneMode = RedstoneModeSettings.getRedstoneMode(tag.getInt(TAG_REDSTONE_MODE));
        }
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(final RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;
        setChanged();
    }
}
