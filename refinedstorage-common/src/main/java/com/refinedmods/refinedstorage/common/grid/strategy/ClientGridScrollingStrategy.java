package com.refinedmods.refinedstorage.common.grid.strategy;

import com.refinedmods.refinedstorage.common.api.grid.GridScrollMode;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridScrollingStrategy;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.packet.c2s.C2SPackets;

public class ClientGridScrollingStrategy implements GridScrollingStrategy {
    @Override
    public boolean onScroll(final PlatformResourceKey resource, final GridScrollMode scrollMode, final int slotIndex) {
        C2SPackets.sendGridScroll(resource, scrollMode, slotIndex);
        return true;
    }
}
