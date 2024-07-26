package com.refinedmods.refinedstorage.network.test.nodefactory;

import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.api.network.impl.node.grid.GridNetworkNode;

import java.util.Map;

public class GridNetworkNodeFactory extends AbstractNetworkNodeFactory {
    @Override
    protected AbstractNetworkNode innerCreate(final Map<String, Object> properties) {
        return new GridNetworkNode(getEnergyUsage(properties));
    }
}
