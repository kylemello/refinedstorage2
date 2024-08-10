package com.refinedmods.refinedstorage.common;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.autocrafting.PatternGridScreen;
import com.refinedmods.refinedstorage.common.constructordestructor.ConstructorScreen;
import com.refinedmods.refinedstorage.common.constructordestructor.DestructorScreen;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.content.KeyMappings;
import com.refinedmods.refinedstorage.common.content.Menus;
import com.refinedmods.refinedstorage.common.controller.ControllerScreen;
import com.refinedmods.refinedstorage.common.detector.DetectorScreen;
import com.refinedmods.refinedstorage.common.exporter.ExporterScreen;
import com.refinedmods.refinedstorage.common.grid.GridContainerMenu;
import com.refinedmods.refinedstorage.common.grid.WirelessGridContainerMenu;
import com.refinedmods.refinedstorage.common.grid.screen.CraftingGridScreen;
import com.refinedmods.refinedstorage.common.grid.screen.GridScreen;
import com.refinedmods.refinedstorage.common.grid.screen.hint.FluidGridInsertionHint;
import com.refinedmods.refinedstorage.common.iface.InterfaceScreen;
import com.refinedmods.refinedstorage.common.importer.ImporterScreen;
import com.refinedmods.refinedstorage.common.networking.NetworkTransmitterScreen;
import com.refinedmods.refinedstorage.common.networking.RelayScreen;
import com.refinedmods.refinedstorage.common.security.FallbackSecurityCardScreen;
import com.refinedmods.refinedstorage.common.security.SecurityCardScreen;
import com.refinedmods.refinedstorage.common.security.SecurityManagerScreen;
import com.refinedmods.refinedstorage.common.storage.FluidStorageVariant;
import com.refinedmods.refinedstorage.common.storage.ItemStorageVariant;
import com.refinedmods.refinedstorage.common.storage.diskdrive.DiskDriveScreen;
import com.refinedmods.refinedstorage.common.storage.diskinterface.DiskInterfaceScreen;
import com.refinedmods.refinedstorage.common.storage.externalstorage.ExternalStorageScreen;
import com.refinedmods.refinedstorage.common.storage.portablegrid.PortableGridScreen;
import com.refinedmods.refinedstorage.common.storage.storageblock.FluidStorageBlockScreen;
import com.refinedmods.refinedstorage.common.storage.storageblock.ItemStorageBlockScreen;
import com.refinedmods.refinedstorage.common.storagemonitor.StorageMonitorScreen;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.FluidResourceRendering;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResourceRendering;
import com.refinedmods.refinedstorage.common.upgrade.RegulatorUpgradeScreen;
import com.refinedmods.refinedstorage.common.wirelesstransmitter.WirelessTransmitterScreen;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public abstract class AbstractClientModInitializer {
    protected static void registerScreens(final ScreenRegistration registration) {
        registration.register(Menus.INSTANCE.getDiskDrive(), DiskDriveScreen::new);
        registration.register(Menus.INSTANCE.getGrid(), GridScreen<GridContainerMenu>::new);
        registration.register(Menus.INSTANCE.getCraftingGrid(), CraftingGridScreen::new);
        registration.register(Menus.INSTANCE.getPatternGrid(), PatternGridScreen::new);
        registration.register(Menus.INSTANCE.getWirelessGrid(), GridScreen<WirelessGridContainerMenu>::new);
        registration.register(Menus.INSTANCE.getController(), ControllerScreen::new);
        registration.register(Menus.INSTANCE.getItemStorage(), ItemStorageBlockScreen::new);
        registration.register(Menus.INSTANCE.getFluidStorage(), FluidStorageBlockScreen::new);
        registration.register(Menus.INSTANCE.getImporter(), ImporterScreen::new);
        registration.register(Menus.INSTANCE.getExporter(), ExporterScreen::new);
        registration.register(Menus.INSTANCE.getInterface(), InterfaceScreen::new);
        registration.register(Menus.INSTANCE.getExternalStorage(), ExternalStorageScreen::new);
        registration.register(Menus.INSTANCE.getDetector(), DetectorScreen::new);
        registration.register(Menus.INSTANCE.getDestructor(), DestructorScreen::new);
        registration.register(Menus.INSTANCE.getConstructor(), ConstructorScreen::new);
        registration.register(Menus.INSTANCE.getRegulatorUpgrade(), RegulatorUpgradeScreen::new);
        registration.register(Menus.INSTANCE.getWirelessTransmitter(), WirelessTransmitterScreen::new);
        registration.register(Menus.INSTANCE.getStorageMonitor(), StorageMonitorScreen::new);
        registration.register(Menus.INSTANCE.getNetworkTransmitter(), NetworkTransmitterScreen::new);
        registration.register(Menus.INSTANCE.getPortableGridBlock(), PortableGridScreen::new);
        registration.register(Menus.INSTANCE.getPortableGridItem(), PortableGridScreen::new);
        registration.register(Menus.INSTANCE.getSecurityCard(), SecurityCardScreen::new);
        registration.register(Menus.INSTANCE.getFallbackSecurityCard(), FallbackSecurityCardScreen::new);
        registration.register(Menus.INSTANCE.getSecurityManager(), SecurityManagerScreen::new);
        registration.register(Menus.INSTANCE.getRelay(), RelayScreen::new);
        registration.register(Menus.INSTANCE.getDiskInterface(), DiskInterfaceScreen::new);
    }

    protected static void registerAlternativeGridHints() {
        RefinedStorageApi.INSTANCE.addAlternativeGridInsertionHint(new FluidGridInsertionHint());
    }

    protected static void registerResourceRendering() {
        RefinedStorageApi.INSTANCE.registerResourceRendering(ItemResource.class, new ItemResourceRendering());
        RefinedStorageApi.INSTANCE.registerResourceRendering(FluidResource.class, new FluidResourceRendering(
            Platform.INSTANCE.getBucketAmount()
        ));
    }

    protected static void handleInputEvents() {
        final Player player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        final KeyMapping openWirelessGrid = KeyMappings.INSTANCE.getOpenWirelessGrid();
        while (openWirelessGrid != null && openWirelessGrid.consumeClick()) {
            RefinedStorageApi.INSTANCE.useSlotReferencedItem(
                player,
                Items.INSTANCE.getWirelessGrid(),
                Items.INSTANCE.getCreativeWirelessGrid()
            );
        }
        final KeyMapping openPortableGrid = KeyMappings.INSTANCE.getOpenPortableGrid();
        while (openPortableGrid != null && openPortableGrid.consumeClick()) {
            RefinedStorageApi.INSTANCE.useSlotReferencedItem(
                player,
                Items.INSTANCE.getPortableGrid(),
                Items.INSTANCE.getCreativePortableGrid()
            );
        }
    }

    protected static void registerDiskModels() {
        final ResourceLocation diskModel = createIdentifier("block/disk/disk");
        for (final ItemStorageVariant variant : ItemStorageVariant.values()) {
            RefinedStorageApi.INSTANCE.getStorageContainerItemHelper().registerDiskModel(
                Items.INSTANCE.getItemStorageDisk(variant),
                diskModel
            );
        }

        final ResourceLocation fluidDiskModel = createIdentifier("block/disk/fluid_disk");
        for (final FluidStorageVariant variant : FluidStorageVariant.values()) {
            RefinedStorageApi.INSTANCE.getStorageContainerItemHelper().registerDiskModel(
                Items.INSTANCE.getFluidStorageDisk(variant),
                fluidDiskModel
            );
        }
    }

    @FunctionalInterface
    public interface ScreenRegistration {
        <M extends AbstractContainerMenu, U extends Screen & MenuAccess<M>> void register(MenuType<? extends M> type,
                                                                                          ScreenConstructor<M, U>
                                                                                              factory);
    }

    @FunctionalInterface
    public interface ScreenConstructor<T extends AbstractContainerMenu, U extends Screen & MenuAccess<T>> {
        U create(T menu, Inventory inventory, Component title);
    }
}
