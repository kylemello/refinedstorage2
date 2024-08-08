package com.refinedmods.refinedstorage.common.grid.view;

import com.refinedmods.refinedstorage.api.grid.operations.GridExtractMode;
import com.refinedmods.refinedstorage.api.grid.view.GridView;
import com.refinedmods.refinedstorage.common.api.grid.GridResourceAttributeKeys;
import com.refinedmods.refinedstorage.common.api.grid.GridScrollMode;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridExtractionStrategy;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridScrollingStrategy;
import com.refinedmods.refinedstorage.common.api.grid.view.AbstractPlatformGridResource;
import com.refinedmods.refinedstorage.common.api.support.AmountFormatting;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceType;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;
import com.refinedmods.refinedstorage.common.support.tooltip.MouseClientTooltipComponent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemGridResource extends AbstractPlatformGridResource<ItemResource> {
    private final int id;
    private final ItemStack itemStack;
    private final ItemResource itemResource;

    public ItemGridResource(final ItemResource resource,
                            final ItemStack itemStack,
                            final String name,
                            final String modId,
                            final String modName,
                            final Set<String> tags,
                            final String tooltip) {
        super(resource, name, Map.of(
            GridResourceAttributeKeys.MOD_ID, Set.of(modId),
            GridResourceAttributeKeys.MOD_NAME, Set.of(modName),
            GridResourceAttributeKeys.TAGS, tags,
            GridResourceAttributeKeys.TOOLTIP, Set.of(tooltip)
        ));
        this.id = Item.getId(resource.item());
        this.itemStack = itemStack;
        this.itemResource = resource;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public ItemResource getItemResource() {
        return itemResource;
    }

    @Override
    public int getRegistryId() {
        return id;
    }

    @Override
    public List<ClientTooltipComponent> getExtractionHints(final GridView view) {
        final long amount = getAmount(view);
        final long extractableAmount = Math.min(amount, itemStack.getMaxStackSize());
        final long halfExtractionAmount = extractableAmount == 1 ? 1 : extractableAmount / 2;
        return List.of(
            MouseClientTooltipComponent.itemWithDecorations(
                MouseClientTooltipComponent.Type.LEFT,
                itemStack,
                extractableAmount == 1 ? null : AmountFormatting.format(extractableAmount)
            ),
            MouseClientTooltipComponent.itemWithDecorations(
                MouseClientTooltipComponent.Type.RIGHT,
                itemStack,
                halfExtractionAmount == 1 ? null : AmountFormatting.format(halfExtractionAmount)
            )
        );
    }

    @Override
    public void onExtract(final GridExtractMode extractMode,
                          final boolean cursor,
                          final GridExtractionStrategy extractionStrategy) {
        extractionStrategy.onExtract(resource, extractMode, cursor);
    }

    @Override
    public void onScroll(final GridScrollMode scrollMode, final GridScrollingStrategy scrollingStrategy) {
        scrollingStrategy.onScroll(resource, scrollMode, -1);
    }

    @Override
    public void render(final GuiGraphics graphics, final int x, final int y) {
        final Font font = Minecraft.getInstance().font;
        graphics.renderItem(itemStack, x, y);
        graphics.renderItemDecorations(font, itemStack, x, y, null);
    }

    @Override
    public String getDisplayedAmount(final GridView view) {
        return AmountFormatting.formatWithUnits(getAmount(view));
    }

    @Override
    public String getAmountInTooltip(final GridView view) {
        return AmountFormatting.format(getAmount(view));
    }

    @Override
    public boolean belongsToResourceType(final ResourceType resourceType) {
        return resourceType == ResourceTypes.ITEM;
    }

    @Override
    public List<Component> getTooltip() {
        final Minecraft minecraft = Minecraft.getInstance();
        return Screen.getTooltipFromItem(minecraft, itemStack);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage() {
        return itemStack.getTooltipImage();
    }
}
