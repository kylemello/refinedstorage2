package com.refinedmods.refinedstorage.common.grid.screen;

import com.refinedmods.refinedstorage.common.grid.AbstractGridContainerMenu;
import com.refinedmods.refinedstorage.common.grid.GridSortingTypes;
import com.refinedmods.refinedstorage.common.support.widget.AbstractSideButtonWidget;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

class SortingTypeSideButtonWidget extends AbstractSideButtonWidget {
    private static final MutableComponent TITLE = createTranslation("gui", "grid.sorting.type");
    private static final MutableComponent SUBTEXT_QUANTITY = createTranslation("gui", "grid.sorting.type.quantity");
    private static final MutableComponent SUBTEXT_NAME = createTranslation("gui", "grid.sorting.type.name");
    private static final MutableComponent SUBTEXT_ID = createTranslation("gui", "grid.sorting.type.id");
    private static final MutableComponent SUBTEXT_LAST_MODIFIED =
        createTranslation("gui", "grid.sorting.type.last_modified");
    private static final ResourceLocation QUANTITY = createIdentifier("widget/side_button/grid/sorting_type/quantity");
    private static final ResourceLocation NAME = createIdentifier("widget/side_button/grid/sorting_type/name");
    private static final ResourceLocation ID = createIdentifier("widget/side_button/grid/sorting_type/id");
    private static final ResourceLocation LAST_MODIFIED =
        createIdentifier("widget/side_button/grid/sorting_type/last_modified");

    private final AbstractGridContainerMenu menu;

    SortingTypeSideButtonWidget(final AbstractGridContainerMenu menu) {
        super(createPressAction(menu));
        this.menu = menu;
    }

    private static OnPress createPressAction(final AbstractGridContainerMenu menu) {
        return btn -> menu.setSortingType(toggle(menu.getSortingType()));
    }

    private static GridSortingTypes toggle(final GridSortingTypes sortingType) {
        return switch (sortingType) {
            case QUANTITY -> GridSortingTypes.NAME;
            case NAME -> GridSortingTypes.ID;
            case ID -> GridSortingTypes.LAST_MODIFIED;
            case LAST_MODIFIED -> GridSortingTypes.QUANTITY;
        };
    }

    @Override
    protected ResourceLocation getSprite() {
        return switch (menu.getSortingType()) {
            case QUANTITY -> QUANTITY;
            case NAME -> NAME;
            case ID -> ID;
            case LAST_MODIFIED -> LAST_MODIFIED;
        };
    }

    @Override
    protected MutableComponent getTitle() {
        return TITLE;
    }

    @Override
    protected MutableComponent getSubText() {
        return switch (menu.getSortingType()) {
            case QUANTITY -> SUBTEXT_QUANTITY;
            case NAME -> SUBTEXT_NAME;
            case ID -> SUBTEXT_ID;
            case LAST_MODIFIED -> SUBTEXT_LAST_MODIFIED;
        };
    }
}
