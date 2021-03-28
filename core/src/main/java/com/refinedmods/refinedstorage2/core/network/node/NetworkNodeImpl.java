package com.refinedmods.refinedstorage2.core.network.node;

import com.refinedmods.refinedstorage2.core.World;
import com.refinedmods.refinedstorage2.core.network.Network;
import net.minecraft.util.math.BlockPos;

public class NetworkNodeImpl implements NetworkNode {
    protected final World world;
    private final BlockPos pos;
    private final NetworkNodeReference ref;
    private RedstoneMode redstoneMode = RedstoneMode.IGNORE;
    protected Network network;

    public NetworkNodeImpl(World world, BlockPos pos, NetworkNodeReference ref) {
        this.world = world;
        this.pos = pos;
        this.ref = ref;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    @Override
    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public Network getNetwork() {
        return network;
    }

    @Override
    public NetworkNodeReference createReference() {
        return ref;
    }

    public RedstoneMode getRedstoneMode() {
        return redstoneMode;
    }

    public void setRedstoneMode(RedstoneMode redstoneMode) {
        this.redstoneMode = redstoneMode;
    }

    @Override
    public boolean isActive() {
        return redstoneMode.isActive(world.isPowered(pos));
    }

    @Override
    public void onActiveChanged(boolean active) {

    }
}
