package com.refinedmods.refinedstorage.platform.common.support.slotreference;

import com.refinedmods.refinedstorage.platform.api.support.slotreference.SlotReference;
import com.refinedmods.refinedstorage.platform.api.support.slotreference.SlotReferenceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class InventorySlotReferenceProvider implements SlotReferenceProvider {
    @Override
    public List<SlotReference> find(final Player player, final Set<Item> validItems) {
        final List<SlotReference> result = new ArrayList<>();
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            final ItemStack slot = player.getInventory().getItem(i);
            if (!validItems.contains(slot.getItem())) {
                continue;
            }
            result.add(new InventorySlotReference(i));
        }
        return result;
    }
}
