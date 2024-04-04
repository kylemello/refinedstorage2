package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.api.network.security.SecurityPolicy;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.content.ContentNames;

import java.util.Set;
import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

class FallbackSecurityCardExtendedMenuProvider extends AbstractSecurityCardExtendedMenuProvider {
    private final SlotReference slotReference;

    FallbackSecurityCardExtendedMenuProvider(final SlotReference slotReference,
                                             final SecurityPolicy securityPolicy,
                                             final Set<PlatformPermission> dirtyPermissions,
                                             final SlotReference slotReference1) {
        super(slotReference, securityPolicy, dirtyPermissions);
        this.slotReference = slotReference1;
    }

    @Override
    public Component getDisplayName() {
        return ContentNames.FALLBACK_SECURITY_CARD;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new FallbackSecurityCardContainerMenu(syncId, inventory, slotReference);
    }
}
