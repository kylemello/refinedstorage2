package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.grid.screen.AbstractGridScreen;
import com.refinedmods.refinedstorage.common.support.widget.CustomCheckboxWidget;
import com.refinedmods.refinedstorage.common.support.widget.HoveredImageButton;

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

public class PatternGridScreen extends AbstractGridScreen<PatternGridContainerMenu> {
    private static final ResourceLocation TEXTURE = createIdentifier("textures/gui/pattern_grid.png");
    private static final MutableComponent CREATE_PATTERN = createTranslation("gui", "pattern_grid.create_pattern");
    private static final MutableComponent CLEAR = createTranslation("gui", "pattern_grid.clear");
    private static final MutableComponent FUZZY_MODE = createTranslation("gui", "pattern_grid.fuzzy_mode");
    private static final MutableComponent FUZZY_MODE_ON_HELP =
        createTranslation("gui", "pattern_grid.fuzzy_mode.on.help");
    private static final MutableComponent FUZZY_MODE_OFF_HELP =
        createTranslation("gui", "pattern_grid.fuzzy_mode.off.help");
    private static final int CREATE_PATTERN_BUTTON_SIZE = 16;

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

    @Nullable
    private Button createPatternButton;

    private final Map<PatternType, PatternTypeButton> patternTypeButtons = new EnumMap<>(PatternType.class);

    public PatternGridScreen(final PatternGridContainerMenu menu, final Inventory inventory, final Component title) {
        super(menu, inventory, title, 177);
        this.inventoryLabelY = 153;
        this.imageWidth = 193;
        this.imageHeight = 250;
    }

    @Override
    protected void init() {
        super.init();
        createPatternButton = createCreatePatternButton(leftPos + 152, topPos + imageHeight - bottomHeight + 32);
        addRenderableWidget(createPatternButton);
        final ImageButton clearButton = createClearButton(leftPos + 68, topPos + imageHeight - bottomHeight + 8);
        addRenderableWidget(clearButton);
        addPatternTypeButtons(getMenu().getPatternType());
        final CustomCheckboxWidget fuzzyMode = createFuzzyModeCheckbox();
        addRenderableWidget(fuzzyMode);
        menu.setListener(new PatternGridContainerMenu.PatternGridListener() {
            @Override
            public void patternTypeChanged(final PatternType value) {
                patternTypeButtons.values().forEach(button -> button.setSelected(false));
                patternTypeButtons.get(value).setSelected(true);
                fuzzyMode.visible = value == PatternType.CRAFTING;
                clearButton.visible = value == PatternType.CRAFTING;
            }

            @Override
            public void fuzzyModeChanged(final boolean value) {
                fuzzyMode.setSelected(value);
                fuzzyMode.setTooltip(getFuzzyModeTooltip(value));
            }
        });
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

    @Override
    protected void containerTick() {
        super.containerTick();
        if (createPatternButton != null) {
            createPatternButton.active = getMenu().canCreatePattern();
        }
    }

    private CustomCheckboxWidget createFuzzyModeCheckbox() {
        final CustomCheckboxWidget fuzzyMode = new CustomCheckboxWidget(
            leftPos + 11,
            topPos + imageHeight - bottomHeight + 65,
            FUZZY_MODE,
            font,
            getMenu().isFuzzyMode(),
            CustomCheckboxWidget.Size.SMALL
        );
        fuzzyMode.setOnPressed((checkbox, selected) -> getMenu().setFuzzyMode(selected));
        fuzzyMode.setTooltip(getFuzzyModeTooltip(getMenu().isFuzzyMode()));
        fuzzyMode.visible = getMenu().getPatternType() == PatternType.CRAFTING;
        return fuzzyMode;
    }

    private static Tooltip getFuzzyModeTooltip(final boolean fuzzyMode) {
        return fuzzyMode ? Tooltip.create(FUZZY_MODE_ON_HELP) : Tooltip.create(FUZZY_MODE_OFF_HELP);
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

    private ImageButton createClearButton(final int x, final int y) {
        final HoveredImageButton button = new HoveredImageButton(
            x,
            y,
            CLEAR_BUTTON_SIZE,
            CLEAR_BUTTON_SIZE,
            CLEAR_BUTTON_SPRITES,
            b -> getMenu().sendClear(),
            CLEAR
        );
        button.setTooltip(Tooltip.create(CLEAR));
        button.visible = getMenu().getPatternType() == PatternType.CRAFTING;
        return button;
    }

    @Override
    protected void renderBg(final GuiGraphics graphics, final float delta, final int mouseX, final int mouseY) {
        super.renderBg(graphics, delta, mouseX, mouseY);
        switch (getMenu().getPatternType()) {
            case CRAFTING ->
                graphics.blitSprite(CRAFTING, leftPos + 7 + 4, topPos + imageHeight - bottomHeight + 4 + 4, 130, 54);
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
