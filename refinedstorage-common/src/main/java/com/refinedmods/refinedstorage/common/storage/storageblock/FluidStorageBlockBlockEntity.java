package com.refinedmods.refinedstorage.common.storage.storageblock;

import com.refinedmods.refinedstorage.api.network.impl.node.storage.StorageNetworkNode;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.storage.SerializableStorage;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.storage.FluidStorageVariant;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class FluidStorageBlockBlockEntity extends AbstractStorageBlockBlockEntity {
    private final FluidStorageVariant variant;
    private final Component displayName;

    public FluidStorageBlockBlockEntity(final BlockPos pos,
                                        final BlockState state,
                                        final FluidStorageVariant variant) {
        super(
            BlockEntities.INSTANCE.getFluidStorageBlock(variant),
            pos,
            state,
            new StorageNetworkNode(getEnergyUsage(variant), 0, 1),
            RefinedStorageApi.INSTANCE.getFluidResourceFactory()
        );
        this.variant = variant;
        this.displayName = createTranslation(
            "block",
            String.format("%s_fluid_storage_block", variant.getName())
        );
    }

    private static long getEnergyUsage(final FluidStorageVariant variant) {
        return switch (variant) {
            case SIXTY_FOUR_B -> Platform.INSTANCE.getConfig().getFluidStorageBlock().get64bEnergyUsage();
            case TWO_HUNDRED_FIFTY_SIX_B -> Platform.INSTANCE.getConfig().getFluidStorageBlock().get256bEnergyUsage();
            case THOUSAND_TWENTY_FOUR_B -> Platform.INSTANCE.getConfig().getFluidStorageBlock().get1024bEnergyUsage();
            case FOUR_THOUSAND_NINETY_SIX_B ->
                Platform.INSTANCE.getConfig().getFluidStorageBlock().get4096bEnergyUsage();
            case CREATIVE -> Platform.INSTANCE.getConfig().getFluidStorageBlock().getCreativeEnergyUsage();
        };
    }

    @Override
    protected SerializableStorage createStorage(final Runnable listener) {
        return FluidStorageBlockBlockItem.createStorage(variant, listener);
    }

    @Override
    public Component getDisplayName() {
        return overrideName(displayName);
    }

    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new FluidStorageBlockContainerMenu(
            syncId,
            player,
            getFilterContainer(),
            configContainer
        );
    }
}
