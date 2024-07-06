package com.refinedmods.refinedstorage.platform.api.support.slotreference;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.apiguardian.api.API;

@FunctionalInterface
@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.3")
public interface SlotReferenceHandlerItem {
    void use(ServerPlayer player, ItemStack stack, SlotReference slotReference);
}
