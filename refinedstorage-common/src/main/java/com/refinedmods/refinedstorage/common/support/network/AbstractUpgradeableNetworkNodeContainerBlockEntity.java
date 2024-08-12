package com.refinedmods.refinedstorage.common.support.network;

import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.common.support.BlockEntityWithDrops;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;
import com.refinedmods.refinedstorage.common.util.ContainerUtil;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractUpgradeableNetworkNodeContainerBlockEntity<T extends AbstractNetworkNode>
    extends BaseNetworkNodeContainerBlockEntity<T>
    implements BlockEntityWithDrops {
    private static final String TAG_UPGRADES = "upgr";

    protected final UpgradeContainer upgradeContainer;

    protected AbstractUpgradeableNetworkNodeContainerBlockEntity(
        final BlockEntityType<?> type,
        final BlockPos pos,
        final BlockState state,
        final T node,
        final UpgradeDestinations destination
    ) {
        super(type, pos, state, node);
        this.upgradeContainer = new UpgradeContainer(destination, (rate, upgradeEnergyUsage) -> {
            setWorkTickRate(rate);
            setEnergyUsage(upgradeEnergyUsage);
            setChanged();
            if (level instanceof ServerLevel serverLevel) {
                initialize(serverLevel);
            }
        });
    }

    @Override
    public List<Item> getUpgradeItems() {
        return upgradeContainer.getUpgradeItems();
    }

    @Override
    public boolean addUpgradeItem(final Item upgradeItem) {
        return upgradeContainer.addUpgradeItem(upgradeItem);
    }

    @Override
    public void saveAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(TAG_UPGRADES, ContainerUtil.write(upgradeContainer, provider));
    }

    @Override
    public void loadAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        if (tag.contains(TAG_UPGRADES)) {
            ContainerUtil.read(tag.getCompound(TAG_UPGRADES), upgradeContainer, provider);
        }
        super.loadAdditional(tag, provider);
    }

    @Override
    public final NonNullList<ItemStack> getDrops() {
        return upgradeContainer.getDrops();
    }

    protected abstract void setEnergyUsage(long upgradeEnergyUsage);
}
