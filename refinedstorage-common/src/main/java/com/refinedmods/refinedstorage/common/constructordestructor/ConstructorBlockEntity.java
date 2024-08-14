package com.refinedmods.refinedstorage.common.constructordestructor;

import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNodeActor;
import com.refinedmods.refinedstorage.api.network.node.SchedulingMode;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.constructordestructor.ConstructorStrategy;
import com.refinedmods.refinedstorage.common.api.constructordestructor.ConstructorStrategyFactory;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.content.ContentNames;
import com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.BlockEntityWithDrops;
import com.refinedmods.refinedstorage.common.support.FilterWithFuzzyMode;
import com.refinedmods.refinedstorage.common.support.SchedulingModeContainer;
import com.refinedmods.refinedstorage.common.support.SchedulingModeType;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.network.BaseNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerData;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;
import com.refinedmods.refinedstorage.common.util.ContainerUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class ConstructorBlockEntity extends BaseNetworkNodeContainerBlockEntity<SimpleNetworkNode>
    implements BlockEntityWithDrops, NetworkNodeExtendedMenuProvider<ResourceContainerData> {
    private static final String TAG_DROP_ITEMS = "di";
    private static final String TAG_UPGRADES = "upgr";

    private final Actor actor;
    private final List<ConstructorTask> tasks = new ArrayList<>();
    private final UpgradeContainer upgradeContainer;
    private final FilterWithFuzzyMode filter;
    private final SchedulingModeContainer schedulingModeContainer;

    @Nullable
    private ConstructorStrategy strategy;
    private boolean dropItems;

    public ConstructorBlockEntity(final BlockPos pos, final BlockState state) {
        super(
            BlockEntities.INSTANCE.getConstructor(),
            pos,
            state,
            new SimpleNetworkNode(Platform.INSTANCE.getConfig().getConstructor().getEnergyUsage())
        );
        this.actor = new NetworkNodeActor(mainNetworkNode);
        this.upgradeContainer = new UpgradeContainer(UpgradeDestinations.CONSTRUCTOR, (rate, upgradeEnergyUsage) -> {
            setWorkTickRate(rate);
            final long baseEnergyUsage = Platform.INSTANCE.getConfig().getConstructor().getEnergyUsage();
            mainNetworkNode.setEnergyUsage(baseEnergyUsage + upgradeEnergyUsage);
            setChanged();
            if (level instanceof ServerLevel serverLevel) {
                initialize(serverLevel);
            }
        });
        this.schedulingModeContainer = new SchedulingModeContainer(schedulingMode -> setChanged());
        this.filter = FilterWithFuzzyMode.createAndListenForFilters(
            ResourceContainerImpl.createForFilter(),
            this::setChanged,
            this::setFilters
        );
    }

    @Override
    protected boolean hasWorkTickRate() {
        return true;
    }

    @Override
    public List<Item> getUpgradeItems() {
        return upgradeContainer.getUpgradeItems();
    }

    @Override
    public boolean addUpgradeItem(final Item upgradeItem) {
        return upgradeContainer.addUpgradeItem(upgradeItem);
    }

    private void setFilters(final List<ResourceKey> filters) {
        this.tasks.clear();
        this.tasks.addAll(filters.stream().map(ConstructorTask::new).toList());
    }

    @Override
    protected void initialize(final ServerLevel level, final Direction direction) {
        super.initialize(level, direction);
        this.strategy = createStrategy(level, direction);
    }

    private ConstructorStrategy createStrategy(final ServerLevel serverLevel, final Direction direction) {
        final Direction incomingDirection = direction.getOpposite();
        final BlockPos sourcePosition = worldPosition.relative(direction);
        final Collection<ConstructorStrategyFactory> factories = RefinedStorageApi.INSTANCE
            .getConstructorStrategyFactories();
        final List<ConstructorStrategy> strategies = factories
            .stream()
            .flatMap(factory -> factory.create(
                serverLevel,
                sourcePosition,
                incomingDirection,
                upgradeContainer,
                dropItems
            ).stream())
            .toList();
        return new CompositeConstructorStrategy(strategies);
    }

    // TODO: Move to own obj :)
    @Override
    public void postDoWork() {
        if (mainNetworkNode.getNetwork() == null || !mainNetworkNode.isActive()) {
            return;
        }
        schedulingModeContainer.execute(tasks);
    }

    @Override
    public void saveAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(TAG_UPGRADES, ContainerUtil.write(upgradeContainer, provider));
    }

    @Override
    public void loadAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        if (tag.contains(TAG_UPGRADES)) {
            ContainerUtil.read(tag.getCompound(TAG_UPGRADES), upgradeContainer, provider);
        }
        super.loadAdditional(tag, provider);
    }

    @Override
    public void writeConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.writeConfiguration(tag, provider);
        tag.putBoolean(TAG_DROP_ITEMS, dropItems);
        schedulingModeContainer.writeToTag(tag);
        filter.save(tag, provider);
    }

    @Override
    public void readConfiguration(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.readConfiguration(tag, provider);
        if (tag.contains(TAG_DROP_ITEMS)) {
            dropItems = tag.getBoolean(TAG_DROP_ITEMS);
        }
        schedulingModeContainer.loadFromTag(tag);
        filter.load(tag, provider);
    }

    void setSchedulingModeType(final SchedulingModeType type) {
        schedulingModeContainer.setType(type);
    }

    SchedulingModeType getSchedulingModeType() {
        return schedulingModeContainer.getType();
    }

    boolean isFuzzyMode() {
        return filter.isFuzzyMode();
    }

    void setFuzzyMode(final boolean fuzzyMode) {
        filter.setFuzzyMode(fuzzyMode);
        if (level instanceof ServerLevel serverLevel) {
            initialize(serverLevel);
        }
    }

    @Override
    public final NonNullList<ItemStack> getDrops() {
        return upgradeContainer.getDrops();
    }

    boolean isDropItems() {
        return dropItems;
    }

    void setDropItems(final boolean dropItems) {
        this.dropItems = dropItems;
        setChanged();
        if (level instanceof ServerLevel serverLevel) {
            initialize(serverLevel);
        }
    }

    @Override
    public Component getDisplayName() {
        return getName(ContentNames.CONSTRUCTOR);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new ConstructorContainerMenu(syncId, player, this, filter.getFilterContainer(), upgradeContainer);
    }

    @Override
    public ResourceContainerData getMenuData() {
        return ResourceContainerData.of(filter.getFilterContainer());
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, ResourceContainerData> getMenuCodec() {
        return ResourceContainerData.STREAM_CODEC;
    }

    @Override
    protected boolean doesBlockStateChangeWarrantNetworkNodeUpdate(final BlockState oldBlockState,
                                                                   final BlockState newBlockState) {
        return AbstractDirectionalBlock.didDirectionChange(oldBlockState, newBlockState);
    }

    private class ConstructorTask implements SchedulingMode.ScheduledTask {
        private final ResourceKey filter;

        private ConstructorTask(final ResourceKey filter) {
            this.filter = filter;
        }

        @Override
        public boolean run() {
            final Network network = mainNetworkNode.getNetwork();
            if (strategy == null || !(level instanceof ServerLevel serverLevel) || network == null) {
                return false;
            }
            final Player player = getFakePlayer(serverLevel);
            strategy.apply(filter, actor, player, network);
            return true;
        }
    }
}
