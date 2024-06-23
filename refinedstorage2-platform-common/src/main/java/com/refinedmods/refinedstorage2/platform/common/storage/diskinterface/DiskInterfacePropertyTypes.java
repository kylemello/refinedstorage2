package com.refinedmods.refinedstorage2.platform.common.storage.diskinterface;

import com.refinedmods.refinedstorage2.api.network.impl.node.storagetransfer.StorageTransferMode;
import com.refinedmods.refinedstorage2.platform.common.support.containermenu.PropertyType;

import static com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil.createIdentifier;

class DiskInterfacePropertyTypes {
    public static final PropertyType<StorageTransferMode> TRANSFER_MODE = new PropertyType<>(
        createIdentifier("transfer_mode"),
        TransferModeSettings::getTransferMode,
        TransferModeSettings::getTransferMode
    );

    private DiskInterfacePropertyTypes() {
    }
}
