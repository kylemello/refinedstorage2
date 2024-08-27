package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.widget.AbstractSideButtonWidget;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

class LockModeSideButtonWidget extends AbstractSideButtonWidget {
    private static final String PREFIX = "crafter.lock_mode";

    private static final MutableComponent TITLE = createTranslation("gui", PREFIX);
    private static final ResourceLocation NEVER = createIdentifier("widget/side_button/crafter_lock_mode/never");
    private static final ResourceLocation LOCK_UNTIL_REDSTONE_PULSE_RECEIVED = createIdentifier(
        "widget/side_button/crafter_lock_mode/lock_until_redstone_pulse_is_received"
    );
    private static final ResourceLocation LOCK_UNTIL_CONNECTED_MACHINE_IS_EMPTY = createIdentifier(
        "widget/side_button/crafter_lock_mode/lock_until_connected_machine_is_empty"
    );
    private static final ResourceLocation LOCK_UNTIL_ALL_OUTPUTS_ARE_RECEIVED = createIdentifier(
        "widget/side_button/crafter_lock_mode/lock_until_all_outputs_are_received"
    );
    private static final ResourceLocation LOCK_UNTIL_HIGH_REDSTONE_SIGNAL = createIdentifier(
        "widget/side_button/crafter_lock_mode/lock_until_high_redstone_signal"
    );
    private static final ResourceLocation LOCK_UNTIL_LOW_REDSTONE_SIGNAL = createIdentifier(
        "widget/side_button/crafter_lock_mode/lock_until_low_redstone_signal"
    );

    private static final MutableComponent NEVER_TITLE = createTranslation("gui", PREFIX + ".never");
    private static final MutableComponent LOCK_UNTIL_REDSTONE_PULSE_RECEIVED_TITLE = createTranslation(
        "gui", PREFIX + ".lock_until_redstone_pulse_received"
    );
    private static final MutableComponent LOCK_UNTIL_CONNECTED_MACHINE_IS_EMPTY_TITLE = createTranslation(
        "gui", PREFIX + ".lock_until_connected_machine_is_empty"
    );
    private static final MutableComponent LOCK_UNTIL_ALL_OUTPUTS_ARE_RECEIVED_TITLE = createTranslation(
        "gui", PREFIX + ".lock_until_all_outputs_are_received"
    );
    private static final MutableComponent LOCK_UNTIL_HIGH_REDSTONE_SIGNAL_TITLE = createTranslation(
        "gui", PREFIX + ".lock_until_high_redstone_signal"
    );
    private static final MutableComponent LOCK_UNTIL_LOW_REDSTONE_SIGNAL_TITLE = createTranslation(
        "gui", PREFIX + ".lock_until_low_redstone_signal"
    );

    private final ClientProperty<LockMode> property;

    LockModeSideButtonWidget(final ClientProperty<LockMode> property) {
        super(createPressAction(property));
        this.property = property;
    }

    private static OnPress createPressAction(final ClientProperty<LockMode> property) {
        return btn -> property.setValue(property.getValue().toggle());
    }

    @Override
    protected ResourceLocation getSprite() {
        return switch (property.getValue()) {
            case NEVER -> NEVER;
            case LOCK_UNTIL_REDSTONE_PULSE_RECEIVED -> LOCK_UNTIL_REDSTONE_PULSE_RECEIVED;
            case LOCK_UNTIL_CONNECTED_MACHINE_IS_EMPTY -> LOCK_UNTIL_CONNECTED_MACHINE_IS_EMPTY;
            case LOCK_UNTIL_ALL_OUTPUTS_ARE_RECEIVED -> LOCK_UNTIL_ALL_OUTPUTS_ARE_RECEIVED;
            case LOCK_UNTIL_HIGH_REDSTONE_SIGNAL -> LOCK_UNTIL_HIGH_REDSTONE_SIGNAL;
            case LOCK_UNTIL_LOW_REDSTONE_SIGNAL -> LOCK_UNTIL_LOW_REDSTONE_SIGNAL;
        };
    }

    @Override
    protected MutableComponent getTitle() {
        return TITLE;
    }

    @Override
    protected MutableComponent getSubText() {
        return switch (property.getValue()) {
            case NEVER -> NEVER_TITLE;
            case LOCK_UNTIL_REDSTONE_PULSE_RECEIVED -> LOCK_UNTIL_REDSTONE_PULSE_RECEIVED_TITLE;
            case LOCK_UNTIL_CONNECTED_MACHINE_IS_EMPTY -> LOCK_UNTIL_CONNECTED_MACHINE_IS_EMPTY_TITLE;
            case LOCK_UNTIL_ALL_OUTPUTS_ARE_RECEIVED -> LOCK_UNTIL_ALL_OUTPUTS_ARE_RECEIVED_TITLE;
            case LOCK_UNTIL_HIGH_REDSTONE_SIGNAL -> LOCK_UNTIL_HIGH_REDSTONE_SIGNAL_TITLE;
            case LOCK_UNTIL_LOW_REDSTONE_SIGNAL -> LOCK_UNTIL_LOW_REDSTONE_SIGNAL_TITLE;
        };
    }
}
