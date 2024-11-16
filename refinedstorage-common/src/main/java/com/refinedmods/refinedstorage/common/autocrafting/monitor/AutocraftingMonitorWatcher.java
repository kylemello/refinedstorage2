package com.refinedmods.refinedstorage.common.autocrafting.monitor;

@FunctionalInterface
interface AutocraftingMonitorWatcher {
    void activeChanged(boolean active);
}
