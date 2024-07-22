package com.refinedmods.refinedstorage.common.api.upgrade;

import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.2.10")
public interface UpgradeState {
    default boolean has(UpgradeItem upgradeItem) {
        return getAmount(upgradeItem) > 0;
    }

    int getAmount(UpgradeItem upgradeItem);
}
