package com.refinedmods.refinedstorage.network.test.nodefactory;

import com.refinedmods.refinedstorage.api.network.node.NetworkNode;

import java.util.Map;

@FunctionalInterface
public interface NetworkNodeFactory {
    NetworkNode create(Map<String, Object> properties);
}
