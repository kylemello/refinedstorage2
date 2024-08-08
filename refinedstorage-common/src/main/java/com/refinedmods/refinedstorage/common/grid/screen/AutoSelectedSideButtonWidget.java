package com.refinedmods.refinedstorage.common.grid.screen;

import com.refinedmods.refinedstorage.common.grid.AbstractGridContainerMenu;
import com.refinedmods.refinedstorage.common.support.widget.AbstractSideButtonWidget;
import com.refinedmods.refinedstorage.common.util.IdentifierUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

class AutoSelectedSideButtonWidget extends AbstractSideButtonWidget {
    private static final MutableComponent TITLE = createTranslation("gui", "grid.auto_selected");
    private static final ResourceLocation YES = createIdentifier("widget/side_button/grid/autoselected/yes");
    private static final ResourceLocation NO = createIdentifier("widget/side_button/grid/autoselected/no");
    private static final Component HELP = createTranslation("gui", "grid.auto_selected.help");

    private final AbstractGridContainerMenu menu;

    AutoSelectedSideButtonWidget(final AbstractGridContainerMenu menu) {
        super(createPressAction(menu));
        this.menu = menu;
    }

    private static OnPress createPressAction(final AbstractGridContainerMenu menu) {
        return btn -> menu.setAutoSelected(!menu.isAutoSelected());
    }

    @Override
    protected ResourceLocation getSprite() {
        return menu.isAutoSelected() ? YES : NO;
    }

    @Override
    protected MutableComponent getTitle() {
        return TITLE;
    }

    @Override
    protected MutableComponent getSubText() {
        return menu.isAutoSelected() ? IdentifierUtil.YES : IdentifierUtil.NO;
    }

    @Override
    protected Component getHelpText() {
        return HELP;
    }
}
