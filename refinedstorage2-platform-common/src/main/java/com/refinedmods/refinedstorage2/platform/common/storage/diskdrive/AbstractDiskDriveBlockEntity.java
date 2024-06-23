package com.refinedmods.refinedstorage2.platform.common.storage.diskdrive;

import com.refinedmods.refinedstorage2.api.network.impl.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage2.api.resource.ResourceKey;
import com.refinedmods.refinedstorage2.platform.common.Platform;
import com.refinedmods.refinedstorage2.platform.common.content.BlockEntities;
import com.refinedmods.refinedstorage2.platform.common.content.ContentNames;
import com.refinedmods.refinedstorage2.platform.common.storage.AbstractDiskContainerBlockEntity;
import com.refinedmods.refinedstorage2.platform.common.storage.StorageConfigurationContainerImpl;

import java.util.Set;
import java.util.function.UnaryOperator;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractDiskDriveBlockEntity extends AbstractDiskContainerBlockEntity<StorageNetworkNode> {
    static final int AMOUNT_OF_DISKS = 8;

    private final StorageConfigurationContainerImpl configContainer;

    protected AbstractDiskDriveBlockEntity(final BlockPos pos, final BlockState state) {
        super(BlockEntities.INSTANCE.getDiskDrive(), pos, state, new StorageNetworkNode(
            Platform.INSTANCE.getConfig().getDiskDrive().getEnergyUsage(),
            Platform.INSTANCE.getConfig().getDiskDrive().getEnergyUsagePerDisk(),
            AMOUNT_OF_DISKS
        ));
        this.configContainer = new StorageConfigurationContainerImpl(
            mainNode.getStorageConfiguration(),
            filter,
            this::setChanged,
            this::getRedstoneMode,
            this::setRedstoneMode
        );
    }

    @Override
    protected void setFilters(final Set<ResourceKey> filters) {
        mainNode.getStorageConfiguration().setFilters(filters);
    }

    @Override
    protected void setNormalizer(final UnaryOperator<ResourceKey> normalizer) {
        mainNode.getStorageConfiguration().setNormalizer(normalizer);
    }

    @Override
    public void readConfiguration(final CompoundTag tag) {
        super.readConfiguration(tag);
        configContainer.load(tag);
    }

    @Override
    public void writeConfiguration(final CompoundTag tag) {
        super.writeConfiguration(tag);
        configContainer.save(tag);
    }

    @Override
    public Component getDisplayName() {
        return ContentNames.DISK_DRIVE;
    }

    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inv, final Player player) {
        return new DiskDriveContainerMenu(
            syncId,
            player,
            diskInventory,
            filter.getFilterContainer(),
            configContainer,
            new EmptyStorageDiskInfoAccessor()
        );
    }
}
