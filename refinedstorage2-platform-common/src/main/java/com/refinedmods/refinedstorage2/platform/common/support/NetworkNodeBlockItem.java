package com.refinedmods.refinedstorage2.platform.common.support;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;

import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

// TODO: fix slot shuffle security manager w/ fallback
// TODO: sonar fixes
// TODO: server test before release
public class NetworkNodeBlockItem extends BaseBlockItem {
    public NetworkNodeBlockItem(final Block block) {
        super(block);
    }

    public NetworkNodeBlockItem(final Block block, final Component helpText) {
        super(block, helpText);
    }

    public NetworkNodeBlockItem(final Block block, final Properties properties, @Nullable final Component helpText) {
        super(block, properties, helpText);
    }

    @Override
    protected boolean placeBlock(final BlockPlaceContext ctx, final BlockState state) {
        if (ctx.getPlayer() instanceof ServerPlayer serverPlayer
            && !(PlatformApi.INSTANCE.canPlaceNetworkNode(serverPlayer, ctx.getLevel(), ctx.getClickedPos(), state))) {
            return false;
        }
        return super.placeBlock(ctx, state);
    }
}
