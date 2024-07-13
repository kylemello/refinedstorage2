package com.refinedmods.refinedstorage.network.test.nodefactory;

import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.api.network.node.NetworkNode;

import java.util.Map;

public abstract class AbstractNetworkNodeFactory implements NetworkNodeFactory {
    public static final String PROPERTY_ACTIVE = "active";
    public static final String PROPERTY_ENERGY_USAGE = "energy_usage";

    @Override
    public final NetworkNode create(final Map<String, Object> properties) {
        final AbstractNetworkNode value = innerCreate(properties);
        final boolean active = (boolean) properties.getOrDefault(PROPERTY_ACTIVE, true);
        value.setActive(active);
        return value;
    }

    protected final long getEnergyUsage(final Map<String, Object> properties) {
        return (long) properties.getOrDefault(PROPERTY_ENERGY_USAGE, 0L);
    }

    protected abstract AbstractNetworkNode innerCreate(Map<String, Object> properties);
}
