package com.refinedmods.refinedstorage.common.support.network.item;

import com.refinedmods.refinedstorage.common.api.support.energy.AbstractNetworkEnergyItem;

import javax.annotation.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

public class NetworkItemPropertyFunction implements ClampedItemPropertyFunction {
    public static final ResourceLocation NAME = createIdentifier("network_bound_active");

    @Override
    public float unclampedCall(final ItemStack itemStack,
                               @Nullable final ClientLevel clientLevel,
                               @Nullable final LivingEntity livingEntity,
                               final int i) {
        if (itemStack.getItem() instanceof AbstractNetworkEnergyItem item) {
            return item.isBound(itemStack) ? 1 : 0;
        }
        return 0;
    }
}
