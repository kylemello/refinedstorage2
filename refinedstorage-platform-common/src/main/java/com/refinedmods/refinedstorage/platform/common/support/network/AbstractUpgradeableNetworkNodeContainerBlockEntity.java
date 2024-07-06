package com.refinedmods.refinedstorage.platform.common.support.network;

import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.platform.api.PlatformApi;
import com.refinedmods.refinedstorage.platform.common.content.Items;
import com.refinedmods.refinedstorage.platform.common.support.BlockEntityWithDrops;
import com.refinedmods.refinedstorage.platform.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.platform.common.upgrade.UpgradeDestinations;
import com.refinedmods.refinedstorage.platform.common.util.ContainerUtil;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUpgradeableNetworkNodeContainerBlockEntity<T extends AbstractNetworkNode>
    extends AbstractLevelInteractingNetworkNodeContainerBlockEntity<T>
    implements BlockEntityWithDrops {
    private static final Logger LOGGER = LoggerFactory.getLogger(
        AbstractUpgradeableNetworkNodeContainerBlockEntity.class
    );

    private static final String TAG_UPGRADES = "upgr";

    protected final UpgradeContainer upgradeContainer;
    private int workTickRate = 9;
    private int workTicks;

    protected AbstractUpgradeableNetworkNodeContainerBlockEntity(
        final BlockEntityType<?> type,
        final BlockPos pos,
        final BlockState state,
        final T node,
        final UpgradeDestinations destination
    ) {
        super(type, pos, state, node);
        this.upgradeContainer = new UpgradeContainer(
            destination,
            PlatformApi.INSTANCE.getUpgradeRegistry(),
            this::upgradeContainerChanged
        );
    }

    @Override
    public final void doWork() {
        if (workTicks++ % workTickRate == 0) {
            super.doWork();
            postDoWork();
        }
    }

    protected void postDoWork() {
    }

    private void upgradeContainerChanged() {
        LOGGER.debug("Reconfiguring {} for upgrades", getBlockPos());
        final int amountOfSpeedUpgrades = upgradeContainer.getAmount(Items.INSTANCE.getSpeedUpgrade());
        this.workTickRate = 9 - (amountOfSpeedUpgrades * 2);
        this.setEnergyUsage(upgradeContainer.getEnergyUsage());
        setChanged();
        if (level instanceof ServerLevel serverLevel) {
            initialize(serverLevel);
        }
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

    protected abstract void setEnergyUsage(long upgradeEnergyUsage);

    @Override
    public final NonNullList<ItemStack> getDrops() {
        final NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < upgradeContainer.getContainerSize(); ++i) {
            drops.add(upgradeContainer.getItem(i));
        }
        return drops;
    }
}
