package com.refinedmods.refinedstorage.common.storage.storageblock;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.content.Menus;
import com.refinedmods.refinedstorage.common.storage.StorageConfigurationContainer;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class FluidStorageBlockContainerMenu extends AbstractStorageBlockContainerMenu {
    public FluidStorageBlockContainerMenu(final int syncId,
                                          final Inventory playerInventory,
                                          final StorageBlockData storageBlockData) {
        super(
            Menus.INSTANCE.getFluidStorage(),
            syncId,
            playerInventory.player,
            storageBlockData,
            RefinedStorageApi.INSTANCE.getFluidResourceFactory()
        );
    }

    FluidStorageBlockContainerMenu(final int syncId,
                                   final Player player,
                                   final ResourceContainer resourceContainer,
                                   final StorageConfigurationContainer configContainer) {
        super(Menus.INSTANCE.getFluidStorage(), syncId, player, resourceContainer, configContainer);
    }

    @Override
    public boolean hasCapacity() {
        return getCapacity() > 0;
    }
}
