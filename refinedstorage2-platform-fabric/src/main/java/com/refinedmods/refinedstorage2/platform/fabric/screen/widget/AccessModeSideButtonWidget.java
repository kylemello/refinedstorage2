package com.refinedmods.refinedstorage2.platform.fabric.screen.widget;

import com.refinedmods.refinedstorage2.api.storage.AccessMode;
import com.refinedmods.refinedstorage2.platform.fabric.Rs2Mod;
import com.refinedmods.refinedstorage2.platform.fabric.screen.TooltipRenderer;
import com.refinedmods.refinedstorage2.platform.fabric.screenhandler.AccessModeAccessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class AccessModeSideButtonWidget extends SideButtonWidget {
    private final AccessModeAccessor accessModeAccessor;
    private final TooltipRenderer tooltipRenderer;
    private final Map<AccessMode, List<Component>> tooltips = new EnumMap<>(AccessMode.class);

    public AccessModeSideButtonWidget(AccessModeAccessor accessModeAccessor, TooltipRenderer tooltipRenderer) {
        super(createPressAction(accessModeAccessor));
        this.accessModeAccessor = accessModeAccessor;
        this.tooltipRenderer = tooltipRenderer;
        Arrays.stream(AccessMode.values()).forEach(accessMode -> tooltips.put(accessMode, calculateTooltip(accessMode)));
    }

    private static OnPress createPressAction(AccessModeAccessor accessModeAccessor) {
        return btn -> accessModeAccessor.setAccessMode(accessModeAccessor.getAccessMode().toggle());
    }

    private List<Component> calculateTooltip(AccessMode accessMode) {
        List<Component> lines = new ArrayList<>();
        lines.add(Rs2Mod.createTranslation("gui", "access_mode"));
        lines.add(Rs2Mod.createTranslation("gui", "access_mode." + accessMode.toString().toLowerCase(Locale.ROOT)).withStyle(ChatFormatting.GRAY));
        return lines;
    }

    @Override
    protected int getXTexture() {
        switch (accessModeAccessor.getAccessMode()) {
            case INSERT_EXTRACT:
                return 0;
            case INSERT:
                return 16;
            case EXTRACT:
                return 32;
            default:
                return 0;
        }
    }

    @Override
    protected int getYTexture() {
        return 240;
    }

    @Override
    public void onTooltip(Button button, PoseStack matrices, int mouseX, int mouseY) {
        tooltipRenderer.render(matrices, tooltips.get(accessModeAccessor.getAccessMode()), mouseX, mouseY);
    }
}
