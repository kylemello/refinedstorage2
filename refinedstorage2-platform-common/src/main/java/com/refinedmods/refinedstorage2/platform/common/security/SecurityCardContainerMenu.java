package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.common.content.Menus;
import com.refinedmods.refinedstorage2.platform.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage2.platform.common.support.stretching.ScreenSizeListener;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SecurityCardContainerMenu extends AbstractBaseContainerMenu implements ScreenSizeListener {
    private final Inventory playerInventory;
    private final List<Permission> permissions;

    public SecurityCardContainerMenu(final int syncId,
                                     final Inventory playerInventory,
                                     final FriendlyByteBuf buf) {
        super(Menus.INSTANCE.getSecurityCard(), syncId);
        this.playerInventory = playerInventory;
        this.permissions = new ArrayList<>();
        this.disabledSlot = PlatformApi.INSTANCE.getSlotReference(buf).orElse(null);
        final int amountOfPermissions = buf.readInt();
        for (int i = 0; i < amountOfPermissions; ++i) {
            final ResourceLocation id = buf.readResourceLocation();
            PlatformApi.INSTANCE.getPermissionRegistry().get(id).ifPresent(permission -> permissions.add(new Permission(
                id,
                permission.getName(),
                permission.getDescription(),
                permission.getOwnerName()
            )));
        }
    }

    SecurityCardContainerMenu(final int syncId, final Inventory playerInventory) {
        super(Menus.INSTANCE.getSecurityCard(), syncId);
        this.playerInventory = playerInventory;
        this.permissions = new ArrayList<>();
    }

    List<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public void initSlots(final int playerInventoryY) {
        resetSlots();
        addPlayerInventory(playerInventory, 8, playerInventoryY);
    }

    record Permission(ResourceLocation id, Component name, Component description, Component ownerName) {
    }
}
