package com.refinedmods.refinedstorage.common.controller;

import com.refinedmods.refinedstorage.common.content.BlockConstants;
import com.refinedmods.refinedstorage.common.support.AbstractBaseBlock;
import com.refinedmods.refinedstorage.common.support.BlockItemProvider;
import com.refinedmods.refinedstorage.common.support.ColorableBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public abstract class AbstractControllerBlock<I extends BlockItem>
    extends AbstractBaseBlock
    implements ColorableBlock<AbstractControllerBlock<I>, I>, EntityBlock, BlockItemProvider<I> {
    public static final EnumProperty<ControllerEnergyType> ENERGY_TYPE = EnumProperty.create(
        "energy_type",
        ControllerEnergyType.class
    );

    private final MutableComponent name;

    private final ControllerType type;
    private final ControllerBlockEntityTicker ticker;
    private final DyeColor color;

    protected AbstractControllerBlock(final ControllerType type,
                                      final MutableComponent name,
                                      final ControllerBlockEntityTicker ticker,
                                      final DyeColor color) {
        super(BlockConstants.PROPERTIES);
        this.type = type;
        this.name = name;
        this.ticker = ticker;
        this.color = color;
    }

    @Override
    protected BlockState getDefaultState() {
        return super.getDefaultState().setValue(ENERGY_TYPE, ControllerEnergyType.OFF);
    }

    @Override
    public MutableComponent getName() {
        return name;
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ENERGY_TYPE);
    }

    @Override
    public boolean hasAnalogOutputSignal(final BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ControllerBlockEntity controller) {
            if (state.getValue(ENERGY_TYPE) == ControllerEnergyType.OFF || controller.getEnergyStorage().getStored() <= 0) {
                return 0;
            }
            long energy = controller.getEnergyStorage().getStored();
            long maxEnergy = controller.getEnergyStorage().getCapacity();
            return Math.max(1, (int) ((energy * 14L) / maxEnergy) + 1);
        }
        return 0;
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new ControllerBlockEntity(type, pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level,
                                                                  final BlockState state,
                                                                  final BlockEntityType<T> blockEntityType) {
        return ticker.get(level, blockEntityType);
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public boolean canAlwaysConnect() {
        return true;
    }
}
