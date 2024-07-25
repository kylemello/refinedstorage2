package com.refinedmods.refinedstorage.common.api.support.network.item;

import com.refinedmods.refinedstorage.common.api.support.slotreference.SlotReference;

import java.util.List;
import java.util.Optional;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.3.1")
public interface NetworkItemHelper {
    void addTooltip(ItemStack stack, List<Component> lines);

    InteractionResult bind(UseOnContext ctx);

    Optional<TooltipComponent> getTooltipImage(ItemStack stack);

    NetworkItemContext createContext(ItemStack stack, ServerPlayer player, SlotReference slotReference);

    boolean isBound(ItemStack stack);
}
