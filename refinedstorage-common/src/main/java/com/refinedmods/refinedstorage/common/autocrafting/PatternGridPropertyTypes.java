package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.support.containermenu.PropertyType;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;

final class PatternGridPropertyTypes {
    static final PropertyType<PatternType> PATTERN_TYPE = new PropertyType<>(
        createIdentifier("pattern_type"),
        PatternTypeSettings::getPatternType,
        PatternTypeSettings::getPatternType
    );

    private PatternGridPropertyTypes() {
    }
}
