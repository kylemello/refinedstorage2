package com.refinedmods.refinedstorage.common.storage.storageblock;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.content.Menus;
import com.refinedmods.refinedstorage.common.storage.StorageConfigurationContainer;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

public class ItemStorageBlockContainerMenu extends AbstractStorageBlockContainerMenu {
    public ItemStorageBlockContainerMenu(final int syncId,
                                         final Inventory playerInventory,
                                         final StorageBlockData storageBlockData) {
        super(
            Menus.INSTANCE.getItemStorage(),
            syncId,
            playerInventory.player,
            storageBlockData,
            RefinedStorageApi.INSTANCE.getItemResourceFactory()
        );
    }

    ItemStorageBlockContainerMenu(final int syncId,
                                  final Player player,
                                  final ResourceContainer resourceContainer,
                                  final StorageConfigurationContainer configContainer) {
        super(Menus.INSTANCE.getItemStorage(), syncId, player, resourceContainer, configContainer);
    }

    @Override
    public boolean hasCapacity() {
        return getCapacity() > 0;
    }
}
