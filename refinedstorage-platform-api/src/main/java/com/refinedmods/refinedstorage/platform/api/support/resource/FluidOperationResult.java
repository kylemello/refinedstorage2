package com.refinedmods.refinedstorage.platform.api.support.resource;

import net.minecraft.world.item.ItemStack;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.5")
public record FluidOperationResult(ItemStack container, PlatformResourceKey fluid, long amount) {
}
