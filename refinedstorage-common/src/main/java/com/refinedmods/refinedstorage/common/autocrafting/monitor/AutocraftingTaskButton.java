package com.refinedmods.refinedstorage.common.autocrafting.monitor;

import com.refinedmods.refinedstorage.api.autocrafting.status.AutocraftingTaskStatus;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceRendering;
import com.refinedmods.refinedstorage.common.support.tooltip.SmallText;
import com.refinedmods.refinedstorage.common.support.widget.TextMarquee;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import static com.refinedmods.refinedstorage.common.autocrafting.monitor.AutocraftingMonitorScreen.TASK_BUTTON_HEIGHT;
import static com.refinedmods.refinedstorage.common.autocrafting.monitor.AutocraftingMonitorScreen.TASK_BUTTON_WIDTH;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;

class AutocraftingTaskButton extends AbstractButton {
    private final AutocraftingTaskStatus.Id taskId;
    private final TextMarquee text;
    private final Consumer<AutocraftingTaskStatus.Id> onPress;

    AutocraftingTaskButton(final int x,
                           final int y,
                           final AutocraftingTaskStatus.Id taskId,
                           final Consumer<AutocraftingTaskStatus.Id> onPress) {
        super(x, y, TASK_BUTTON_WIDTH, TASK_BUTTON_HEIGHT, Component.empty());
        this.taskId = taskId;
        final ResourceKey resource = taskId.resource();
        final ResourceRendering rendering = RefinedStorageApi.INSTANCE.getResourceRendering(resource.getClass());
        this.text = new TextMarquee(Component.literal(rendering.formatAmount(taskId.amount(), true))
            .append(" ")
            .append(rendering.getDisplayName(resource)),
            TASK_BUTTON_WIDTH - 16 - 4 - 4 - 4,
            0xFFFFFF,
            true,
            true);
        this.onPress = onPress;
    }

    AutocraftingTaskStatus.Id getTaskId() {
        return taskId;
    }

    @Override
    protected void renderWidget(final GuiGraphics graphics,
                                final int mouseX,
                                final int mouseY,
                                final float partialTick) {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        final ResourceKey resource = taskId.resource();
        final ResourceRendering rendering = RefinedStorageApi.INSTANCE.getResourceRendering(resource.getClass());
        rendering.render(resource, graphics, getX() + 3, getY() + 4);
        final int yOffset = SmallText.isSmall() ? 5 : 3;
        final int textX = getX() + 3 + 16 + 3;
        final int textY = getY() + yOffset;
        text.render(graphics, textX, textY, Minecraft.getInstance().font, isHovered);
        final int ySpacing = SmallText.isSmall() ? 7 : 8;
        SmallText.render(graphics, Minecraft.getInstance().font, "69%", textX, textY + ySpacing, 0xFFFFFF, true);
        updateTooltip();
    }

    private void updateTooltip() {
        if (isHovered) {
            final String runningTime = getRunningTimeText();
            setTooltip(Tooltip.create(
                createTranslation("gui", "autocrafting_monitor.running_time", runningTime)
            ));
        } else {
            setTooltip(null);
        }
    }

    private String getRunningTimeText() {
        final int totalSecs = (int) (System.currentTimeMillis() - taskId.startTime()) / 1000;
        final int hours = totalSecs / 3600;
        final int minutes = (totalSecs % 3600) / 60;
        final int seconds = totalSecs % 60;
        final String runningTime;
        if (hours > 0) {
            runningTime = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            runningTime = String.format("%02d:%02d", minutes, seconds);
        }
        return runningTime;
    }

    @Override
    public void onPress() {
        onPress.accept(taskId);
    }

    @Override
    protected void updateWidgetNarration(final NarrationElementOutput narrationElementOutput) {
        // no op
    }
}
