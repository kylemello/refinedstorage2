package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.support.containermenu.ExtendedMenuProvider;

import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

abstract class AbstractSecurityCardExtendedMenuProvider implements ExtendedMenuProvider {
    private final SlotReference slotReference;
    private final SecurityCardModel model;

    AbstractSecurityCardExtendedMenuProvider(final SlotReference slotReference, final SecurityCardModel model) {
        this.slotReference = slotReference;
        this.model = model;
    }

    @Override
    public void writeScreenOpeningData(final ServerPlayer player, final FriendlyByteBuf buf) {
        PlatformApi.INSTANCE.writeSlotReference(slotReference, buf);

        final List<PlatformPermission> permissions = PlatformApi.INSTANCE.getPermissionRegistry().getAll();
        buf.writeInt(permissions.size());
        for (final PlatformPermission permission : permissions) {
            final ResourceLocation id = PlatformApi.INSTANCE.getPermissionRegistry().getId(permission).orElseThrow();
            buf.writeResourceLocation(id);
            buf.writeBoolean(model.isAllowed(permission));
            buf.writeBoolean(model.isDirty(permission));
        }
    }
}
