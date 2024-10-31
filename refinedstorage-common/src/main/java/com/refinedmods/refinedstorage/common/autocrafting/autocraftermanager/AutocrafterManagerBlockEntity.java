package com.refinedmods.refinedstorage.common.autocrafting.autocraftermanager;

import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.content.ContentNames;
import com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class AutocrafterManagerBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity<SimpleNetworkNode>
    implements NetworkNodeExtendedMenuProvider<AutocrafterManagerData> {
    private final List<AutocrafterManagerData.Group> groups = List.of(
        new AutocrafterManagerData.Group(Component.literal("testing"), 10),
        new AutocrafterManagerData.Group(Component.literal(
            "HELLO WORLD!!! HELLO WORLD!!!HELLO WORLD!!!HELLO WORLD!!!HELLO WORLD!!!HELLO WORLD!!!HELLO WORLD!!!HELLO WORLD!!!"),
            8),
        new AutocrafterManagerData.Group(Component.literal("testing"), 10)
    );
    private final List<Container> inventories = List.of(
        new SimpleContainer(10),
        new SimpleContainer(8),
        new SimpleContainer(10)
    );

    public AutocrafterManagerBlockEntity(final BlockPos pos, final BlockState state) {
        super(BlockEntities.INSTANCE.getAutocrafterManager(), pos, state, new SimpleNetworkNode(
            Platform.INSTANCE.getConfig().getAutocrafterManager().getEnergyUsage()
        ));
    }

    @Override
    public Component getName() {
        return ContentNames.AUTOCRAFTER_MANAGER;
    }

    @Override
    protected boolean doesBlockStateChangeWarrantNetworkNodeUpdate(final BlockState oldBlockState,
                                                                   final BlockState newBlockState) {
        return AbstractDirectionalBlock.didDirectionChange(oldBlockState, newBlockState);
    }

    @Override
    public AutocrafterManagerData getMenuData() {
        return new AutocrafterManagerData(groups);
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, AutocrafterManagerData> getMenuCodec() {
        return AutocrafterManagerData.STREAM_CODEC;
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new AutocrafterManagerContainerMenu(syncId, inventory, this, inventories);
    }
}
