package com.refinedmods.refinedstorage.common.grid.screen;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceType;
import com.refinedmods.refinedstorage.common.grid.AbstractGridContainerMenu;
import com.refinedmods.refinedstorage.common.support.widget.AbstractSideButtonWidget;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

class ResourceTypeSideButtonWidget extends AbstractSideButtonWidget {
    private static final MutableComponent TITLE = createTranslation("gui", "grid.resource_type");
    private static final MutableComponent SUBTEXT_ALL = createTranslation("gui", "grid.resource_type.all");
    private static final ResourceLocation ALL = createIdentifier("widget/side_button/resource_type/all");

    private final AbstractGridContainerMenu menu;

    ResourceTypeSideButtonWidget(final AbstractGridContainerMenu menu) {
        super(createPressAction(menu));
        this.menu = menu;
    }

    private static OnPress createPressAction(final AbstractGridContainerMenu menu) {
        return btn -> menu.toggleResourceType();
    }

    @Override
    protected ResourceLocation getSprite() {
        final ResourceType resourceType = menu.getResourceType();
        if (resourceType == null) {
            return ALL;
        }
        return resourceType.getSprite();
    }

    @Override
    protected MutableComponent getTitle() {
        return TITLE;
    }

    @Override
    protected MutableComponent getSubText() {
        final ResourceType resourceType = menu.getResourceType();
        if (resourceType == null) {
            return SUBTEXT_ALL;
        }
        return resourceType.getTitle();
    }
}
