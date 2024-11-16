package com.refinedmods.refinedstorage.common.autocrafting.monitor;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.content.ContentNames;
import com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class AutocraftingMonitorBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity<SimpleNetworkNode>
    implements NetworkNodeExtendedMenuProvider<AutocraftingMonitorData> {
    private final Set<AutocraftingMonitorWatcher> watchers = new HashSet<>();

    public AutocraftingMonitorBlockEntity(final BlockPos pos, final BlockState state) {
        super(BlockEntities.INSTANCE.getAutocraftingMonitor(), pos, state, new SimpleNetworkNode(
            Platform.INSTANCE.getConfig().getAutocraftingMonitor().getEnergyUsage()
        ));
    }

    void addWatcher(final AutocraftingMonitorWatcher watcher) {
        watchers.add(watcher);
    }

    void removeWatcher(final AutocraftingMonitorWatcher watcher) {
        watchers.remove(watcher);
    }

    @Override
    protected void activenessChanged(final boolean newActive) {
        super.activenessChanged(newActive);
        watchers.forEach(watcher -> watcher.activeChanged(newActive));
    }

    @Override
    public Component getName() {
        return overrideName(ContentNames.AUTOCRAFTING_MONITOR);
    }

    @Override
    protected boolean doesBlockStateChangeWarrantNetworkNodeUpdate(final BlockState oldBlockState,
                                                                   final BlockState newBlockState) {
        return AbstractDirectionalBlock.didDirectionChange(oldBlockState, newBlockState);
    }

    @Override
    public AutocraftingMonitorData getMenuData() {
        return new AutocraftingMonitorData(new TaskStatusProviderImpl().getStatuses(), mainNetworkNode.isActive());
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, AutocraftingMonitorData> getMenuCodec() {
        return AutocraftingMonitorData.STREAM_CODEC;
    }

    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new AutocraftingMonitorContainerMenu(syncId, player, new TaskStatusProviderImpl(), this);
    }
}
