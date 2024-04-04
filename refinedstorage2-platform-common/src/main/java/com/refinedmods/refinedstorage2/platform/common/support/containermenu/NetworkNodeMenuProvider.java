package com.refinedmods.refinedstorage2.platform.common.support.containermenu;

import com.refinedmods.refinedstorage2.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage2.platform.api.security.SecurityHelper;
import com.refinedmods.refinedstorage2.platform.common.security.BuiltinPermission;

import net.minecraft.server.level.ServerPlayer;

public interface NetworkNodeMenuProvider extends ExtendedMenuProvider {
    NetworkNode getNode();

    default boolean canOpen(final ServerPlayer player) {
        return SecurityHelper.isAllowed(player, BuiltinPermission.OPEN, getNode());
    }
}
