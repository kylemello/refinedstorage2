package com.refinedmods.refinedstorage.common.support.energy;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.common.api.PlatformApi;
import com.refinedmods.refinedstorage.common.api.support.energy.TransferableBlockEntityEnergy;
import com.refinedmods.refinedstorage.common.content.LootFunctions;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class EnergyLootItemFunction implements LootItemFunction {
    @Override
    public LootItemFunctionType<? extends EnergyLootItemFunction> getType() {
        return LootFunctions.INSTANCE.getEnergy();
    }

    @Override
    public ItemStack apply(final ItemStack stack, final LootContext lootContext) {
        final BlockEntity blockEntity = lootContext.getParam(LootContextParams.BLOCK_ENTITY);
        if (blockEntity instanceof TransferableBlockEntityEnergy transferableBlockEntityEnergy) {
            final long stored = transferableBlockEntityEnergy.getEnergyStorage().getStored();
            PlatformApi.INSTANCE.getEnergyStorage(stack).ifPresent(
                energyStorage -> energyStorage.receive(stored, Action.EXECUTE)
            );
        }
        return stack;
    }
}
