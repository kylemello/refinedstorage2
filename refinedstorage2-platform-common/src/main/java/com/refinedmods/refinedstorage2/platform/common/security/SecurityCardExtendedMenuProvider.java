package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.content.ContentNames;
import com.refinedmods.refinedstorage2.platform.common.support.containermenu.ExtendedMenuProvider;

import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

class SecurityCardExtendedMenuProvider implements ExtendedMenuProvider {
    private final SlotReference slotReference;

    SecurityCardExtendedMenuProvider(final SlotReference slotReference) {
        this.slotReference = slotReference;
    }

    @Override
    public void writeScreenOpeningData(final ServerPlayer player, final FriendlyByteBuf buf) {
        PlatformApi.INSTANCE.writeSlotReference(slotReference, buf);
        final List<PlatformPermission> permissions = PlatformApi.INSTANCE.getPermissionRegistry().getAll();
        buf.writeInt(permissions.size());
        for (final PlatformPermission permission : permissions) {
            final ResourceLocation id = PlatformApi.INSTANCE.getPermissionRegistry().getId(permission).orElseThrow();
            buf.writeResourceLocation(id);
        }
    }

    @Override
    public Component getDisplayName() {
        return ContentNames.SECURITY_CARD;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new SecurityCardContainerMenu(syncId, inventory);
    }
}
