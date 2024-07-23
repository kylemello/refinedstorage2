package com.refinedmods.refinedstorage.common.grid;

import com.refinedmods.refinedstorage.api.network.energy.EnergyStorage;
import com.refinedmods.refinedstorage.api.network.impl.energy.EnergyStorageImpl;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.PlatformApi;
import com.refinedmods.refinedstorage.common.api.grid.Grid;
import com.refinedmods.refinedstorage.common.api.security.SecurityHelper;
import com.refinedmods.refinedstorage.common.api.support.energy.AbstractNetworkEnergyItem;
import com.refinedmods.refinedstorage.common.api.support.network.item.NetworkItemContext;
import com.refinedmods.refinedstorage.common.api.support.slotreference.SlotReference;
import com.refinedmods.refinedstorage.common.content.ContentNames;
import com.refinedmods.refinedstorage.common.security.BuiltinPermission;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class WirelessGridItem extends AbstractNetworkEnergyItem {
    public WirelessGridItem() {
        super(
            new Item.Properties().stacksTo(1),
            PlatformApi.INSTANCE.getEnergyItemHelper(),
            PlatformApi.INSTANCE.getNetworkItemHelper()
        );
    }

    public EnergyStorage createEnergyStorage(final ItemStack stack) {
        final EnergyStorage energyStorage = new EnergyStorageImpl(
            Platform.INSTANCE.getConfig().getWirelessGrid().getEnergyCapacity()
        );
        return PlatformApi.INSTANCE.asItemEnergyStorage(energyStorage, stack);
    }

    @Override
    protected void use(final ServerPlayer player,
                       final SlotReference slotReference,
                       final NetworkItemContext context) {
        final boolean isAllowed = context.resolveNetwork()
            .map(network -> SecurityHelper.isAllowed(player, BuiltinPermission.OPEN, network))
            .orElse(true); // if the network can't be resolved that will be apparent later in the UI.
        if (!isAllowed) {
            PlatformApi.INSTANCE.sendNoPermissionToOpenMessage(player, ContentNames.WIRELESS_GRID);
            return;
        }
        final Grid grid = new WirelessGrid(context);
        Platform.INSTANCE.getMenuOpener().openMenu(player, new WirelessGridExtendedMenuProvider(grid, slotReference));
    }
}
