package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.content.Menus;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;

public class FallbackSecurityCardContainerMenu extends AbstractSecurityCardContainerMenu {
    public FallbackSecurityCardContainerMenu(final int syncId,
                                             final Inventory playerInventory,
                                             final FriendlyByteBuf buf) {
        super(Menus.INSTANCE.getFallbackSecurityCard(), syncId, playerInventory, buf);
    }

    FallbackSecurityCardContainerMenu(final int syncId,
                                      final Inventory playerInventory,
                                      final SlotReference disabledSlot) {
        super(Menus.INSTANCE.getFallbackSecurityCard(), syncId, playerInventory, disabledSlot);
    }
}
