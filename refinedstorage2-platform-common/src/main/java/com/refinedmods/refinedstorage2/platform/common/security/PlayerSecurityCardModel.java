package com.refinedmods.refinedstorage2.platform.common.security;

import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

class PlayerSecurityCardModel extends SecurityCardModel {
    private static final String TAG_BOUND_PLAYER_ID = "bid";
    private static final String TAG_BOUND_PLAYER_NAME = "bname";

    PlayerSecurityCardModel(final ItemStack stack) {
        super(stack);
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

    @Override
    boolean isActive() {
        return super.isActive() && isActive(stack);
    }

    static boolean isActive(final ItemStack stack) {
        return stack.getTag() != null
            && stack.getTag().contains(TAG_BOUND_PLAYER_ID)
            && stack.getTag().contains(TAG_BOUND_PLAYER_NAME);
    }
}
