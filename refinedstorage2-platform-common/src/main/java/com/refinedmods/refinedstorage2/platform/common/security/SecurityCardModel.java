package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

class SecurityCardModel {
    private static final String TAG_PERMISSIONS = "permissions";

    protected final ItemStack stack;

    SecurityCardModel(final ItemStack stack) {
        this.stack = stack;
    }

    boolean isAllowed(final PlatformPermission permission) {
        if (stack.getTag() != null && stack.getTag().contains(TAG_PERMISSIONS)) {
            final ResourceLocation permissionId = PlatformApi.INSTANCE.getPermissionRegistry()
                .getId(permission)
                .orElseThrow();
            final CompoundTag permissionsTag = stack.getTag().getCompound(TAG_PERMISSIONS);
            final boolean dirty = permissionsTag.contains(permissionId.toString());
            if (dirty) {
                return permissionsTag.getBoolean(permissionId.toString());
            }
        }
        return permission.isAllowedByDefault();
    }

    boolean isDirty(final PlatformPermission permission) {
        final ResourceLocation permissionId = PlatformApi.INSTANCE.getPermissionRegistry()
            .getId(permission)
            .orElseThrow();
        return stack.getTag() != null
            && stack.getTag().contains(TAG_PERMISSIONS)
            && stack.getTag().getCompound(TAG_PERMISSIONS).contains(permissionId.toString());
    }

    void setPermission(final ResourceLocation permissionId, final boolean allowed) {
        final CompoundTag permissionsTag = stack.getOrCreateTagElement(TAG_PERMISSIONS);
        permissionsTag.putBoolean(permissionId.toString(), allowed);
    }

    void resetPermission(final ResourceLocation permissionId) {
        final CompoundTag permissionsTag = stack.getOrCreateTagElement(TAG_PERMISSIONS);
        permissionsTag.remove(permissionId.toString());
    }

    boolean isActive() {
        return stack.getTag() != null && stack.getTag().contains(TAG_PERMISSIONS);
    }

    boolean isCleared() {
        return stack.getTag() == null;
    }

    public void clear() {
        stack.setTag(null);
    }
}
