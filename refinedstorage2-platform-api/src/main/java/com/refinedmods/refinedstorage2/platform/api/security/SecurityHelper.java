package com.refinedmods.refinedstorage2.platform.api.security;

import com.refinedmods.refinedstorage2.api.network.Network;
import com.refinedmods.refinedstorage2.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage2.api.network.security.Permission;

import net.minecraft.server.level.ServerPlayer;

public final class SecurityHelper {
    private SecurityHelper() {
    }

    public static boolean isAllowed(final ServerPlayer player, final Permission permission, final NetworkNode node) {
        final Network network = node.getNetwork();
        if (network == null) {
            return false;
        }
        return isAllowed(player, permission, network);
    }

    public static boolean isAllowed(final ServerPlayer player, final Permission permission, final Network network) {
        return network.getComponent(PlatformSecurityNetworkComponent.class).isAllowed(permission, player);
    }
}
