package com.refinedmods.refinedstorage.common.constructordestructor;

import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.widget.AbstractYesNoSideButtonWidget;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

class DestructorPickupItemsSideButtonWidget extends AbstractYesNoSideButtonWidget {
    DestructorPickupItemsSideButtonWidget(final ClientProperty<Boolean> property) {
        super(property, createTranslation("gui", "destructor.pickup_items"));
    }

    @Override
    protected int getXTexture() {
        return Boolean.TRUE.equals(property.getValue()) ? 64 : 80;
    }

    @Override
    protected int getYTexture() {
        return 0;
    }
}
