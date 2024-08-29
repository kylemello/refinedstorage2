package com.refinedmods.refinedstorage.common.storage.storageblock;

import com.refinedmods.refinedstorage.api.network.impl.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.storage.SerializableStorage;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.storage.ItemStorageVariant;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class ItemStorageBlockBlockEntity extends AbstractStorageBlockBlockEntity {
    private final ItemStorageVariant variant;
    private final Component displayName;

    public ItemStorageBlockBlockEntity(final BlockPos pos,
                                       final BlockState state,
                                       final ItemStorageVariant variant) {
        super(
            BlockEntities.INSTANCE.getItemStorageBlock(variant),
            pos,
            state,
            new StorageNetworkNode(getEnergyUsage(variant), 0, 1),
            RefinedStorageApi.INSTANCE.getItemResourceFactory()
        );
        this.variant = variant;
        this.displayName = createTranslation("block", String.format("%s_storage_block", variant.getName()));
    }

    private static long getEnergyUsage(final ItemStorageVariant variant) {
        return switch (variant) {
            case ONE_K -> Platform.INSTANCE.getConfig().getStorageBlock().get1kEnergyUsage();
            case FOUR_K -> Platform.INSTANCE.getConfig().getStorageBlock().get4kEnergyUsage();
            case SIXTEEN_K -> Platform.INSTANCE.getConfig().getStorageBlock().get16kEnergyUsage();
            case SIXTY_FOUR_K -> Platform.INSTANCE.getConfig().getStorageBlock().get64kEnergyUsage();
            case CREATIVE -> Platform.INSTANCE.getConfig().getStorageBlock().getCreativeEnergyUsage();
        };
    }

    @Override
    protected SerializableStorage createStorage(final Runnable listener) {
        return ItemStorageBlockBlockItem.createStorage(variant, listener);
    }

    @Override
    public Component getName() {
        return overrideName(displayName);
    }

    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new ItemStorageBlockContainerMenu(
            syncId,
            player,
            getFilterContainer(),
            configContainer
        );
    }
}
