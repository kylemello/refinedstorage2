package com.refinedmods.refinedstorage2.platform.fabric.security;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.support.network.PlatformNetworkNodeContainer;

import javax.annotation.Nullable;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createTranslation;

public class NetworkNodeBreakSecurityEventListener implements PlayerBlockBreakEvents.Before {
    @Override
    public boolean beforeBlockBreak(final Level world,
                                    final Player player,
                                    final BlockPos pos,
                                    final BlockState state,
                                    @Nullable final BlockEntity blockEntity) {
        if (blockEntity instanceof PlatformNetworkNodeContainer platformNetworkNodeContainer
            && player instanceof ServerPlayer serverPlayer
            && !platformNetworkNodeContainer.canBreak(serverPlayer)) {
            PlatformApi.INSTANCE.sendNoPermissionMessage(
                serverPlayer,
                createTranslation("misc", "no_permission.build.break", state.getBlock().getName())
            );
            return false;
        }
        return true;
    }
}
