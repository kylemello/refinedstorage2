package com.refinedmods.refinedstorage.common.autocrafting.monitor;

import com.refinedmods.refinedstorage.api.autocrafting.status.TaskStatusProvider;
import com.refinedmods.refinedstorage.common.content.Menus;
import com.refinedmods.refinedstorage.common.support.RedstoneMode;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class AutocraftingMonitorContainerMenu extends AbstractAutocraftingMonitorContainerMenu {
    @Nullable
    private final AutocraftingMonitorBlockEntity autocraftingMonitor;

    public AutocraftingMonitorContainerMenu(final int syncId,
                                            final Inventory playerInventory,
                                            final AutocraftingMonitorData data) {
        super(Menus.INSTANCE.getAutocraftingMonitor(), syncId, playerInventory, data);
        registerProperty(new ClientProperty<>(PropertyTypes.REDSTONE_MODE, RedstoneMode.IGNORE));
        this.autocraftingMonitor = null;
    }

    AutocraftingMonitorContainerMenu(final int syncId,
                                     final Player player,
                                     final TaskStatusProvider taskStatusProvider,
                                     final AutocraftingMonitorBlockEntity autocraftingMonitor) {
        super(Menus.INSTANCE.getAutocraftingMonitor(), syncId, player, taskStatusProvider);
        registerProperty(new ServerProperty<>(
            PropertyTypes.REDSTONE_MODE,
            autocraftingMonitor::getRedstoneMode,
            autocraftingMonitor::setRedstoneMode
        ));
        this.autocraftingMonitor = autocraftingMonitor;
        autocraftingMonitor.addWatcher(this);
    }

    @Override
    public void removed(final Player removedPlayer) {
        super.removed(removedPlayer);
        if (autocraftingMonitor != null) {
            autocraftingMonitor.removeWatcher(this);
        }
    }
}
