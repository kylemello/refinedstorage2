package com.refinedmods.refinedstorage.common.content;

import com.refinedmods.refinedstorage.common.misc.ProcessorItem;
import com.refinedmods.refinedstorage.common.storage.FluidStorageVariant;
import com.refinedmods.refinedstorage.common.storage.ItemStorageVariant;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public final class CreativeModeTabItems {
    private CreativeModeTabItems() {
    }

    public static void append(final Consumer<ItemStack> consumer) {
        appendBlocks(consumer);
        appendItems(consumer);
    }

    private static void appendBlocks(final Consumer<ItemStack> consumer) {
        final Consumer<ItemLike> itemConsumer = item -> consumer.accept(new ItemStack(item));
        Items.INSTANCE.getControllers().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getControllers().forEach(controllerItem -> consumer.accept(
            controllerItem.get().createAtEnergyCapacity()
        ));
        Items.INSTANCE.getCreativeControllers().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getCables().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getImporters().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getExporters().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getExternalStorages().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getConstructors().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getDestructors().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getWirelessTransmitters().stream().map(Supplier::get).forEach(itemConsumer);
        itemConsumer.accept(Blocks.INSTANCE.getDiskDrive());
        appendBlockColors(consumer, Blocks.INSTANCE.getGrid());
        appendBlockColors(consumer, Blocks.INSTANCE.getCraftingGrid());
        appendBlockColors(consumer, Blocks.INSTANCE.getPatternGrid());
        itemConsumer.accept(Items.INSTANCE.getPortableGrid());
        consumer.accept(Items.INSTANCE.getPortableGrid().createAtEnergyCapacity());
        itemConsumer.accept(Items.INSTANCE.getCreativePortableGrid());
        Items.INSTANCE.getDetectors().stream().map(Supplier::get).forEach(itemConsumer);
        itemConsumer.accept(Blocks.INSTANCE.getInterface());
        Arrays.stream(ItemStorageVariant.values()).forEach(variant -> itemConsumer.accept(
            Blocks.INSTANCE.getItemStorageBlock(variant)
        ));
        Arrays.stream(FluidStorageVariant.values()).forEach(variant -> itemConsumer.accept(
            Blocks.INSTANCE.getFluidStorageBlock(variant)
        ));
        itemConsumer.accept(Blocks.INSTANCE.getMachineCasing());
        itemConsumer.accept(Blocks.INSTANCE.getQuartzEnrichedIronBlock());
        itemConsumer.accept(Blocks.INSTANCE.getQuartzEnrichedCopperBlock());
        itemConsumer.accept(Blocks.INSTANCE.getStorageMonitor());
        Items.INSTANCE.getNetworkTransmitters().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getNetworkReceivers().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getSecurityManagers().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getRelays().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getDiskInterfaces().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getAutocrafters().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getAutocrafterManagers().stream().map(Supplier::get).forEach(itemConsumer);
        Items.INSTANCE.getAutocraftingMonitors().stream().map(Supplier::get).forEach(itemConsumer);
    }

    private static void appendBlockColors(final Consumer<ItemStack> consumer, final BlockColorMap<?, ?> map) {
        map.values().forEach(block -> consumer.accept(new ItemStack(block)));
    }

    private static void appendItems(final Consumer<ItemStack> consumer) {
        final Consumer<ItemLike> itemConsumer = item -> consumer.accept(new ItemStack(item));
        itemConsumer.accept(Items.INSTANCE.getQuartzEnrichedIron());
        itemConsumer.accept(Items.INSTANCE.getQuartzEnrichedCopper());
        itemConsumer.accept(Items.INSTANCE.getSilicon());
        itemConsumer.accept(Items.INSTANCE.getProcessorBinding());
        itemConsumer.accept(Items.INSTANCE.getWrench());

        Arrays.stream(ProcessorItem.Type.values()).map(Items.INSTANCE::getProcessor).forEach(itemConsumer);

        itemConsumer.accept(Items.INSTANCE.getConstructionCore());
        itemConsumer.accept(Items.INSTANCE.getDestructionCore());

        Arrays.stream(ItemStorageVariant.values())
            .filter(variant -> variant != ItemStorageVariant.CREATIVE)
            .map(Items.INSTANCE::getItemStoragePart)
            .forEach(itemConsumer);
        Arrays.stream(FluidStorageVariant.values())
            .filter(variant -> variant != FluidStorageVariant.CREATIVE)
            .map(Items.INSTANCE::getFluidStoragePart)
            .forEach(itemConsumer);

        Arrays.stream(ItemStorageVariant.values()).forEach(variant -> itemConsumer.accept(
            Items.INSTANCE.getItemStorageDisk(variant)
        ));
        Arrays.stream(FluidStorageVariant.values()).forEach(variant -> itemConsumer.accept(
            Items.INSTANCE.getFluidStorageDisk(variant)
        ));
        itemConsumer.accept(Items.INSTANCE.getStorageHousing());

        itemConsumer.accept(Items.INSTANCE.getUpgrade());
        itemConsumer.accept(Items.INSTANCE.getSpeedUpgrade());
        itemConsumer.accept(Items.INSTANCE.getStackUpgrade());
        itemConsumer.accept(Items.INSTANCE.getFortune1Upgrade());
        itemConsumer.accept(Items.INSTANCE.getFortune2Upgrade());
        itemConsumer.accept(Items.INSTANCE.getFortune3Upgrade());
        itemConsumer.accept(Items.INSTANCE.getSilkTouchUpgrade());
        itemConsumer.accept(Items.INSTANCE.getRegulatorUpgrade());
        itemConsumer.accept(Items.INSTANCE.getRangeUpgrade());
        itemConsumer.accept(Items.INSTANCE.getCreativeRangeUpgrade());
        itemConsumer.accept(Items.INSTANCE.getWirelessGrid());
        consumer.accept(Items.INSTANCE.getWirelessGrid().createAtEnergyCapacity());
        itemConsumer.accept(Items.INSTANCE.getCreativeWirelessGrid());
        itemConsumer.accept(Items.INSTANCE.getConfigurationCard());
        itemConsumer.accept(Items.INSTANCE.getNetworkCard());
        itemConsumer.accept(Items.INSTANCE.getSecurityCard());
        itemConsumer.accept(Items.INSTANCE.getFallbackSecurityCard());
        itemConsumer.accept(Items.INSTANCE.getPattern());
        itemConsumer.accept(Items.INSTANCE.getWirelessAutocraftingMonitor());
        consumer.accept(Items.INSTANCE.getWirelessAutocraftingMonitor().createAtEnergyCapacity());
        itemConsumer.accept(Items.INSTANCE.getCreativeWirelessAutocraftingMonitor());
    }
}
