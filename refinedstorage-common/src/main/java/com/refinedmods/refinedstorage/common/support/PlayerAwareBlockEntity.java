package com.refinedmods.refinedstorage.common.support;

import java.util.UUID;

public interface PlayerAwareBlockEntity {
    void setPlacedBy(UUID playerId);
}
