package com.refinedmods.refinedstorage.common.autocrafting.autocraftermanager;

public enum AutocrafterManagerSearchMode {
    ALL,
    PATTERN_INPUTS,
    PATTERN_OUTPUTS,
    AUTOCRAFTER_NAMES;

    AutocrafterManagerSearchMode toggle() {
        return switch (this) {
            case ALL -> PATTERN_INPUTS;
            case PATTERN_INPUTS -> PATTERN_OUTPUTS;
            case PATTERN_OUTPUTS -> AUTOCRAFTER_NAMES;
            case AUTOCRAFTER_NAMES -> ALL;
        };
    }

    boolean isSlotVisible(final AutocrafterManagerContainerMenu.Group group,
                          final String normalizedQuery,
                          final int index) {
        return switch (this) {
            case ALL -> group.nameContains(normalizedQuery)
                || group.hasPatternInput(normalizedQuery, index)
                || group.hasPatternOutput(normalizedQuery, index);
            case PATTERN_INPUTS -> group.hasPatternInput(normalizedQuery, index);
            case PATTERN_OUTPUTS -> group.hasPatternOutput(normalizedQuery, index);
            case AUTOCRAFTER_NAMES -> group.nameContains(normalizedQuery);
        };
    }
}
