package com.refinedmods.refinedstorage.common.support.packet;

@FunctionalInterface
public interface PacketHandler<T> {
    void handle(T packet, PacketContext ctx);
}
