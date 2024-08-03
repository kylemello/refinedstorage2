package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.grid.screen.AbstractGridScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;
import com.refinedmods.refinedstorage.common.support.widget.CustomCheckboxWidget;
import com.refinedmods.refinedstorage.common.support.widget.HoveredImageButton;
import com.refinedmods.refinedstorage.common.support.widget.ScrollbarWidget;

import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
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

    private final Map<PatternType, PatternTypeButton> patternTypeButtons = new EnumMap<>(PatternType.class);

    public PatternGridScreen(final PatternGridContainerMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title, 177);
        this.inventoryLabelY = 153;
        this.imageWidth = 193;
        this.imageHeight = 249;
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
        updateMaxProcessingScrollbarOffset();
        addWidget(processingScrollbar);
        menu.setListener(this);
    }

    private ImageButton createCreatePatternButton(final int x, final int y) {
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

    private ImageButton createClearButton(final PatternType patternType) {
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
        button.visible = isClearButtonVisible();
        button.active = getMenu().canCreatePattern();
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
        scrollbar.setListener(offset -> onScrollbarChanged((int) offset));
        return scrollbar;
    }

    private void onScrollbarChanged(final int offset) {
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

    @Override
    protected void containerTick() {
        super.containerTick();
        if (createPatternButton != null) {
            createPatternButton.active = getMenu().canCreatePattern();
        }
        updateMaxProcessingScrollbarOffset();
    }

    private void updateMaxProcessingScrollbarOffset() {
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

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (processingScrollbar != null) {
            processingScrollbar.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void renderBg(final GuiGraphics graphics, final float delta, final int mouseX, final int mouseY) {
        super.renderBg(graphics, delta, mouseX, mouseY);
        final int insetContentX = getInsetContentX();
        final int insetContentY = getInsetContentY();
        switch (getMenu().getPatternType()) {
            case CRAFTING -> graphics.blitSprite(CRAFTING, insetContentX, insetContentY, 130, 54);
            case PROCESSING -> renderProcessingBackground(graphics, mouseX, mouseY, insetContentX, insetContentY);
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
        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    @Override
    public void mouseMoved(final double mx, final double my) {
        if (processingScrollbar != null) {
            processingScrollbar.mouseMoved(mx, my);
        }
        super.mouseMoved(mx, my);
    }

    @Override
    public boolean mouseReleased(final double mx, final double my, final int button) {
        return (processingScrollbar != null && processingScrollbar.mouseReleased(mx, my, button))
            || super.mouseReleased(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(final double x, final double y, final double z, final double delta) {
        final boolean isOverProcessingArea = x >= getInsetX()
            && (x < getInsetX() + INSET_WIDTH)
            && y > getInsetY()
            && (y < getInsetY() + INSET_HEIGHT);
        final boolean didScrollbar = processingScrollbar != null
            && processingScrollbar.isActive()
            && isOverProcessingArea
            && processingScrollbar.mouseScrolled(x, y, z, delta);
        return didScrollbar || super.mouseScrolled(x, y, z, delta);
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
            clearButton.visible = isClearButtonVisible();
            if (clearButton.visible) {
                clearButton.setX(getClearButtonX(value));
                requireNonNull(clearButton).setY(getClearButtonY(value));
            }
        }
        if (processingScrollbar != null) {
            processingScrollbar.visible = isProcessingScrollbarVisible();
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
        return patternType == PatternType.CRAFTING ? leftPos + 69 : leftPos + 124;
    }

    private int getClearButtonY(final PatternType patternType) {
        if (patternType == PatternType.PROCESSING) {
            return getInsetContentY() + PROCESSING_INSET_Y_PADDING;
        }
        return getInsetContentY();
    }

    private boolean isClearButtonVisible() {
        return getMenu().getPatternType() == PatternType.CRAFTING
            || getMenu().getPatternType() == PatternType.PROCESSING;
    }

    private boolean isFuzzyModeCheckboxVisible() {
        return getMenu().getPatternType() == PatternType.CRAFTING;
    }

    private boolean isProcessingScrollbarVisible() {
        return getMenu().getPatternType() == PatternType.PROCESSING;
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
}
