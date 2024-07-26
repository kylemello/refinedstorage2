package com.refinedmods.refinedstorage.common.grid;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.storage.PlayerActor;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.content.ContentNames;
import com.refinedmods.refinedstorage.common.support.BlockEntityWithDrops;
import com.refinedmods.refinedstorage.common.support.CraftingMatrix;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CraftingGridBlockEntity extends AbstractGridBlockEntity implements BlockEntityWithDrops,
    NetworkNodeExtendedMenuProvider<GridData> {
    private final CraftingState craftingState = new CraftingState(this::setChanged, this::getLevel);

    public CraftingGridBlockEntity(final BlockPos pos, final BlockState state) {
        super(
            BlockEntities.INSTANCE.getCraftingGrid(),
            pos,
            state,
            Platform.INSTANCE.getConfig().getCraftingGrid().getEnergyUsage()
        );
    }

    CraftingMatrix getCraftingMatrix() {
        return craftingState.getCraftingMatrix();
    }

    ResultContainer getCraftingResult() {
        return craftingState.getCraftingResult();
    }

    NonNullList<ItemStack> getRemainingItems(final Player player, final CraftingInput input) {
        return craftingState.getRemainingItems(level, player, input);
    }

    @Override
    public GridData getMenuData() {
        return GridData.of(this);
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, GridData> getMenuCodec() {
        return GridData.STREAM_CODEC;
    }

    @Override
    public Component getDisplayName() {
        return ContentNames.CRAFTING_GRID;
    }

    @Override
    @Nullable
    public AbstractGridContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new CraftingGridContainerMenu(syncId, inventory, this);
    }

    @Override
    public void saveAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        craftingState.writeToTag(tag, provider);
    }

    @Override
    public void loadAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        craftingState.readFromTag(tag, provider);
    }

    @Override
    public void setLevel(final Level level) {
        super.setLevel(level);
        craftingState.updateResult(level);
    }

    @Override
    public NonNullList<ItemStack> getDrops() {
        final NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < craftingState.getCraftingMatrix().getContainerSize(); ++i) {
            drops.add(craftingState.getCraftingMatrix().getItem(i));
        }
        return drops;
    }

    Optional<Network> getNetwork() {
        if (!mainNetworkNode.isActive()) {
            return Optional.empty();
        }
        return Optional.ofNullable(mainNetworkNode.getNetwork());
    }

    Optional<RootStorage> getRootStorage() {
        return getNetwork().map(network -> network.getComponent(StorageNetworkComponent.class));
    }

    ItemStack insert(final ItemStack stack, final Player player) {
        return getRootStorage().map(rootStorage -> doInsert(stack, player, rootStorage)).orElse(stack);
    }

    private ItemStack doInsert(final ItemStack stack,
                               final Player player,
                               final RootStorage rootStorage) {
        final long inserted = rootStorage.insert(
            ItemResource.ofItemStack(stack),
            stack.getCount(),
            Action.EXECUTE,
            new PlayerActor(player)
        );
        final long remainder = stack.getCount() - inserted;
        if (remainder == 0) {
            return ItemStack.EMPTY;
        }
        return stack.copyWithCount((int) remainder);
    }

    long extract(final ItemResource resource, final Player player) {
        return getRootStorage().map(rootStorage -> rootStorage.extract(
            resource,
            1,
            Action.EXECUTE,
            new PlayerActor(player)
        )).orElse(0L);
    }
}
