package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.common.Platform;
import com.refinedmods.refinedstorage2.platform.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage2.platform.common.support.stretching.ScreenSizeListener;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractSecurityCardContainerMenu extends AbstractBaseContainerMenu
    implements ScreenSizeListener {
    protected final Inventory playerInventory;
    private final List<Permission> permissions = new ArrayList<>();

    protected AbstractSecurityCardContainerMenu(final MenuType<?> menuType,
                                                final int syncId,
                                                final Inventory playerInventory,
                                                final FriendlyByteBuf buf) {
        super(menuType, syncId);
        this.playerInventory = playerInventory;
        this.disabledSlot = PlatformApi.INSTANCE.getSlotReference(buf).orElse(null);
        final int amountOfPermissions = buf.readInt();
        for (int i = 0; i < amountOfPermissions; ++i) {
            final ResourceLocation id = buf.readResourceLocation();
            final boolean allowed = buf.readBoolean();
            final boolean dirty = buf.readBoolean();
            PlatformApi.INSTANCE.getPermissionRegistry().get(id).ifPresent(permission -> permissions.add(new Permission(
                permission,
                allowed,
                dirty
            )));
        }
    }

    protected AbstractSecurityCardContainerMenu(final MenuType<?> menuType,
                                                final int syncId,
                                                final Inventory playerInventory,
                                                final SlotReference disabledSlot) {
        super(menuType, syncId);
        this.playerInventory = playerInventory;
        this.disabledSlot = disabledSlot;
    }

    List<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public void onScreenReady(final int playerInventoryY) {
        resetSlots();
        addPlayerInventory(playerInventory, 8, playerInventoryY);
    }

    public void setPermission(final ResourceLocation permissionId, final boolean allowed) {
        if (disabledSlot == null) {
            return;
        }
        disabledSlot.resolve(playerInventory.player).ifPresent(stack -> setPermission(permissionId, allowed, stack));
    }

    private void setPermission(final ResourceLocation permissionId, final boolean allowed, final ItemStack stack) {
        if (stack.getItem() instanceof AbstractSecurityCardItem<?> securityCardItem) {
            final SecurityCardModel model = securityCardItem.createModel(stack);
            model.setPermission(permissionId, allowed);
        }
    }

    public void resetPermission(final ResourceLocation permissionId) {
        if (disabledSlot == null) {
            return;
        }
        disabledSlot.resolve(playerInventory.player).ifPresent(stack -> resetPermission(permissionId, stack));
    }

    private void resetPermission(final ResourceLocation permissionId, final ItemStack stack) {
        if (stack.getItem() instanceof AbstractSecurityCardItem<?> securityCardItem) {
            final SecurityCardModel model = securityCardItem.createModel(stack);
            model.resetPermission(permissionId);
        }
    }

    Permission resetPermission(final PlatformPermission permission) {
        final boolean allowed = permission.isAllowedByDefault();
        Platform.INSTANCE.getClientToServerCommunications().sendSecurityCardResetPermission(permission);
        return updatePermissionLocally(permission, allowed, false);
    }
    
    Permission changePermission(final PlatformPermission permission, final boolean selected) {
        Platform.INSTANCE.getClientToServerCommunications().sendSecurityCardPermission(permission, selected);
        return updatePermissionLocally(permission, selected, true);
    }

    private Permission updatePermissionLocally(final PlatformPermission permission,
                                               final boolean allowed,
                                               final boolean dirty) {
        final Permission localPermission = permissions.stream().filter(p -> p.platformPermission == permission)
            .findFirst()
            .orElseThrow();
        final int index = permissions.indexOf(localPermission);
        final Permission updatedLocalPermission = new Permission(
            localPermission.platformPermission,
            allowed,
            dirty
        );
        permissions.set(index, updatedLocalPermission);
        return updatedLocalPermission;
    }

    record Permission(PlatformPermission platformPermission, boolean allowed, boolean dirty) {
    }
}
