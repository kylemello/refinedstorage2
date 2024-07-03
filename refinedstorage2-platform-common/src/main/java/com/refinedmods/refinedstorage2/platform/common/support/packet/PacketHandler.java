package com.refinedmods.refinedstorage2.platform.common.support.packet;

@FunctionalInterface
public interface PacketHandler<T> {
    void handle(T packet, PacketContext ctx);
}
