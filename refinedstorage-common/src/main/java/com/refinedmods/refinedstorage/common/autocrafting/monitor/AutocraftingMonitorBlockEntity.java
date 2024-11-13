package com.refinedmods.refinedstorage.common.autocrafting.monitor;

import com.refinedmods.refinedstorage.api.autocrafting.status.AutocraftingTaskStatus;
import com.refinedmods.refinedstorage.api.network.impl.node.SimpleNetworkNode;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.content.BlockEntities;
import com.refinedmods.refinedstorage.common.content.ContentNames;
import com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.containermenu.NetworkNodeExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.List;
import java.util.UUID;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;

public class AutocraftingMonitorBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity<SimpleNetworkNode>
    implements NetworkNodeExtendedMenuProvider<AutocraftingMonitorData> {
    public AutocraftingMonitorBlockEntity(final BlockPos pos, final BlockState state) {
        super(BlockEntities.INSTANCE.getAutocraftingMonitor(), pos, state, new SimpleNetworkNode(
            Platform.INSTANCE.getConfig().getAutocraftingMonitor().getEnergyUsage()
        ));
    }

    @Override
    public Component getName() {
        return overrideName(ContentNames.AUTOCRAFTING_MONITOR);
    }

    @Override
    protected boolean doesBlockStateChangeWarrantNetworkNodeUpdate(final BlockState oldBlockState,
                                                                   final BlockState newBlockState) {
        return AbstractDirectionalBlock.didDirectionChange(oldBlockState, newBlockState);
    }

    @Override
    public AutocraftingMonitorData getMenuData() {
        return new AutocraftingMonitorData(List.of(
            new AutocraftingTaskStatus(
                new AutocraftingTaskStatus.Id(
                    UUID.randomUUID(),
                    ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                    64,
                    System.currentTimeMillis()
                ),
                List.of(
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        1,
                        0,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        1,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        1,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        0,
                        1
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.AUTOCRAFTER_IS_LOCKED,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        1,
                        0,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        1,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        1,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        0,
                        1
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.AUTOCRAFTER_IS_LOCKED,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        5448748,
                        1,
                        0,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        1,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        1,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        0,
                        1
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.AUTOCRAFTER_IS_LOCKED,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        10000,
                        0,
                        0,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        1,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        1,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        0,
                        1
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.AUTOCRAFTER_IS_LOCKED,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        5448748,
                        1,
                        0,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        1,
                        0,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        1,
                        0
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        1,
                        0,
                        0,
                        0,
                        1
                    ),
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.AUTOCRAFTER_IS_LOCKED,
                        ItemResource.ofItemStack(new ItemStack(Items.DIAMOND)),
                        10000,
                        0,
                        0,
                        0,
                        0
                    )
                )
            ),
            new AutocraftingTaskStatus(
                new AutocraftingTaskStatus.Id(
                    UUID.randomUUID(),
                    new FluidResource(Fluids.WATER, DataComponentPatch.EMPTY),
                    Platform.INSTANCE.getBucketAmount() * 2,
                    System.currentTimeMillis() - 3000
                ),
                List.of(
                    new AutocraftingTaskStatus.Element(
                        AutocraftingTaskStatus.ElementType.NORMAL,
                        ItemResource.ofItemStack(new ItemStack(Items.DIRT)),
                        6,
                        7,
                        8,
                        9,
                        10
                    )
                )
            )
        ));
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, AutocraftingMonitorData> getMenuCodec() {
        return AutocraftingMonitorData.STREAM_CODEC;
    }

    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new AutocraftingMonitorContainerMenu(syncId, this);
    }
}
