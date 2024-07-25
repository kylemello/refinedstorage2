package com.refinedmods.refinedstorage.common.api.support.network;

import com.refinedmods.refinedstorage.api.network.node.NetworkNode;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.1.2")
public abstract class AbstractNetworkNodeContainerBlockEntity<T extends NetworkNode> extends BlockEntity {
    protected final T mainNetworkNode;
    protected final NetworkNodeContainerProvider containers;

    protected AbstractNetworkNodeContainerBlockEntity(final BlockEntityType<?> type,
                                                      final BlockPos pos,
                                                      final BlockState state,
                                                      final T mainNetworkNode) {
        super(type, pos, state);
        this.containers = createContainerProvider();
        this.containers.addContainer(createMainContainer(mainNetworkNode));
        this.mainNetworkNode = mainNetworkNode;
    }

    protected NetworkNodeContainerProvider createContainerProvider() {
        return RefinedStorageApi.INSTANCE.createNetworkNodeContainerProvider();
    }

    protected InWorldNetworkNodeContainer createMainContainer(final T networkNode) {
        return RefinedStorageApi.INSTANCE.createNetworkNodeContainer(this, networkNode).build();
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        containers.initialize(level, this::containerInitialized);
    }

    protected void containerInitialized() {
        // no op
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        containers.remove(level);
    }

    public final NetworkNodeContainerProvider getContainerProvider() {
        return containers;
    }
}
