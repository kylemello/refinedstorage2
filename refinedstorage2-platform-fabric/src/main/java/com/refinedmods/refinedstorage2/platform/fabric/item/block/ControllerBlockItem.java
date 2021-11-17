package com.refinedmods.refinedstorage2.platform.fabric.item.block;

import com.refinedmods.refinedstorage2.api.core.Action;
import com.refinedmods.refinedstorage2.api.core.QuantityFormatter;
import com.refinedmods.refinedstorage2.platform.fabric.Rs2Mod;
import com.refinedmods.refinedstorage2.platform.fabric.block.entity.ControllerBlockEntity;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ControllerBlockItem extends ColoredBlockItem {
    private static final int MAX_DAMAGE = 100;

    public ControllerBlockItem(Block block, Properties settings, DyeColor color, Component displayName) {
        super(block, settings.durability(MAX_DAMAGE), color, displayName);
    }

    public static float getPercentFull(ItemStack stack) {
        long stored = getStored(stack);
        long capacity = getCapacity(stack);
        if (capacity == 0) {
            return 1;
        }
        return (float) stored / (float) capacity;
    }

    private static long getStored(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 0;
        }
        return tag.getLong("stored");
    }

    private static long getCapacity(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return 0;
        }
        return tag.getLong("cap");
    }

    public static void setEnergy(ItemStack stack, long stored, long capacity) {
        if (!stack.hasTag()) {
            stack.setTag(new CompoundTag());
        }
        stack.getTag().putLong("stored", stored);
        stack.getTag().putLong("cap", capacity);
    }

    public static int calculateDamage(long stored, long capacity) {
        return MAX_DAMAGE - (int) (((double) stored / (double) capacity) * MAX_DAMAGE);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        super.appendHoverText(stack, world, tooltip, context);

        long cap = getCapacity(stack);
        if (cap > 0) {
            tooltip.add(Rs2Mod.createTranslation(
                    "misc",
                    "stored_with_capacity",
                    QuantityFormatter.format(getStored(stack)),
                    QuantityFormatter.format(cap)
            ).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, @Nullable Player player, ItemStack stack, BlockState state) {
        boolean result = super.updateCustomBlockEntityTag(pos, world, player, stack, state);
        if (!world.isClientSide()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof ControllerBlockEntity controllerBlockEntity) {
                controllerBlockEntity.getContainer().getNode().receive(getStored(stack), Action.EXECUTE);
            }
        }
        return result;
    }
}
