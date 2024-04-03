package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.api.network.impl.security.SecurityNetworkComponentImpl;
import com.refinedmods.refinedstorage2.api.network.security.Permission;
import com.refinedmods.refinedstorage2.api.network.security.SecurityPolicy;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformSecurityNetworkComponent;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PlatformSecurityNetworkComponentImpl extends SecurityNetworkComponentImpl
    implements PlatformSecurityNetworkComponent {
    private final SecurityPolicy defaultPolicy;

    public PlatformSecurityNetworkComponentImpl(final SecurityPolicy defaultPolicy) {
        super(defaultPolicy);
        this.defaultPolicy = defaultPolicy;
    }

    @Override
    public boolean isAllowed(final Permission permission, final ServerPlayer player) {
        if (providers.isEmpty()) {
            return defaultPolicy.isAllowed(permission);
        }
        final MinecraftServer server = player.getServer();
        if (server == null) {
            return false;
        }
        final GameProfile gameProfile = player.getGameProfile();
        if (server.getPlayerList().isOp(gameProfile)) {
            return true;
        }
        final PlayerSecurityActor actor = new PlayerSecurityActor(gameProfile.getId());
        return super.isAllowed(permission, actor);
    }
}
