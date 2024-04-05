package com.refinedmods.refinedstorage2.platform.common.support.containermenu;

import com.refinedmods.refinedstorage2.platform.api.security.SecurityHelper;
import com.refinedmods.refinedstorage2.platform.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage2.platform.common.security.BuiltinPermission;

import java.util.Set;

import net.minecraft.server.level.ServerPlayer;

public interface NetworkNodeMenuProvider extends ExtendedMenuProvider {
    Set<InWorldNetworkNodeContainer> getContainers();

    default boolean canOpen(final ServerPlayer player) {
        return SecurityHelper.isAllowed(player, BuiltinPermission.OPEN, getContainers());
    }
}
