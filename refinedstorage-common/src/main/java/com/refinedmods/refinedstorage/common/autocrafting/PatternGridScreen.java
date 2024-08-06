package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.grid.screen.AbstractGridScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;
import com.refinedmods.refinedstorage.common.support.tooltip.SmallTextClientTooltipComponent;
import com.refinedmods.refinedstorage.common.support.widget.CustomCheckboxWidget;
import com.refinedmods.refinedstorage.common.support.widget.HoveredImageButton;
import com.refinedmods.refinedstorage.common.support.widget.ScrollbarWidget;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SmithingTemplateItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.StonecutterRecipe;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslationAsHeading;
import static java.util.Objects.requireNonNull;

public class PatternGridScreen extends AbstractGridScreen<PatternGridContainerMenu> implements
    PatternGridContainerMenu.PatternGridListener {
    private static final ResourceLocation TEXTURE = createIdentifier("textures/gui/pattern_grid.png");
    private static final MutableComponent CREATE_PATTERN = createTranslation("gui", "pattern_grid.create_pattern");
    private static final MutableComponent CLEAR = createTranslation("gui", "pattern_grid.clear");
    private static final MutableComponent FUZZY_MODE = createTranslation("gui", "pattern_grid.fuzzy_mode");
    private static final MutableComponent FUZZY_MODE_ON_HELP =
        createTranslation("gui", "pattern_grid.fuzzy_mode.on.help");
    private static final MutableComponent FUZZY_MODE_OFF_HELP =
        createTranslation("gui", "pattern_grid.fuzzy_mode.off.help");
    private static final MutableComponent INPUTS = createTranslation("gui", "pattern_grid.processing.inputs");
    private static final MutableComponent OUTPUTS = createTranslation("gui", "pattern_grid.processing.outputs");
    private static final int CREATE_PATTERN_BUTTON_SIZE = 16;
    private static final SmallTextClientTooltipComponent CLICK_TO_CONFIGURE_AMOUNT_AND_ALTERNATIVES =
        new SmallTextClientTooltipComponent(
            createTranslationAsHeading("gui", "pattern_grid.processing.click_to_configure_amount_and_alternatives")
        );

    private static final int INSET_PADDING = 4;
    private static final int PROCESSING_INSET_Y_PADDING = 9;
    private static final int INSET_WIDTH = 138;
    private static final int INSET_HEIGHT = 71;

    private static final WidgetSprites CREATE_PATTERN_BUTTON_SPRITES = new WidgetSprites(
        createIdentifier("widget/create_pattern"),
        createIdentifier("widget/create_pattern_disabled"),
        createIdentifier("widget/create_pattern_focused"),
        createIdentifier("widget/create_pattern_disabled")
    );
    private static final WidgetSprites CLEAR_BUTTON_SPRITES = new WidgetSprites(
        createIdentifier("widget/clear"),
        createIdentifier("widget/clear_disabled"),
        createIdentifier("widget/clear_focused"),
        createIdentifier("widget/clear_disabled")
    );
    private static final ResourceLocation CRAFTING = createIdentifier("pattern_grid/crafting");
    private static final ResourceLocation PROCESSING = createIdentifier("pattern_grid/processing");
    private static final ResourceLocation PROCESSING_MATRIX = createIdentifier("pattern_grid/processing_matrix");
    private static final ResourceLocation STONECUTTER = createIdentifier("pattern_grid/stonecutter");
    private static final ResourceLocation SMITHING_TABLE = createIdentifier("pattern_grid/smithing_table");

    private static final int INDIVIDUAL_PROCESSING_MATRIX_SIZE = 54;
    private static final int PROCESSING_MATRIX_SLOT_SIZE = 18;

    @Nullable
    private Button createPatternButton;
    @Nullable
    private CustomCheckboxWidget fuzzyModeCheckbox;
    @Nullable
    private Button clearButton;
    @Nullable
    private ScrollbarWidget processingScrollbar;
    @Nullable
    private ScrollbarWidget stonecutterScrollbar;
    @Nullable
    private ArmorStand smithingTablePreview;
    private ItemStack smithingTableResult = ItemStack.EMPTY;

    private final Map<PatternType, PatternTypeButton> patternTypeButtons = new EnumMap<>(PatternType.class);
    private final Inventory playerInventory;
    private final Map<Pair<PlatformResourceKey, Set<ResourceLocation>>, ProcessingMatrixInputClientTooltipComponent>
        processingMatrixInputTooltipCache = new HashMap<>();

    private final CyclingSlotBackground smithingTemplateIcon;
    private final CyclingSlotBackground smithingBaseIcon;
    private final CyclingSlotBackground smithingAdditionalIcon;

    public PatternGridScreen(final PatternGridContainerMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title, 177);
        this.inventoryLabelY = 153;
        this.imageWidth = 193;
        this.imageHeight = 249;
        this.playerInventory = inventory;
        this.smithingTemplateIcon = new CyclingSlotBackground(menu.getFirstSmithingTableSlotIndex());
        this.smithingBaseIcon = new CyclingSlotBackground(menu.getFirstSmithingTableSlotIndex() + 1);
        this.smithingAdditionalIcon = new CyclingSlotBackground(menu.getFirstSmithingTableSlotIndex() + 2);
    }

    @Override
    protected void init() {
        super.init();
        this.createPatternButton = createCreatePatternButton(leftPos + 152, topPos + imageHeight - bottomHeight + 32);
        addRenderableWidget(createPatternButton);
        addPatternTypeButtons(getMenu().getPatternType());
        this.clearButton = createClearButton(getMenu().getPatternType());
        addRenderableWidget(clearButton);
        this.fuzzyModeCheckbox = createFuzzyModeCheckbox();
        addRenderableWidget(fuzzyModeCheckbox);
        this.processingScrollbar = createProcessingScrollbar();
        updateProcessingScrollbarMaxOffset();
        addWidget(processingScrollbar);
        this.stonecutterScrollbar = createStonecutterScrollbar();
        updateStonecutterScrollbarMaxOffset();
        addWidget(stonecutterScrollbar);
        menu.setListener(this);
        if (minecraft != null && minecraft.level != null) {
            smithingTablePreview = new ArmorStand(minecraft.level, 0.0, 0.0, 0.0);
            smithingTablePreview.setNoBasePlate(true);
            smithingTablePreview.setShowArms(true);
            smithingTablePreview.yBodyRot = 210.0F;
            smithingTablePreview.setXRot(25.0F);
            smithingTablePreview.yHeadRot = smithingTablePreview.getYRot();
            smithingTablePreview.yHeadRotO = smithingTablePreview.getYRot();
            smithingTableResult = getMenu().getSmithingTableResult().copy();
            updateArmorStandPreview(smithingTableResult);
        }
    }

    private HoveredImageButton createCreatePatternButton(final int x, final int y) {
        final HoveredImageButton button = new HoveredImageButton(
            x,
            y,
            CREATE_PATTERN_BUTTON_SIZE,
            CREATE_PATTERN_BUTTON_SIZE,
            CREATE_PATTERN_BUTTON_SPRITES,
            b -> getMenu().sendCreatePattern(),
            CREATE_PATTERN
        );
        button.setTooltip(Tooltip.create(CREATE_PATTERN));
        button.active = getMenu().canCreatePattern();
        return button;
    }

    private void addPatternTypeButtons(final PatternType currentPatternType) {
        final PatternType[] patternTypes = PatternType.values();
        for (int i = 0; i < patternTypes.length; ++i) {
            final PatternType patternType = patternTypes[i];
            final PatternTypeButton button = new PatternTypeButton(
                leftPos + 172,
                topPos + imageHeight - bottomHeight + 4 + (i * (16 + 3)),
                btn -> getMenu().setPatternType(patternType),
                patternType,
                patternType == currentPatternType
            );
            patternTypeButtons.put(patternType, button);
            addRenderableWidget(button);
        }
    }

    private HoveredImageButton createClearButton(final PatternType patternType) {
        final HoveredImageButton button = new HoveredImageButton(
            getClearButtonX(patternType),
            getClearButtonY(patternType),
            CLEAR_BUTTON_SIZE,
            CLEAR_BUTTON_SIZE,
            CLEAR_BUTTON_SPRITES,
            b -> getMenu().sendClear(),
            CLEAR
        );
        button.setTooltip(Tooltip.create(CLEAR));
        return button;
    }

    private CustomCheckboxWidget createFuzzyModeCheckbox() {
        final CustomCheckboxWidget checkbox = new CustomCheckboxWidget(
            getInsetContentX(),
            topPos + imageHeight - bottomHeight + 65,
            FUZZY_MODE,
            font,
            getMenu().isFuzzyMode(),
            CustomCheckboxWidget.Size.SMALL
        );
        checkbox.setOnPressed((c, selected) -> getMenu().setFuzzyMode(selected));
        checkbox.setTooltip(getFuzzyModeTooltip(getMenu().isFuzzyMode()));
        checkbox.visible = isFuzzyModeCheckboxVisible();
        return checkbox;
    }

    private static Tooltip getFuzzyModeTooltip(final boolean fuzzyMode) {
        return fuzzyMode ? Tooltip.create(FUZZY_MODE_ON_HELP) : Tooltip.create(FUZZY_MODE_OFF_HELP);
    }

    private ScrollbarWidget createProcessingScrollbar() {
        final ScrollbarWidget scrollbar = new ScrollbarWidget(
            getInsetX() + 126,
            getInsetY() + 14,
            ScrollbarWidget.Type.SMALL,
            52
        );
        scrollbar.visible = isProcessingScrollbarVisible();
        scrollbar.setListener(offset -> onProcessingScrollbarChanged((int) offset));
        return scrollbar;
    }

    private void onProcessingScrollbarChanged(final int offset) {
        int inputRow = 0;
        int outputRow = 0;
        final int scrollbarOffset = (processingScrollbar != null && processingScrollbar.isSmoothScrolling())
            ? offset
            : offset * PROCESSING_MATRIX_SLOT_SIZE;
        for (int i = 0; i < getMenu().getResourceSlots().size(); ++i) {
            final ResourceSlot slot = getMenu().getResourceSlots().get(i);
            if (!(slot instanceof ProcessingMatrixResourceSlot matrixSlot)) {
                continue;
            }
            final int row = matrixSlot.isInput() ? inputRow : outputRow;
            final int slotY = getInsetContentY()
                + PROCESSING_INSET_Y_PADDING + 1
                + (row * PROCESSING_MATRIX_SLOT_SIZE)
                - scrollbarOffset
                - topPos;
            Platform.INSTANCE.setSlotY(getMenu().getResourceSlots().get(i), slotY);
            if ((i + 1) % 3 == 0) {
                if (matrixSlot.isInput()) {
                    inputRow++;
                } else {
                    outputRow++;
                }
            }
        }
    }

    private ScrollbarWidget createStonecutterScrollbar() {
        final ScrollbarWidget scrollbar = new ScrollbarWidget(
            getInsetX() + 107,
            getInsetY() + 9,
            ScrollbarWidget.Type.NORMAL,
            54
        );
        scrollbar.visible = isStonecutterScrollbarVisible();
        return scrollbar;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (createPatternButton != null) {
            createPatternButton.active = getMenu().canCreatePattern();
        }
        updateProcessingScrollbarMaxOffset();
        updateStonecutterScrollbarMaxOffset();
        updateSmithingTableState();
    }

    private void updateProcessingScrollbarMaxOffset() {
        if (processingScrollbar == null || getMenu().getPatternType() != PatternType.PROCESSING) {
            return;
        }

        int filledInputSlots = 0;
        int filledOutputSlots = 0;
        int lastFilledInputSlot = 0;
        int lastFilledOutputSlot = 0;

        for (int i = 0; i < getMenu().getResourceSlots().size(); ++i) {
            final ResourceSlot resourceSlot = getMenu().getResourceSlots().get(i);
            if (resourceSlot.isEmpty() || !(resourceSlot instanceof ProcessingMatrixResourceSlot matrixSlot)) {
                continue;
            }
            if (matrixSlot.isInput()) {
                filledInputSlots++;
                lastFilledInputSlot = i;
            } else {
                filledOutputSlots++;
                lastFilledOutputSlot = i - 81;
            }
        }

        final int maxFilledSlots = Math.max(filledInputSlots, filledOutputSlots);
        final int maxFilledRows = Math.floorDiv(maxFilledSlots - 1, 3);

        final int maxLastFilledSlot = Math.max(lastFilledInputSlot, lastFilledOutputSlot);
        final int maxLastFilledRow = Math.floorDiv(maxLastFilledSlot, 3) - 2;

        final int maxOffset = Math.max(maxFilledRows, maxLastFilledRow);
        final int maxOffsetCorrected = processingScrollbar.isSmoothScrolling()
            ? maxOffset * PROCESSING_MATRIX_SLOT_SIZE
            : maxOffset;

        processingScrollbar.setMaxOffset(maxOffsetCorrected);
        processingScrollbar.setEnabled(maxOffsetCorrected > 0);
    }

    private void updateStonecutterScrollbarMaxOffset() {
        if (stonecutterScrollbar == null || getMenu().getPatternType() != PatternType.STONECUTTER) {
            return;
        }
        final int items = getMenu().getStonecutterRecipes().size();
        final int rows = Math.ceilDiv(items, VanillaConstants.STONECUTTER_RECIPES_PER_ROW);
        final int maxOffset = rows - VanillaConstants.STONECUTTER_ROWS_VISIBLE;
        final int maxOffsetCorrected = maxOffset * (stonecutterScrollbar.isSmoothScrolling() ? 18 : 1);
        stonecutterScrollbar.setMaxOffset(maxOffsetCorrected);
        stonecutterScrollbar.setEnabled(maxOffsetCorrected > 0);
    }

    private void updateSmithingTableState() {
        if (getMenu().getPatternType() != PatternType.SMITHING_TABLE) {
            return;
        }
        final ItemStack result = getMenu().getSmithingTableResult();
        if (!ItemStack.isSameItemSameComponents(result, smithingTableResult)) {
            smithingTableResult = result.copy();
            updateArmorStandPreview(result);
        }
        final Optional<SmithingTemplateItem> templateItem = getMenu().getSmithingTableTemplateItem();
        smithingTemplateIcon.tick(VanillaConstants.EMPTY_SLOT_SMITHING_TEMPLATES);
        smithingBaseIcon.tick(templateItem.map(SmithingTemplateItem::getBaseSlotEmptyIcons).orElse(List.of()));
        smithingAdditionalIcon.tick(
            templateItem.map(SmithingTemplateItem::getAdditionalSlotEmptyIcons).orElse(List.of())
        );
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (processingScrollbar != null) {
            processingScrollbar.render(graphics, mouseX, mouseY, partialTicks);
        }
        if (stonecutterScrollbar != null) {
            stonecutterScrollbar.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void renderBg(final GuiGraphics graphics, final float partialTicks, final int mouseX, final int mouseY) {
        super.renderBg(graphics, partialTicks, mouseX, mouseY);
        final int insetContentX = getInsetContentX();
        final int insetContentY = getInsetContentY();
        switch (getMenu().getPatternType()) {
            case CRAFTING -> graphics.blitSprite(CRAFTING, insetContentX, insetContentY, 130, 54);
            case PROCESSING -> renderProcessingBackground(graphics, mouseX, mouseY, insetContentX, insetContentY);
            case STONECUTTER -> renderStonecutterBackground(graphics, mouseX, mouseY, getInsetX(), getInsetY());
            case SMITHING_TABLE -> renderSmithingTableBackground(graphics, partialTicks, insetContentX, insetContentY);
        }
    }

    private void renderProcessingBackground(final GuiGraphics graphics,
                                            final int mouseX,
                                            final int mouseY,
                                            final int insetContentX,
                                            final int insetContentY) {
        graphics.blitSprite(PROCESSING, insetContentX, insetContentY + PROCESSING_INSET_Y_PADDING, 130, 54);
        renderProcessingMatrix(
            graphics,
            getInsetContentX() + 1,
            getInsetContentX() + 1 + 52 + 1, // include the edge so we get the item counts properly
            mouseX,
            mouseY,
            true
        );
        renderProcessingMatrix(
            graphics,
            getInsetContentX() + INDIVIDUAL_PROCESSING_MATRIX_SIZE + 2 + 1,
            getInsetContentX() + INDIVIDUAL_PROCESSING_MATRIX_SIZE + 2 + 1 + 52 + 1,
            mouseX,
            mouseY,
            false
        );
    }

    private void renderProcessingMatrix(final GuiGraphics graphics,
                                        final int startX,
                                        final int endX,
                                        final int mouseX,
                                        final int mouseY,
                                        final boolean input) {
        final int startY = getInsetY() + 14;
        // include the edge so we get the item counts properly
        final int endY = getInsetY() + 14 + 52 + 1;
        graphics.enableScissor(startX, startY, endX, endY);
        renderProcessingMatrixSlotBackground(graphics, input);
        renderProcessingMatrixSlots(graphics, mouseX, mouseY, input);
        graphics.disableScissor();
    }

    private void renderProcessingMatrixSlotBackground(final GuiGraphics graphics, final boolean input) {
        final int x = getInsetContentX() + (!input ? INDIVIDUAL_PROCESSING_MATRIX_SIZE + 2 : 0);
        final int startY = getInsetY() + PROCESSING_INSET_Y_PADDING - INDIVIDUAL_PROCESSING_MATRIX_SIZE;
        final int endY = getInsetY() + PROCESSING_INSET_Y_PADDING + INDIVIDUAL_PROCESSING_MATRIX_SIZE;
        final int scrollbarOffset = processingScrollbar != null ? (int) processingScrollbar.getOffset() : 0;
        final int scrollbarOffsetCorrected = processingScrollbar != null && processingScrollbar.isSmoothScrolling()
            ? scrollbarOffset
            : scrollbarOffset * PROCESSING_MATRIX_SLOT_SIZE;
        for (int i = 0; i < 9; ++i) {
            final int y = (getInsetY() + 13)
                + (i * INDIVIDUAL_PROCESSING_MATRIX_SIZE)
                - scrollbarOffsetCorrected;
            if (y < startY || y > endY) {
                continue;
            }
            graphics.blitSprite(
                PROCESSING_MATRIX,
                x,
                y,
                INDIVIDUAL_PROCESSING_MATRIX_SIZE,
                INDIVIDUAL_PROCESSING_MATRIX_SIZE
            );
        }
    }

    private void renderProcessingMatrixSlots(final GuiGraphics graphics,
                                             final int mouseX,
                                             final int mouseY,
                                             final boolean input) {
        for (final ResourceSlot resourceSlot : getMenu().getResourceSlots()) {
            if (resourceSlot.isActive()
                && resourceSlot instanceof ProcessingMatrixResourceSlot matrixSlot
                && matrixSlot.isInput() == input) {
                tryRenderResourceSlot(graphics, resourceSlot);
                if (isHovering(resourceSlot.x, resourceSlot.y, 16, 16, mouseX, mouseY)
                    && canInteractWithResourceSlot(resourceSlot, mouseX, mouseY)) {
                    renderSlotHighlight(graphics, leftPos + resourceSlot.x, topPos + resourceSlot.y, 0);
                }
            }
        }
    }

    private void renderStonecutterBackground(final GuiGraphics graphics,
                                             final int mouseX,
                                             final int mouseY,
                                             final int insetX,
                                             final int insetY) {
        graphics.blitSprite(STONECUTTER, insetX + 8, insetY + 8, 112, 56);
        graphics.enableScissor(insetX + 40, insetY + 9, insetX + 40 + 64, insetY + 9 + 54);
        final boolean isOverArea = isOverStonecutterArea(mouseX, mouseY);
        for (int i = 0; i < getMenu().getStonecutterRecipes().size(); ++i) {
            final RecipeHolder<StonecutterRecipe> recipe = getMenu().getStonecutterRecipes().get(i);
            final int xx = getStonecutterRecipeX(insetX, i);
            final int row = i / VanillaConstants.STONECUTTER_RECIPES_PER_ROW;
            final int yy = getStonecutterRecipeY(insetY, row);
            if (yy < insetY + 9 - 18 || yy > insetY + 9 + 54) {
                continue;
            }
            final boolean hovering = mouseX >= xx && mouseY >= yy && mouseX < xx + 16 && mouseY < yy + 18;
            final ResourceLocation buttonSprite;
            if (i == getMenu().getStonecutterSelectedRecipe()) {
                buttonSprite = VanillaConstants.STONECUTTER_RECIPE_SELECTED_SPRITE;
            } else if (isOverArea && hovering) {
                buttonSprite = VanillaConstants.STONECUTTER_RECIPE_HIGHLIGHTED_SPRITE;
            } else {
                buttonSprite = VanillaConstants.STONECUTTER_RECIPE_SPRITE;
            }
            graphics.blitSprite(buttonSprite, xx, yy, 16, 18);
            graphics.renderItem(
                recipe.value().getResultItem(requireNonNull(minecraft).level.registryAccess()),
                xx,
                yy + 1
            );
        }
        graphics.disableScissor();
    }

    private void renderSmithingTableBackground(final GuiGraphics graphics,
                                               final float partialTicks,
                                               final int insetContentX,
                                               final int insetContentY) {
        graphics.blitSprite(SMITHING_TABLE, insetContentX, getInsetY() + 26, 98, 18);
        smithingTemplateIcon.render(menu, graphics, partialTicks, leftPos, topPos);
        smithingBaseIcon.render(menu, graphics, partialTicks, leftPos, topPos);
        smithingAdditionalIcon.render(menu, graphics, partialTicks, leftPos, topPos);
        if (smithingTablePreview != null) {
            InventoryScreen.renderEntityInInventory(
                graphics,
                (float) (leftPos + 133),
                (float) (insetContentY + 54),
                25.0F,
                VanillaConstants.ARMOR_STAND_TRANSLATION,
                VanillaConstants.ARMOR_STAND_ANGLE,
                null,
                smithingTablePreview
            );
        }
    }

    @Override
    protected void renderTooltip(final GuiGraphics graphics, final int x, final int y) {
        super.renderTooltip(graphics, x, y);
        renderStonecutterHoveredRecipeTooltip(graphics, x, y);
        renderSmithingTableHelpTooltips(graphics, x, y);
    }

    private void renderStonecutterHoveredRecipeTooltip(final GuiGraphics graphics, final int x, final int y) {
        if (getMenu().getPatternType() != PatternType.STONECUTTER || !isOverStonecutterArea(x, y)) {
            return;
        }
        final int insetX = getInsetX();
        final int insetY = getInsetY();
        for (int i = 0; i < getMenu().getStonecutterRecipes().size(); ++i) {
            final RecipeHolder<StonecutterRecipe> recipe = getMenu().getStonecutterRecipes().get(i);
            final ItemStack result = recipe.value().getResultItem(requireNonNull(minecraft).level.registryAccess());
            final int xx = getStonecutterRecipeX(insetX, i);
            final int row = i / VanillaConstants.STONECUTTER_RECIPES_PER_ROW;
            final int yy = getStonecutterRecipeY(insetY, row);
            if (yy < insetY + 9 - 18 || yy > insetY + 9 + 54) {
                continue;
            }
            if (x >= xx && y >= yy && x < xx + 16 && y < yy + 18) {
                graphics.renderTooltip(font, result, x, y);
            }
        }
    }

    private void renderSmithingTableHelpTooltips(final GuiGraphics graphics, final int x, final int y) {
        if (getMenu().getPatternType() != PatternType.SMITHING_TABLE || hoveredSlot == null || hoveredSlot.hasItem()) {
            return;
        }
        final int firstSlotIndex = getMenu().getFirstSmithingTableSlotIndex();
        getMenu().getSmithingTableTemplateItem().ifPresentOrElse(template -> {
            if (hoveredSlot.index == firstSlotIndex + 1) {
                graphics.renderTooltip(font, font.split(template.getBaseSlotDescription(), 115), x, y);
            } else if (hoveredSlot.index == firstSlotIndex + 2) {
                graphics.renderTooltip(font, font.split(template.getAdditionSlotDescription(), 115), x, y);
            }
        }, () -> {
            if (hoveredSlot.index == firstSlotIndex) {
                graphics.renderTooltip(font, font.split(VanillaConstants.MISSING_SMITHING_TEMPLATE_TOOLTIP, 115), x, y);
            }
        });
    }

    private int getStonecutterRecipeX(final int insetX, final int i) {
        return insetX + 40 + i % VanillaConstants.STONECUTTER_RECIPES_PER_ROW * 16;
    }

    private int getStonecutterRecipeY(final int insetY, final int row) {
        return insetY
            + 9
            + (row * 18)
            - ((stonecutterScrollbar != null ? (int) stonecutterScrollbar.getOffset() : 0)
            * (stonecutterScrollbar != null && stonecutterScrollbar.isSmoothScrolling() ? 1 : 18));
    }

    @Override
    protected void renderResourceSlots(final GuiGraphics graphics) {
        // no op, we render them in the scissor rendering
    }

    @Override
    protected boolean canInteractWithResourceSlot(final ResourceSlot resourceSlot,
                                                  final double mouseX,
                                                  final double mouseY) {
        // Ensure that we can't interact with the resource slot, when the resource slot is on the
        // edge of the scissoring area and still technically visible and active,
        // but we're no longer hovering over the processing matrix.
        final int insetContentX = getInsetContentX() + (
            resourceSlot instanceof ProcessingMatrixResourceSlot matrixSlot && !matrixSlot.isInput()
                ? INDIVIDUAL_PROCESSING_MATRIX_SIZE + 2
                : 0);
        final int insetContentY = getInsetContentY();
        return mouseX >= insetContentX
            && mouseX < insetContentX + INDIVIDUAL_PROCESSING_MATRIX_SIZE
            && mouseY >= insetContentY + PROCESSING_INSET_Y_PADDING
            && mouseY < insetContentY + PROCESSING_INSET_Y_PADDING + INDIVIDUAL_PROCESSING_MATRIX_SIZE;
    }

    @Override
    protected void addResourceSlotTooltips(final ResourceSlot resourceSlot,
                                           final List<ClientTooltipComponent> tooltip) {
        if (resourceSlot instanceof ProcessingMatrixResourceSlot matrixSlot && matrixSlot.isInput()) {
            final Set<ResourceLocation> allowedAlternatives = getMenu().getAllowedAlternatives(
                matrixSlot.getContainerSlot()
            );
            if (matrixSlot.getResource() != null && !allowedAlternatives.isEmpty()) {
                final Pair<PlatformResourceKey, Set<ResourceLocation>> cacheKey = Pair.of(
                    matrixSlot.getResource(),
                    allowedAlternatives
                );
                final ProcessingMatrixInputClientTooltipComponent cached = processingMatrixInputTooltipCache
                    .computeIfAbsent(cacheKey,
                        k -> new ProcessingMatrixInputClientTooltipComponent(k.getFirst(), k.getSecond()));
                tooltip.add(cached);
            }
            tooltip.add(CLICK_TO_CONFIGURE_AMOUNT_AND_ALTERNATIVES);
        } else {
            super.addResourceSlotTooltips(resourceSlot, tooltip);
        }
    }

    @Override
    protected Screen createResourceAmountScreen(final ResourceSlot slot) {
        if (slot instanceof ProcessingMatrixResourceSlot matrixSlot && matrixSlot.isInput()) {
            return new AlternativesScreen(
                this,
                playerInventory,
                getMenu().getAllowedAlternatives(matrixSlot.getContainerSlot()),
                slot
            );
        }
        return super.createResourceAmountScreen(slot);
    }

    @Override
    protected void renderLabels(final GuiGraphics graphics, final int mouseX, final int mouseY) {
        super.renderLabels(graphics, mouseX, mouseY);
        if (getMenu().getPatternType() == PatternType.PROCESSING) {
            final int x = getInsetContentX() - leftPos;
            final int y = getInsetContentY() - topPos - 1;
            graphics.drawString(font, INPUTS, x, y, 4210752, false);
            graphics.drawString(font, OUTPUTS, x + 56, y, 4210752, false);
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int clickedButton) {
        if (processingScrollbar != null && processingScrollbar.mouseClicked(mouseX, mouseY, clickedButton)) {
            return true;
        }
        if (stonecutterScrollbar != null && stonecutterScrollbar.mouseClicked(mouseX, mouseY, clickedButton)) {
            return true;
        }
        if (clickedStonecutterRecipe(mouseX, mouseY)) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    private boolean clickedStonecutterRecipe(final double x, final double y) {
        if (getMenu().getPatternType() != PatternType.STONECUTTER || !isOverStonecutterArea(x, y)) {
            return false;
        }
        final int insetX = getInsetX();
        final int insetY = getInsetY();
        for (int i = 0; i < getMenu().getStonecutterRecipes().size(); ++i) {
            final int xx = getStonecutterRecipeX(insetX, i);
            final int row = i / VanillaConstants.STONECUTTER_RECIPES_PER_ROW;
            final int yy = getStonecutterRecipeY(insetY, row);
            if (yy < insetY + 9 - 18 || yy > insetY + 9 + 54) {
                continue;
            }
            if (x >= xx && y >= yy && x < xx + 16 && y < yy + 18) {
                getMenu().setStonecutterSelectedRecipe(i);
                Minecraft.getInstance().getSoundManager().play(
                    SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F)
                );
                return true;
            }
        }
        return false;
    }

    @Override
    public void mouseMoved(final double mx, final double my) {
        if (processingScrollbar != null) {
            processingScrollbar.mouseMoved(mx, my);
        }
        if (stonecutterScrollbar != null) {
            stonecutterScrollbar.mouseMoved(mx, my);
        }
        super.mouseMoved(mx, my);
    }

    @Override
    public boolean mouseReleased(final double mx, final double my, final int button) {
        if (processingScrollbar == null || stonecutterScrollbar == null) {
            return super.mouseReleased(mx, my, button);
        }
        return processingScrollbar.mouseReleased(mx, my, button)
            || stonecutterScrollbar.mouseReleased(mx, my, button)
            || super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(final double x, final double y, final double z, final double delta) {
        return tryProcessingScrollbar(x, y, z, delta)
            || tryStonecutterScrollbar(x, y, z, delta)
            || super.mouseScrolled(x, y, z, delta);
    }

    private boolean tryProcessingScrollbar(final double x, final double y, final double z, final double delta) {
        final boolean isOverProcessingArea = x >= getInsetX()
            && (x < getInsetX() + INSET_WIDTH)
            && y > getInsetY()
            && (y < getInsetY() + INSET_HEIGHT);
        return processingScrollbar != null
            && processingScrollbar.isActive()
            && isOverProcessingArea
            && processingScrollbar.mouseScrolled(x, y, z, delta);
    }

    private boolean tryStonecutterScrollbar(final double x, final double y, final double z, final double delta) {
        return stonecutterScrollbar != null
            && stonecutterScrollbar.isActive()
            && isOverStonecutterArea(x, y)
            && stonecutterScrollbar.mouseScrolled(x, y, z, delta);
    }

    private boolean isOverStonecutterArea(final double x, final double y) {
        return x >= getInsetX() + 40
            && (x < getInsetX() + 40 + 81)
            && y > getInsetY() + 8
            && (y < getInsetY() + 8 + 56);
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    public void patternTypeChanged(final PatternType value) {
        patternTypeButtons.values().forEach(button -> button.setSelected(false));
        patternTypeButtons.get(value).setSelected(true);
        if (fuzzyModeCheckbox != null) {
            fuzzyModeCheckbox.visible = isFuzzyModeCheckboxVisible();
        }
        if (clearButton != null) {
            clearButton.setX(getClearButtonX(value));
            requireNonNull(clearButton).setY(getClearButtonY(value));
        }
        if (processingScrollbar != null) {
            processingScrollbar.visible = isProcessingScrollbarVisible();
        }
        if (stonecutterScrollbar != null) {
            stonecutterScrollbar.visible = isStonecutterScrollbarVisible();
        }
    }

    @Override
    public void fuzzyModeChanged(final boolean value) {
        if (fuzzyModeCheckbox == null) {
            return;
        }
        fuzzyModeCheckbox.setSelected(value);
        fuzzyModeCheckbox.setTooltip(getFuzzyModeTooltip(value));
    }

    private int getClearButtonX(final PatternType patternType) {
        return switch (patternType) {
            case CRAFTING -> leftPos + 69;
            case PROCESSING -> leftPos + 124;
            case STONECUTTER -> leftPos + 131;
            case SMITHING_TABLE -> leftPos + 112;
        };
    }

    private int getClearButtonY(final PatternType patternType) {
        return switch (patternType) {
            case PROCESSING -> getInsetContentY() + PROCESSING_INSET_Y_PADDING;
            case STONECUTTER -> getInsetY() + 8;
            case SMITHING_TABLE -> getInsetY() + 26;
            default -> getInsetContentY();
        };
    }

    private boolean isFuzzyModeCheckboxVisible() {
        return getMenu().getPatternType() == PatternType.CRAFTING;
    }

    private boolean isProcessingScrollbarVisible() {
        return getMenu().getPatternType() == PatternType.PROCESSING;
    }

    private boolean isStonecutterScrollbarVisible() {
        return getMenu().getPatternType() == PatternType.STONECUTTER;
    }

    private int getInsetX() {
        return leftPos + 8;
    }

    private int getInsetY() {
        return topPos + imageHeight - bottomHeight + 5;
    }

    private int getInsetContentX() {
        return getInsetX() + INSET_PADDING;
    }

    private int getInsetContentY() {
        return getInsetY() + INSET_PADDING;
    }

    private void updateArmorStandPreview(final ItemStack result) {
        if (smithingTablePreview == null) {
            return;
        }
        for (final EquipmentSlot equipmentslot : EquipmentSlot.values()) {
            smithingTablePreview.setItemSlot(equipmentslot, ItemStack.EMPTY);
        }
        if (result.isEmpty()) {
            return;
        }
        if (result.getItem() instanceof ArmorItem armorItem) {
            smithingTablePreview.setItemSlot(armorItem.getEquipmentSlot(), result);
        } else {
            smithingTablePreview.setItemSlot(EquipmentSlot.OFFHAND, result);
        }
    }
}
