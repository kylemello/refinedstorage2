package com.refinedmods.refinedstorage.common.controller;

import com.refinedmods.refinedstorage.common.content.BlockColorMap;
import com.refinedmods.refinedstorage.common.content.Blocks;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

public class CreativeControllerBlock extends AbstractControllerBlock<CreativeControllerBlockItem> {
    public CreativeControllerBlock(final MutableComponent name,
                                   final ControllerBlockEntityTicker ticker,
                                   final DyeColor color) {
        super(ControllerType.CREATIVE, name, ticker, color);
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(ENERGY_TYPE) == ControllerEnergyType.OFF) {
            return 0;
        }
        return 15;
    }

    @Override
    public BlockColorMap<
        AbstractControllerBlock<CreativeControllerBlockItem>,
        CreativeControllerBlockItem> getBlockColorMap() {
        return Blocks.INSTANCE.getCreativeController();
    }

    @Override
    public CreativeControllerBlockItem createBlockItem() {
        return new CreativeControllerBlockItem(this);
    }
}
