package com.refinedmods.refinedstorage.common.support.network;

import com.refinedmods.refinedstorage.common.api.security.SecurityHelper;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.api.support.network.NetworkNodeContainerProvider;
import com.refinedmods.refinedstorage.common.security.BuiltinPermission;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.server.level.ServerPlayer;

public class NetworkNodeContainerProviderImpl implements NetworkNodeContainerProvider {
    private final Set<InWorldNetworkNodeContainer> containers = new HashSet<>(1);
    private final Set<InWorldNetworkNodeContainer> containersView = Collections.unmodifiableSet(containers);

    @Override
    public Set<InWorldNetworkNodeContainer> getContainers() {
        return containersView;
    }

    @Override
    public void addContainer(final InWorldNetworkNodeContainer container) {
        containers.add(container);
    }

    @Override
    public boolean canBuild(final ServerPlayer player) {
        return SecurityHelper.isAllowed(player, BuiltinPermission.BUILD, getContainers());
    }
}
