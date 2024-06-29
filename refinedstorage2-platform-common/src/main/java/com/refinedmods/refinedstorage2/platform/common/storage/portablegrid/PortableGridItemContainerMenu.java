package com.refinedmods.refinedstorage2.platform.common.storage.portablegrid;

import com.refinedmods.refinedstorage2.api.network.energy.EnergyStorage;
import com.refinedmods.refinedstorage2.platform.api.grid.Grid;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.content.Menus;
import com.refinedmods.refinedstorage2.platform.common.grid.PortableGridData;
import com.refinedmods.refinedstorage2.platform.common.storage.DiskInventory;

import net.minecraft.world.entity.player.Inventory;

public class PortableGridItemContainerMenu extends AbstractPortableGridContainerMenu {
    public PortableGridItemContainerMenu(final int syncId,
                                         final Inventory playerInventory,
                                         final PortableGridData portableGridData) {
        super(Menus.INSTANCE.getPortableGridItem(), syncId, playerInventory, portableGridData);
        this.disabledSlot = portableGridData.slotReference().orElse(null);
        this.onScreenReady(0);
    }

    PortableGridItemContainerMenu(final int syncId,
                                  final Inventory playerInventory,
                                  final DiskInventory diskInventory,
                                  final Grid grid,
                                  final EnergyStorage energyStorage,
                                  final SlotReference slotReference) {
        super(
            Menus.INSTANCE.getPortableGridItem(),
            syncId,
            playerInventory,
            diskInventory,
            grid,
            energyStorage
        );
        this.disabledSlot = slotReference;
        this.onScreenReady(0);
    }
}
