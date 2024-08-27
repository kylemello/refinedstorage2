package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.support.containermenu.PropertyType;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

final class CrafterPropertyTypes {
    static final PropertyType<LockMode> LOCK_MODE = new PropertyType<>(
        createIdentifier("lock_mode"),
        LockModeSettings::getLockMode,
        LockModeSettings::getLockMode
    );

    static final PropertyType<Integer> PRIORITY = PropertyTypes.createIntegerProperty(
        createIdentifier("crafter_priority")
    );

    private CrafterPropertyTypes() {
    }
}
