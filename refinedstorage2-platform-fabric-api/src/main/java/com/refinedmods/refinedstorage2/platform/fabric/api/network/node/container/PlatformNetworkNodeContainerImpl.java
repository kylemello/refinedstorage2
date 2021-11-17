package com.refinedmods.refinedstorage2.platform.fabric.api.network.node.container;

import com.refinedmods.refinedstorage2.api.network.component.NetworkComponentRegistry;
import com.refinedmods.refinedstorage2.api.network.node.NetworkNodeImpl;
import com.refinedmods.refinedstorage2.api.network.node.container.ConnectionProvider;
import com.refinedmods.refinedstorage2.api.network.node.container.NetworkNodeContainerImpl;
import com.refinedmods.refinedstorage2.platform.fabric.api.Rs2PlatformApiFacade;
import com.refinedmods.refinedstorage2.platform.fabric.api.network.node.RedstoneMode;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class PlatformNetworkNodeContainerImpl<T extends NetworkNodeImpl> extends NetworkNodeContainerImpl<T> implements PlatformNetworkNodeContainer<T> {
    private final BlockPos pos;
    private Level world;
    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    private ConnectionProvider connectionProvider;

    public PlatformNetworkNodeContainerImpl(T node, BlockPos pos) {
        super(node);
        this.pos = pos;
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;
    }

    @Override
    public Level getContainerWorld() {
        return world;
    }

    @Override
    public void setContainerWorld(Level world) {
        this.world = world;
        this.connectionProvider = Rs2PlatformApiFacade.INSTANCE.createConnectionProvider(world);
    }

    @Override
    public BlockPos getContainerPosition() {
        return pos;
    }

    @Override
    public void initialize() {
        initialize(connectionProvider, NetworkComponentRegistry.INSTANCE);
    }

    @Override
    protected boolean isActive() {
        return super.isActive() && redstoneMode.isActive(world.hasNeighborSignal(pos));
    }
}
