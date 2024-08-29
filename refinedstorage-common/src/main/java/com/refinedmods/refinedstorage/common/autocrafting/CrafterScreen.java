package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.AbstractFilterScreen;
import com.refinedmods.refinedstorage.common.support.tooltip.HelpClientTooltipComponent;
import com.refinedmods.refinedstorage.common.support.widget.History;
import com.refinedmods.refinedstorage.common.support.widget.PrioritySideButtonWidget;
import com.refinedmods.refinedstorage.common.support.widget.SearchFieldWidget;
import com.refinedmods.refinedstorage.common.support.widget.TextMarquee;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslationAsHeading;
import static java.util.Objects.requireNonNull;

public class CrafterScreen extends AbstractBaseScreen<CrafterContainerMenu> {
    private static final Component EMPTY_PATTERN_SLOT = createTranslationAsHeading(
        "gui", "crafter.empty_pattern_slot"
    );
    private static final Component CLICK_TO_EDIT_NAME = createTranslation("gui", "crafter.click_to_edit_name");

    private static final Component CHAINED = createTranslation("gui", "crafter.chained");
    private static final Component CHAINED_HELP = createTranslation("gui", "crafter.chained.help");
    private static final Component CHAINED_HEAD_HELP = createTranslation("gui", "crafter.chained.head_help");
    private static final Component NOT_CHAINED = createTranslation("gui", "crafter.not_chained");
    private static final Component NOT_CHAINED_HELP = createTranslation("gui", "crafter.not_chained.help");

    private static final ResourceLocation CRAFTER_NAME_BACKGROUND = createIdentifier("widget/crafter_name");
    private static final List<String> CRAFTER_NAME_HISTORY = new ArrayList<>();

    private final Inventory playerInventory;

    @Nullable
    private EditBox nameField;
    private boolean editName;

    public CrafterScreen(final CrafterContainerMenu menu, final Inventory playerInventory, final Component title) {
        super(menu, playerInventory, new TextMarquee(title, getTitleMaxWidth(menu)));
        this.inventoryLabelY = 42;
        this.imageWidth = 210;
        this.imageHeight = 137;
        this.playerInventory = playerInventory;
    }

    private static int getTitleMaxWidth(final CrafterContainerMenu menu) {
        final Component title = getChainingTitle(menu);
        return TITLE_MAX_WIDTH - Minecraft.getInstance().font.width(title) - 10;
    }

    private static Component getChainingTitle(final CrafterContainerMenu menu) {
        return (menu.isPartOfChain() || menu.isHeadOfChain()) ? CHAINED : NOT_CHAINED;
    }

    private Component getChainingTooltip() {
        if (!getMenu().isPartOfChain() && !getMenu().isHeadOfChain()) {
            return NOT_CHAINED_HELP;
        }
        return getMenu().isHeadOfChain() ? CHAINED_HEAD_HELP : CHAINED_HELP;
    }

    @Override
    protected void renderBg(final GuiGraphics graphics, final float delta, final int mouseX, final int mouseY) {
        super.renderBg(graphics, delta, mouseX, mouseY);
        if (editName) {
            graphics.blitSprite(CRAFTER_NAME_BACKGROUND, leftPos + 7, topPos + 5, 162, 12);
        }
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int clickedButton) {
        if (!editName && getMenu().canChangeName() && isHovering(
            titleLabelX, titleLabelY, titleMarquee.getEffectiveWidth(font), font.lineHeight, mouseX, mouseY
        )) {
            setEditName(true);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    @Override
    protected void init() {
        super.init();
        getMenu().setListener(name -> {
            titleMarquee.setText(name);
            if (nameField != null) {
                nameField.setValue(name.getString());
            }
        });
        addSideButton(new LockModeSideButtonWidget(getMenu().getProperty(CrafterPropertyTypes.LOCK_MODE)));
        addSideButton(PrioritySideButtonWidget.forCrafter(
            getMenu().getProperty(CrafterPropertyTypes.PRIORITY),
            playerInventory,
            this
        ));
        nameField = new SearchFieldWidget(
            font,
            leftPos + 8 + 1,
            topPos + 6 + 1,
            159 - 6,
            new History(CRAFTER_NAME_HISTORY)
        );
        nameField.setValue(title.getString());
        nameField.setBordered(false);
        nameField.setCanLoseFocus(false);
        addWidget(nameField);
        setEditName(false);
    }

    private void setEditName(final boolean editName) {
        this.editName = editName;
        this.titleMarquee.setTooltip(getMenu().canChangeName() ? CLICK_TO_EDIT_NAME : null);
        if (nameField != null) {
            nameField.visible = editName;
            nameField.setFocused(editName);
            nameField.setCanLoseFocus(!editName);
            if (editName) {
                setFocused(nameField);
            } else {
                setFocused(null);
            }
        }
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (nameField != null && editName) {
            nameField.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void renderLabels(final GuiGraphics graphics, final int mouseX, final int mouseY) {
        if (editName) {
            renderPlayerInventoryTitle(graphics);
            return;
        }
        super.renderLabels(graphics, mouseX, mouseY);
        if (getMenu().canChangeName()) {
            titleMarquee.renderTooltipHighlight(
                graphics,
                titleLabelX,
                titleLabelY,
                font,
                isHoveringOverTitle(mouseX, mouseY)
            );
        }
        final Component title = getChainingTitle(menu);
        graphics.drawString(font, title, getChainingTitleX(title), titleLabelY, 4210752, false);
    }

    private int getChainingTitleX(final Component title) {
        return imageWidth - 41 - font.width(title);
    }

    @Override
    public boolean charTyped(final char unknown1, final int unknown2) {
        return (nameField != null && editName && nameField.charTyped(unknown1, unknown2))
            || super.charTyped(unknown1, unknown2);
    }

    @Override
    public boolean keyPressed(final int key, final int scanCode, final int modifiers) {
        if (nameField != null && editName) {
            if (nameField.isFocused() && saveOrCancel(key)) {
                return true;
            }
            return nameField.keyPressed(key, scanCode, modifiers);
        }
        return super.keyPressed(key, scanCode, modifiers);
    }

    private boolean saveOrCancel(final int key) {
        if ((key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER)) {
            getMenu().changeName(requireNonNull(nameField).getValue());
            setEditName(false);
            return true;
        } else if (key == GLFW.GLFW_KEY_ESCAPE) {
            setEditName(false);
            requireNonNull(nameField).setValue(titleMarquee.getText().getString());
            return true;
        }
        return false;
    }

    @Override
    protected void renderTooltip(final GuiGraphics graphics, final int x, final int y) {
        if (hoveredSlot instanceof PatternSlot patternSlot
            && !patternSlot.hasItem()
            && getMenu().getCarried().isEmpty()) {
            graphics.renderTooltip(font, EMPTY_PATTERN_SLOT, x, y);
            return;
        }
        final Component chainingTitle = getChainingTitle(getMenu());
        final int chainingTitleX = getChainingTitleX(chainingTitle);
        if (isHovering(chainingTitleX, titleLabelY, font.width(chainingTitle), font.lineHeight, x, y)) {
            final Component chainingTooltip = getChainingTooltip();
            Platform.INSTANCE.renderTooltip(
                graphics,
                List.of(HelpClientTooltipComponent.createAlwaysDisplayed(chainingTooltip)),
                x,
                y
            );
        }
        super.renderTooltip(graphics, x, y);
    }

    @Override
    protected ResourceLocation getTexture() {
        return AbstractFilterScreen.TEXTURE;
    }
}
