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
import com.refinedmods.refinedstorage.common.support.RecipeMatrix;
import com.refinedmods.refinedstorage.common.support.RecipeMatrixContainer;
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
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CraftingGridBlockEntity extends AbstractGridBlockEntity implements BlockEntityWithDrops,
    NetworkNodeExtendedMenuProvider<GridData> {
    private static final String TAG_MATRIX = "matrix";

    private final RecipeMatrix<CraftingRecipe, CraftingInput> craftingRecipe = RecipeMatrix.crafting(
        this::setChanged,
        this::getLevel
    );

    public CraftingGridBlockEntity(final BlockPos pos, final BlockState state) {
        super(
            BlockEntities.INSTANCE.getCraftingGrid(),
            pos,
            state,
            Platform.INSTANCE.getConfig().getCraftingGrid().getEnergyUsage()
        );
    }

    RecipeMatrixContainer getCraftingMatrix() {
        return craftingRecipe.getMatrix();
    }

    ResultContainer getCraftingResult() {
        return craftingRecipe.getResult();
    }

    NonNullList<ItemStack> getRemainingItems(final Player player, final CraftingInput input) {
        return craftingRecipe.getRemainingItems(level, player, input);
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
        return getName(ContentNames.CRAFTING_GRID);
    }

    @Override
    @Nullable
    public AbstractGridContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new CraftingGridContainerMenu(syncId, inventory, this);
    }

    @Override
    public void saveAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(TAG_MATRIX, craftingRecipe.writeToTag(provider));
    }

    @Override
    public void loadAdditional(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains(TAG_MATRIX)) {
            craftingRecipe.readFromTag(tag.getCompound(TAG_MATRIX), provider);
        }
    }

    @Override
    public void setLevel(final Level level) {
        super.setLevel(level);
        craftingRecipe.updateResult(level);
    }

    @Override
    public final NonNullList<ItemStack> getDrops() {
        final NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < craftingRecipe.getMatrix().getContainerSize(); ++i) {
            drops.add(craftingRecipe.getMatrix().getItem(i));
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
