package com.refinedmods.refinedstorage.common.detector;

import com.refinedmods.refinedstorage.api.network.impl.node.detector.DetectorMode;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyType;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

final class DetectorPropertyTypes {
    static final PropertyType<DetectorMode> MODE = new PropertyType<>(
        createIdentifier("detector_mode"),
        DetectorModeSettings::getDetectorMode,
        DetectorModeSettings::getDetectorMode
    );

    private DetectorPropertyTypes() {
    }
}
