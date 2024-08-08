package com.refinedmods.refinedstorage.common.support.stretching;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.support.widget.AbstractSideButtonWidget;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

public class ScreenSizeSideButtonWidget extends AbstractSideButtonWidget {
    private static final MutableComponent TITLE = createTranslation("gui", "screen_size");
    private static final MutableComponent SUBTEXT_STRETCH = createTranslation("gui", "screen_size.stretch");
    private static final MutableComponent SUBTEXT_SMALL = createTranslation("gui", "screen_size.small");
    private static final MutableComponent SUBTEXT_MEDIUM = createTranslation("gui", "screen_size.medium");
    private static final MutableComponent SUBTEXT_LARGE = createTranslation("gui", "screen_size.large");
    private static final MutableComponent SUBTEXT_EXTRA_LARGE = createTranslation("gui", "screen_size.extra_large");
    private static final ResourceLocation STRETCH = createIdentifier("widget/side_button/screen_size/stretch");
    private static final ResourceLocation SMALL = createIdentifier("widget/side_button/screen_size/small");
    private static final ResourceLocation MEDIUM = createIdentifier("widget/side_button/screen_size/medium");
    private static final ResourceLocation EXTRA_LARGE = createIdentifier("widget/side_button/screen_size/extra_large");

    public ScreenSizeSideButtonWidget(final AbstractStretchingScreen<?> stretchingScreen) {
        super(createPressAction(stretchingScreen));
    }

    private static OnPress createPressAction(final AbstractStretchingScreen<?> stretchingScreen) {
        return btn -> {
            Platform.INSTANCE.getConfig().setScreenSize(Platform.INSTANCE.getConfig().getScreenSize().toggle());
            stretchingScreen.init();
        };
    }

    @Override
    protected ResourceLocation getSprite() {
        final ScreenSize screenSize = Platform.INSTANCE.getConfig().getScreenSize();
        return switch (screenSize) {
            case STRETCH -> STRETCH;
            case SMALL -> SMALL;
            case MEDIUM -> MEDIUM;
            case LARGE, EXTRA_LARGE -> EXTRA_LARGE;
        };
    }

    @Override
    protected MutableComponent getTitle() {
        return TITLE;
    }

    @Override
    protected MutableComponent getSubText() {
        final ScreenSize screenSize = Platform.INSTANCE.getConfig().getScreenSize();
        return switch (screenSize) {
            case STRETCH -> SUBTEXT_STRETCH;
            case SMALL -> SUBTEXT_SMALL;
            case MEDIUM -> SUBTEXT_MEDIUM;
            case LARGE -> SUBTEXT_LARGE;
            case EXTRA_LARGE -> SUBTEXT_EXTRA_LARGE;
        };
    }
}
