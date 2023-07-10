package com.refinedmods.refinedstorage2.platform.common.content;

import com.refinedmods.refinedstorage2.platform.api.item.AbstractUpgradeItem;
import com.refinedmods.refinedstorage2.platform.common.internal.storage.type.FluidStorageType;
import com.refinedmods.refinedstorage2.platform.common.internal.storage.type.ItemStorageType;
import com.refinedmods.refinedstorage2.platform.common.item.ProcessorItem;
import com.refinedmods.refinedstorage2.platform.common.item.block.ControllerBlockItem;
import com.refinedmods.refinedstorage2.platform.common.item.block.NamedBlockItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.world.item.Item;

public final class Items {
    public static final Items INSTANCE = new Items();

    private final Map<ItemStorageType.Variant, Supplier<Item>> itemStorageParts
        = new EnumMap<>(ItemStorageType.Variant.class);
    private final Map<FluidStorageType.Variant, Supplier<Item>> fluidStorageParts
        = new EnumMap<>(FluidStorageType.Variant.class);
    private final Map<ItemStorageType.Variant, Supplier<Item>> itemStorageDisks
        = new EnumMap<>(ItemStorageType.Variant.class);
    private final Map<FluidStorageType.Variant, Supplier<Item>> fluidStorageDisks
        = new EnumMap<>(FluidStorageType.Variant.class);
    private final List<Supplier<ControllerBlockItem>> regularControllers = new ArrayList<>();
    private final List<Supplier<? extends Item>> allControllers = new ArrayList<>();
    private final List<Supplier<NamedBlockItem>> allCables = new ArrayList<>();
    private final List<Supplier<NamedBlockItem>> allExporters = new ArrayList<>();
    private final List<Supplier<NamedBlockItem>> allImporters = new ArrayList<>();
    private final List<Supplier<NamedBlockItem>> allExternalStorages = new ArrayList<>();
    private final List<Supplier<NamedBlockItem>> allDetectors = new ArrayList<>();
    private final List<Supplier<NamedBlockItem>> allDestructors = new ArrayList<>();
    private final List<Supplier<NamedBlockItem>> allConstructors = new ArrayList<>();
    @Nullable
    private Supplier<Item> quartzEnrichedIron;
    @Nullable
    private Supplier<Item> silicon;
    @Nullable
    private Supplier<Item> processorBinding;
    @Nullable
    private Supplier<Item> wrench;
    private final Map<ProcessorItem.Type, Supplier<Item>> processors = new EnumMap<>(ProcessorItem.Type.class);
    @Nullable
    private Supplier<Item> constructionCore;
    @Nullable
    private Supplier<Item> destructionCore;
    @Nullable
    private Supplier<Item> storageHousing;
    @Nullable
    private Supplier<Item> upgrade;
    @Nullable
    private Supplier<? extends AbstractUpgradeItem> speedUpgrade;
    @Nullable
    private Supplier<? extends AbstractUpgradeItem> stackUpgrade;
    @Nullable
    private Supplier<? extends AbstractUpgradeItem> fortune1Upgrade;
    @Nullable
    private Supplier<? extends AbstractUpgradeItem> fortune2Upgrade;
    @Nullable
    private Supplier<? extends AbstractUpgradeItem> fortune3Upgrade;
    @Nullable
    private Supplier<? extends AbstractUpgradeItem> silkTouchUpgrade;

    private Items() {
    }

    public Item getItemStoragePart(final ItemStorageType.Variant variant) {
        return itemStorageParts.get(variant).get();
    }

    public void setItemStoragePart(final ItemStorageType.Variant variant, final Supplier<Item> supplier) {
        itemStorageParts.put(variant, supplier);
    }

    public Item getItemStorageDisk(final ItemStorageType.Variant variant) {
        return itemStorageDisks.get(variant).get();
    }

    public void setItemStorageDisk(final ItemStorageType.Variant variant, final Supplier<Item> supplier) {
        itemStorageDisks.put(variant, supplier);
    }

    public Item getFluidStoragePart(final FluidStorageType.Variant type) {
        return fluidStorageParts.get(type).get();
    }

    public void setFluidStoragePart(final FluidStorageType.Variant variant, final Supplier<Item> supplier) {
        fluidStorageParts.put(variant, supplier);
    }

    public Item getFluidStorageDisk(final FluidStorageType.Variant variant) {
        return fluidStorageDisks.get(variant).get();
    }

    public void setFluidStorageDisk(final FluidStorageType.Variant variant, final Supplier<Item> supplier) {
        fluidStorageDisks.put(variant, supplier);
    }

    public void setQuartzEnrichedIron(final Supplier<Item> supplier) {
        this.quartzEnrichedIron = supplier;
    }

    public Item getQuartzEnrichedIron() {
        return Objects.requireNonNull(quartzEnrichedIron).get();
    }

    public void setSilicon(final Supplier<Item> supplier) {
        this.silicon = supplier;
    }

    public Item getSilicon() {
        return Objects.requireNonNull(silicon).get();
    }

    public void setProcessorBinding(final Supplier<Item> supplier) {
        this.processorBinding = supplier;
    }

    public Item getProcessorBinding() {
        return Objects.requireNonNull(processorBinding).get();
    }

    public void setWrench(final Supplier<Item> supplier) {
        this.wrench = supplier;
    }

    public Item getWrench() {
        return Objects.requireNonNull(wrench).get();
    }

    public void setProcessor(final ProcessorItem.Type type, final Supplier<Item> supplier) {
        this.processors.put(type, supplier);
    }

    public Item getProcessor(final ProcessorItem.Type type) {
        return Objects.requireNonNull(processors.get(type)).get();
    }

    public void setConstructionCore(final Supplier<Item> supplier) {
        this.constructionCore = supplier;
    }

    public Item getConstructionCore() {
        return Objects.requireNonNull(constructionCore).get();
    }

    public void setDestructionCore(final Supplier<Item> supplier) {
        this.destructionCore = supplier;
    }

    public Item getDestructionCore() {
        return Objects.requireNonNull(destructionCore).get();
    }

    public void addController(final Supplier<? extends Item> supplier) {
        allControllers.add(supplier);
    }

    public List<Supplier<? extends Item>> getAllControllers() {
        return Collections.unmodifiableList(allControllers);
    }

    public void addRegularController(final Supplier<ControllerBlockItem> supplier) {
        addController(supplier);
        regularControllers.add(supplier);
    }

    public List<Supplier<ControllerBlockItem>> getRegularControllers() {
        return Collections.unmodifiableList(regularControllers);
    }

    public void addCable(final Supplier<NamedBlockItem> supplier) {
        allCables.add(supplier);
    }

    public List<Supplier<NamedBlockItem>> getCables() {
        return Collections.unmodifiableList(allCables);
    }

    public void addExporter(final Supplier<NamedBlockItem> supplier) {
        allExporters.add(supplier);
    }

    public List<Supplier<NamedBlockItem>> getExporters() {
        return Collections.unmodifiableList(allExporters);
    }

    public void addImporter(final Supplier<NamedBlockItem> supplier) {
        allImporters.add(supplier);
    }

    public List<Supplier<NamedBlockItem>> getImporters() {
        return Collections.unmodifiableList(allImporters);
    }

    public void addExternalStorage(final Supplier<NamedBlockItem> supplier) {
        allExternalStorages.add(supplier);
    }

    public List<Supplier<NamedBlockItem>> getExternalStorages() {
        return Collections.unmodifiableList(allExternalStorages);
    }

    public void addDetector(final Supplier<NamedBlockItem> supplier) {
        allDetectors.add(supplier);
    }

    public List<Supplier<NamedBlockItem>> getDetectors() {
        return Collections.unmodifiableList(allDetectors);
    }

    public void addDestructor(final Supplier<NamedBlockItem> supplier) {
        allDestructors.add(supplier);
    }

    public List<Supplier<NamedBlockItem>> getDestructors() {
        return Collections.unmodifiableList(allDestructors);
    }

    public void addConstructor(final Supplier<NamedBlockItem> supplier) {
        allConstructors.add(supplier);
    }

    public List<Supplier<NamedBlockItem>> getConstructors() {
        return Collections.unmodifiableList(allConstructors);
    }

    public Item getStorageHousing() {
        return Objects.requireNonNull(storageHousing).get();
    }

    public void setStorageHousing(final Supplier<Item> supplier) {
        this.storageHousing = supplier;
    }

    public void setUpgrade(final Supplier<Item> supplier) {
        this.upgrade = supplier;
    }

    public Item getUpgrade() {
        return Objects.requireNonNull(upgrade).get();
    }

    public AbstractUpgradeItem getSpeedUpgrade() {
        return Objects.requireNonNull(speedUpgrade).get();
    }

    public void setSpeedUpgrade(final Supplier<? extends AbstractUpgradeItem> supplier) {
        this.speedUpgrade = supplier;
    }

    public AbstractUpgradeItem getStackUpgrade() {
        return Objects.requireNonNull(stackUpgrade).get();
    }

    public void setStackUpgrade(final Supplier<? extends AbstractUpgradeItem> supplier) {
        this.stackUpgrade = supplier;
    }

    public AbstractUpgradeItem getFortune1Upgrade() {
        return Objects.requireNonNull(fortune1Upgrade).get();
    }

    public void setFortune1Upgrade(final Supplier<? extends AbstractUpgradeItem> fortune1Upgrade) {
        this.fortune1Upgrade = fortune1Upgrade;
    }

    public AbstractUpgradeItem getFortune2Upgrade() {
        return Objects.requireNonNull(fortune2Upgrade).get();
    }

    public void setFortune2Upgrade(final Supplier<? extends AbstractUpgradeItem> fortune2Upgrade) {
        this.fortune2Upgrade = fortune2Upgrade;
    }

    public AbstractUpgradeItem getFortune3Upgrade() {
        return Objects.requireNonNull(fortune3Upgrade).get();
    }

    public void setFortune3Upgrade(final Supplier<? extends AbstractUpgradeItem> fortune3Upgrade) {
        this.fortune3Upgrade = fortune3Upgrade;
    }

    public AbstractUpgradeItem getSilkTouchUpgrade() {
        return Objects.requireNonNull(silkTouchUpgrade).get();
    }

    public void setSilkTouchUpgrade(final Supplier<? extends AbstractUpgradeItem> silkTouchUpgrade) {
        this.silkTouchUpgrade = silkTouchUpgrade;
    }
}
