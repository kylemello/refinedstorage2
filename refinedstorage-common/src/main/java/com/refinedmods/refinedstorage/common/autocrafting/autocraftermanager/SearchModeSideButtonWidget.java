package com.refinedmods.refinedstorage.common.autocrafting.autocraftermanager;

import com.refinedmods.refinedstorage.common.support.widget.AbstractSideButtonWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

class SearchModeSideButtonWidget extends AbstractSideButtonWidget {
    private static final MutableComponent TITLE = createTranslation("gui", "autocrafter_manager.search_mode");
    private static final MutableComponent SUBTEXT_ALL = createTranslation("gui", "autocrafter_manager.search_mode.all");
    private static final MutableComponent HELP_ALL =
        createTranslation("gui", "autocrafter_manager.search_mode.all.help");
    private static final MutableComponent SUBTEXT_PATTERN_INPUTS =
        createTranslation("gui", "autocrafter_manager.search_mode.pattern_inputs");
    private static final MutableComponent HELP_PATTERN_INPUTS =
        createTranslation("gui", "autocrafter_manager.search_mode.pattern_inputs.help");
    private static final MutableComponent SUBTEXT_PATTERN_OUTPUTS =
        createTranslation("gui", "autocrafter_manager.search_mode.pattern_outputs");
    private static final MutableComponent HELP_PATTERN_OUTPUTS =
        createTranslation("gui", "autocrafter_manager.search_mode.pattern_outputs.help");
    private static final MutableComponent SUBTEXT_AUTOCRAFTER_NAMES =
        createTranslation("gui", "autocrafter_manager.search_mode.autocrafter_names");
    private static final MutableComponent HELP_AUTOCRAFTER_NAMES =
        createTranslation("gui", "autocrafter_manager.search_mode.autocrafter_names.help");
    private static final ResourceLocation SPRITE_ALL =
        createIdentifier("widget/side_button/autocrafter_manager/search_mode/all");
    private static final ResourceLocation SPRITE_PATTERN_INPUTS =
        createIdentifier("widget/side_button/autocrafter_manager/search_mode/pattern_inputs");
    private static final ResourceLocation SPRITE_PATTERN_OUTPUTS =
        createIdentifier("widget/side_button/autocrafter_manager/search_mode/pattern_outputs");
    private static final ResourceLocation SPRITE_AUTOCRAFTER_NAMES =
        createIdentifier("widget/side_button/autocrafter_manager/search_mode/autocrafter_names");

    private final AutocrafterManagerContainerMenu containerMenu;

    SearchModeSideButtonWidget(final AutocrafterManagerContainerMenu containerMenu) {
        super(createPressAction(containerMenu));
        this.containerMenu = containerMenu;
    }

    private static OnPress createPressAction(final AutocrafterManagerContainerMenu containerMenu) {
        return btn -> containerMenu.setSearchMode(containerMenu.getSearchMode().toggle());
    }

    @Override
    protected ResourceLocation getSprite() {
        return switch (containerMenu.getSearchMode()) {
            case ALL -> SPRITE_ALL;
            case PATTERN_INPUTS -> SPRITE_PATTERN_INPUTS;
            case PATTERN_OUTPUTS -> SPRITE_PATTERN_OUTPUTS;
            case AUTOCRAFTER_NAMES -> SPRITE_AUTOCRAFTER_NAMES;
        };
    }

    @Override
    protected MutableComponent getTitle() {
        return TITLE;
    }

    @Override
    protected MutableComponent getSubText() {
        return switch (containerMenu.getSearchMode()) {
            case ALL -> SUBTEXT_ALL;
            case PATTERN_INPUTS -> SUBTEXT_PATTERN_INPUTS;
            case PATTERN_OUTPUTS -> SUBTEXT_PATTERN_OUTPUTS;
            case AUTOCRAFTER_NAMES -> SUBTEXT_AUTOCRAFTER_NAMES;
        };
    }

    @Override
    protected Component getHelpText() {
        return switch (containerMenu.getSearchMode()) {
            case ALL -> HELP_ALL;
            case PATTERN_INPUTS -> HELP_PATTERN_INPUTS;
            case PATTERN_OUTPUTS -> HELP_PATTERN_OUTPUTS;
            case AUTOCRAFTER_NAMES -> HELP_AUTOCRAFTER_NAMES;
        };
    }
}
