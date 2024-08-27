package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.support.containermenu.PropertyType;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

final class CrafterPropertyTypes {
    static final PropertyType<LockMode> LOCK_MODE = new PropertyType<>(
        createIdentifier("lock_mode"),
        LockModeSettings::getLockMode,
        LockModeSettings::getLockMode
    );

    private CrafterPropertyTypes() {
    }
}
