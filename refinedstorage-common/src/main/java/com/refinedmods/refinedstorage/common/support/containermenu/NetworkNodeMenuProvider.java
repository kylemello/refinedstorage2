package com.refinedmods.refinedstorage.common.support.containermenu;

import com.refinedmods.refinedstorage.common.api.security.SecurityHelper;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import com.refinedmods.refinedstorage.common.security.BuiltinPermission;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;

public interface NetworkNodeMenuProvider extends MenuProvider {
    NetworkNodeContainerProvider getContainerProvider();

    default boolean canOpen(final ServerPlayer player) {
        return SecurityHelper.isAllowed(
            player,
            BuiltinPermission.OPEN,
            getContainerProvider().getContainers()
        );
    }
}
