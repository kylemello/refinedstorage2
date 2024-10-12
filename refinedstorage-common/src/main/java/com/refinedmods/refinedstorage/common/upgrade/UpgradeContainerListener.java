package com.refinedmods.refinedstorage.common.upgrade;

@FunctionalInterface
public interface UpgradeContainerListener {
    void updateState(long upgradeEnergyUsage);
}
