package com.refinedmods.refinedstorage.common.support;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceFactory;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceRendering;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeMapping;
import com.refinedmods.refinedstorage.common.support.amount.ResourceAmountScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.AbstractResourceContainerMenu;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;
import com.refinedmods.refinedstorage.common.support.tooltip.HelpClientTooltipComponent;
import com.refinedmods.refinedstorage.common.support.tooltip.MouseClientTooltipComponent;
import com.refinedmods.refinedstorage.common.support.tooltip.SmallTextClientTooltipComponent;
import com.refinedmods.refinedstorage.common.support.widget.AbstractSideButtonWidget;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeItemClientTooltipComponent;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeSlot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.apiguardian.api.API;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslationAsHeading;
import static java.util.Objects.requireNonNullElse;

public abstract class AbstractBaseScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {
    private static final SmallTextClientTooltipComponent CLICK_TO_CLEAR = new SmallTextClientTooltipComponent(
        createTranslationAsHeading("gui", "filter_slot.click_to_clear")
    );
    private static final ClientTooltipComponent EMPTY_FILTER = ClientTooltipComponent.create(
        createTranslationAsHeading("gui", "filter_slot.empty_filter").getVisualOrderText()
    );

    private final Inventory playerInventory;
    private final List<Rect2i> exclusionZones = new ArrayList<>();
    private int sideButtonY;

    @Nullable
    private List<ClientTooltipComponent> deferredTooltip;

    protected AbstractBaseScreen(final T menu, final Inventory playerInventory, final Component text) {
        super(menu, playerInventory, text);
        this.playerInventory = playerInventory;
        this.titleLabelX = 7;
        this.titleLabelY = 7;
        this.inventoryLabelX = 7;
    }

    protected int getSideButtonY() {
        return 6;
    }

    @Override
    protected void init() {
        clearWidgets();
        super.init();
        sideButtonY = getSideButtonY();
    }

    @Override
    protected void clearWidgets() {
        super.clearWidgets();
        exclusionZones.clear();
    }

    protected abstract ResourceLocation getTexture();

    @Override
    protected void renderBg(final GuiGraphics graphics, final float delta, final int mouseX, final int mouseY) {
        final int x = (width - imageWidth) / 2;
        final int y = (height - imageHeight) / 2;
        graphics.blit(getTexture(), x, y, 0, 0, imageWidth, imageHeight);
        renderResourceSlots(graphics);
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }

    protected void renderResourceSlots(final GuiGraphics graphics) {
        if (!(menu instanceof AbstractResourceContainerMenu resourceContainerMenu)) {
            return;
        }
        for (final ResourceSlot slot : resourceContainerMenu.getResourceSlots()) {
            tryRenderResourceSlot(graphics, slot);
        }
    }

    protected final void tryRenderResourceSlot(final GuiGraphics graphics, final ResourceSlot slot) {
        final ResourceKey resource = slot.getResource();
        if (resource == null) {
            return;
        }
        renderResourceSlot(
            graphics,
            leftPos + slot.x,
            topPos + slot.y,
            resource,
            slot.getAmount(),
            slot.shouldRenderAmount()
        );
    }

    private void renderResourceSlot(final GuiGraphics graphics,
                                    final int x,
                                    final int y,
                                    final ResourceKey resource,
                                    final long amount,
                                    final boolean renderAmount) {
        final ResourceRendering rendering = RefinedStorageApi.INSTANCE.getResourceRendering(resource);
        rendering.render(resource, graphics, x, y);
        if (renderAmount) {
            renderResourceAmount(graphics, x, y, amount, rendering);
        }
    }

    public static void renderResourceAmount(final GuiGraphics graphics,
                                            final int x,
                                            final int y,
                                            final long amount,
                                            final ResourceRendering rendering) {
        renderAmount(
            graphics,
            x,
            y,
            rendering.getDisplayedAmount(amount, true),
            requireNonNullElse(ChatFormatting.WHITE.getColor(), 15),
            true
        );
    }

    protected static void renderAmount(final GuiGraphics graphics,
                                       final int x,
                                       final int y,
                                       final String amount,
                                       final int color,
                                       final boolean large) {
        final Font font = Minecraft.getInstance().font;
        final PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        // Large amounts overlap with the slot lines (see Minecraft behavior)
        poseStack.translate(x + (large ? 1D : 0D), y + (large ? 1D : 0D), 199);
        if (!large) {
            poseStack.scale(0.5F, 0.5F, 1);
        }
        graphics.drawString(font, amount, (large ? 16 : 30) - font.width(amount), large ? 8 : 22, color, true);
        poseStack.popPose();
    }

    public void addSideButton(final AbstractSideButtonWidget button) {
        button.setX(leftPos - button.getWidth() - 2);
        button.setY(topPos + sideButtonY);
        exclusionZones.add(new Rect2i(button.getX(), button.getY(), button.getWidth(), button.getHeight()));
        sideButtonY += button.getHeight() + 2;
        addRenderableWidget(button);
    }

    @API(status = API.Status.INTERNAL)
    public List<Rect2i> getExclusionZones() {
        return exclusionZones;
    }

    @Override
    protected void renderTooltip(final GuiGraphics graphics, final int x, final int y) {
        if (hoveredSlot instanceof UpgradeSlot upgradeSlot) {
            final List<ClientTooltipComponent> tooltip = getUpgradeTooltip(menu.getCarried(), upgradeSlot);
            if (!tooltip.isEmpty()) {
                Platform.INSTANCE.renderTooltip(graphics, tooltip, x, y);
                return;
            }
        }
        if (hoveredSlot instanceof ResourceSlot resourceSlot && canInteractWithResourceSlot(resourceSlot, x, y)) {
            final List<ClientTooltipComponent> tooltip = getResourceTooltip(menu.getCarried(), resourceSlot);
            if (!tooltip.isEmpty()) {
                Platform.INSTANCE.renderTooltip(graphics, tooltip, x, y);
                return;
            }
        }
        if (deferredTooltip != null) {
            Platform.INSTANCE.renderTooltip(graphics, deferredTooltip, x, y);
            deferredTooltip = null;
        }
        super.renderTooltip(graphics, x, y);
    }

    public void setDeferredTooltip(@Nullable final List<ClientTooltipComponent> deferredTooltip) {
        this.deferredTooltip = deferredTooltip;
    }

    private List<ClientTooltipComponent> getUpgradeTooltip(final ItemStack carried, final UpgradeSlot upgradeSlot) {
        if (!carried.isEmpty() || upgradeSlot.hasItem()) {
            return Collections.emptyList();
        }
        final List<ClientTooltipComponent> lines = new ArrayList<>();
        lines.add(ClientTooltipComponent.create(
            createTranslationAsHeading("gui", "upgrade_slot").getVisualOrderText()
        ));
        for (final UpgradeMapping upgrade : upgradeSlot.getAllowedUpgrades()) {
            lines.add(new UpgradeItemClientTooltipComponent(upgrade));
        }
        return lines;
    }

    public List<ClientTooltipComponent> getResourceTooltip(final ItemStack carried, final ResourceSlot resourceSlot) {
        final ResourceKey resource = resourceSlot.getResource();
        if (resource == null) {
            return getTooltipForEmptySlot(carried, resourceSlot);
        }
        return getTooltipForResource(resource, resourceSlot);
    }

    private List<ClientTooltipComponent> getTooltipForEmptySlot(final ItemStack carried,
                                                                final ResourceSlot resourceSlot) {
        if (resourceSlot.isDisabled() || resourceSlot.supportsItemSlotInteractions()) {
            return Collections.emptyList();
        }
        final List<ClientTooltipComponent> tooltip = new ArrayList<>();
        tooltip.add(EMPTY_FILTER);
        tooltip.addAll(getResourceSlotHelpTooltip(carried, resourceSlot));
        tooltip.add(HelpClientTooltipComponent.create(resourceSlot.getHelpText()));
        return tooltip;
    }

    private List<ClientTooltipComponent> getResourceSlotHelpTooltip(final ItemStack carried,
                                                                    final ResourceSlot resourceSlot) {
        if (carried.isEmpty()) {
            return Collections.emptyList();
        }
        final List<ClientTooltipComponent> lines = new ArrayList<>();
        resourceSlot.getPrimaryResourceFactory().create(carried).ifPresent(primaryResourceInstance -> lines.add(
            MouseClientTooltipComponent.resource(
                MouseClientTooltipComponent.Type.LEFT,
                primaryResourceInstance.resource(),
                null
            )
        ));
        for (final ResourceFactory alternativeResourceFactory : resourceSlot.getAlternativeResourceFactories()) {
            final var result = alternativeResourceFactory.create(carried);
            result.ifPresent(alternativeResourceInstance -> lines.add(MouseClientTooltipComponent.resource(
                MouseClientTooltipComponent.Type.RIGHT,
                alternativeResourceInstance.resource(),
                null
            )));
        }
        return lines;
    }

    private List<ClientTooltipComponent> getTooltipForResource(final ResourceKey resource,
                                                               final ResourceSlot resourceSlot) {
        final List<ClientTooltipComponent> tooltip = RefinedStorageApi.INSTANCE
            .getResourceRendering(resource)
            .getTooltip(resource)
            .stream()
            .map(Component::getVisualOrderText)
            .map(ClientTooltipComponent::create)
            .collect(Collectors.toList());
        if (!resourceSlot.isDisabled() && !resourceSlot.supportsItemSlotInteractions()) {
            tooltip.add(CLICK_TO_CLEAR);
        }
        if (resourceSlot.supportsItemSlotInteractions()) {
            RefinedStorageApi.INSTANCE.getResourceContainerInsertStrategies()
                .stream()
                .flatMap(strategy -> strategy.getConversionInfo(resource).stream())
                .map(conversionInfo -> MouseClientTooltipComponent.itemConversion(
                    MouseClientTooltipComponent.Type.LEFT,
                    conversionInfo.from(),
                    conversionInfo.to(),
                    null
                ))
                .forEach(tooltip::add);
        }
        return tooltip;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int clickedButton) {
        if (hoveredSlot instanceof ResourceSlot resourceSlot
            && !resourceSlot.supportsItemSlotInteractions()
            && !resourceSlot.isDisabled()
            && canInteractWithResourceSlot(resourceSlot, mouseX, mouseY)) {
            if (!tryOpenResourceAmountScreen(resourceSlot)
                && getMenu() instanceof AbstractResourceContainerMenu resourceMenu) {
                resourceMenu.sendResourceSlotChange(hoveredSlot.index, clickedButton == 1);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    private boolean tryOpenResourceAmountScreen(final ResourceSlot slot) {
        final boolean isFilterSlot = slot.getResource() != null;
        final boolean canModifyAmount = isFilterSlot && slot.canModifyAmount();
        final boolean isNotTryingToRemoveFilter = !hasShiftDown();
        final boolean isNotCarryingItem = getMenu().getCarried().isEmpty();
        final boolean canOpen = isFilterSlot
            && canModifyAmount
            && isNotTryingToRemoveFilter
            && isNotCarryingItem;
        if (canOpen && minecraft != null) {
            minecraft.setScreen(new ResourceAmountScreen(this, playerInventory, slot));
        }
        return canOpen;
    }

    protected boolean canInteractWithResourceSlot(final ResourceSlot resourceSlot,
                                                  final double mouseX,
                                                  final double mouseY) {
        return true;
    }

    @Nullable
    @API(status = API.Status.INTERNAL)
    public PlatformResourceKey getHoveredResource() {
        if (hoveredSlot instanceof ResourceSlot resourceSlot) {
            return resourceSlot.getResource();
        }
        return null;
    }

    @API(status = API.Status.INTERNAL)
    public int getLeftPos() {
        return leftPos;
    }

    @API(status = API.Status.INTERNAL)
    public int getTopPos() {
        return topPos;
    }
}
