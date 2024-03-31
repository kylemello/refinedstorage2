package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;

import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

class SecurityCardModel {
    private static final String TAG_BOUND_PLAYER_ID = "bid";
    private static final String TAG_BOUND_PLAYER_NAME = "bname";
    private static final String TAG_PERMISSIONS = "permissions";

    private final ItemStack stack;

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

    @Nullable
    UUID getBoundPlayerId() {
        return (stack.getTag() == null || !stack.getTag().contains(TAG_BOUND_PLAYER_ID))
            ? null
            : stack.getTag().getUUID(TAG_BOUND_PLAYER_ID);
    }

    @Nullable
    String getBoundPlayerName() {
        return (stack.getTag() == null || !stack.getTag().contains(TAG_BOUND_PLAYER_NAME))
            ? null
            : stack.getTag().getString(TAG_BOUND_PLAYER_NAME);
    }

    void setBoundPlayer(@Nullable final ServerPlayer player) {
        final CompoundTag tag = stack.getOrCreateTag();
        if (player == null) {
            tag.remove(TAG_BOUND_PLAYER_ID);
            tag.remove(TAG_BOUND_PLAYER_NAME);
            return;
        }
        tag.putUUID(TAG_BOUND_PLAYER_ID, player.getGameProfile().getId());
        tag.putString(TAG_BOUND_PLAYER_NAME, player.getGameProfile().getName());
    }

    static boolean isActive(final ItemStack stack) {
        return stack.getTag() != null
            && stack.getTag().contains(TAG_BOUND_PLAYER_ID)
            && stack.getTag().contains(TAG_BOUND_PLAYER_NAME)
            && stack.getTag().contains(TAG_PERMISSIONS);
    }
}
