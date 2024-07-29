package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlotType;

import java.util.function.Supplier;

import net.minecraft.network.chat.MutableComponent;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

class ProcessingMatrixResourceSlot extends ResourceSlot {
    private static final MutableComponent INPUT_HELP = createTranslation(
        "gui",
        "pattern_grid.processing.input_slots_help"
    );
    private static final MutableComponent OUTPUT_HELP = createTranslation(
        "gui",
        "pattern_grid.processing.output_slots_help"
    );

    private final Supplier<PatternType> patternTypeSupplier;
    private final boolean input;
    private final int startY;
    private final int endY;

    ProcessingMatrixResourceSlot(final ResourceContainer resourceContainer,
                                 final int index,
                                 final int x,
                                 final int y,
                                 final boolean input,
                                 final Supplier<PatternType> patternTypeSupplier,
                                 final int startY,
                                 final int endY) {
        super(resourceContainer, index, input ? INPUT_HELP : OUTPUT_HELP, x, y, ResourceSlotType.FILTER_WITH_AMOUNT);
        this.patternTypeSupplier = patternTypeSupplier;
        this.input = input;
        this.startY = startY;
        this.endY = endY;
    }

    boolean isInput() {
        return input;
    }

    @Override
    public boolean isActive() {
        final PatternType patternType = patternTypeSupplier.get();
        return patternType == PatternType.PROCESSING && y >= startY && y < endY;
    }

    @Override
    public boolean isHighlightable() {
        return false; // we render the highlight in the scissor render
    }
}
