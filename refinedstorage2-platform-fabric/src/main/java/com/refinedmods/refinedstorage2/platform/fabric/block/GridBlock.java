package com.refinedmods.refinedstorage2.platform.fabric.block;

import com.refinedmods.refinedstorage2.platform.fabric.init.BlockColorMap;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public abstract class GridBlock extends NetworkNodeContainerBlock {
    public GridBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected boolean hasBiDirection() {
        return true;
    }

    @Override
    protected boolean hasActive() {
        return true;
    }

    protected abstract BlockColorMap<?> getBlockColorMap();

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult result = getBlockColorMap().updateColor(state, player.getItemInHand(hand), world, pos, player);
        if (result != InteractionResult.PASS) {
            return result;
        }

        return super.use(state, world, pos, player, hand, hit);
    }
}
