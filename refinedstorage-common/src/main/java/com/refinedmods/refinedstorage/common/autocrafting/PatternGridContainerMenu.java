package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.content.Menus;
import com.refinedmods.refinedstorage.common.grid.AbstractGridContainerMenu;
import com.refinedmods.refinedstorage.common.support.CraftingMatrix;
import com.refinedmods.refinedstorage.common.support.FilteredContainer;
import com.refinedmods.refinedstorage.common.support.RedstoneMode;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.DisabledSlot;
import com.refinedmods.refinedstorage.common.support.containermenu.FilterSlot;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.ValidatedSlot;
import com.refinedmods.refinedstorage.common.support.packet.c2s.C2SPackets;

import javax.annotation.Nullable;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.item.ItemStack;

public class PatternGridContainerMenu extends AbstractGridContainerMenu {
    private static final int Y_OFFSET_BETWEEN_PLAYER_INVENTORY_AND_PATTERN_INPUT_SLOT = 81;
    private static final int SPACING_BETWEEN_PATTERN_INPUT_AND_PATTERN_OUTPUT_SLOTS = 36;
    private static final int Y_OFFSET_BETWEEN_PLAYER_INVENTORY_AND_FIRST_CRAFTING_MATRIX_SLOT = 86;

    private final Container patternInput;
    private final Container patternOutput;
    private final Container craftingMatrix;
    private final Container craftingResult;

    @Nullable
    private PatternGridListener listener;
    @Nullable
    private PatternGridBlockEntity patternGrid;

    public PatternGridContainerMenu(final int syncId,
                                    final Inventory playerInventory,
                                    final PatternGridData patternGridData) {
        super(Menus.INSTANCE.getPatternGrid(), syncId, playerInventory, patternGridData.gridData());
        this.patternInput = new FilteredContainer(1, PatternGridBlockEntity::isValidPattern);
        this.patternOutput = new PatternOutputContainer();
        this.craftingMatrix = new CraftingMatrix(null, 3, 3);
        this.craftingResult = new ResultContainer();
        onScreenReady(0);
        registerProperty(new ClientProperty<>(PropertyTypes.REDSTONE_MODE, RedstoneMode.IGNORE));
        registerProperty(new ClientProperty<>(PatternGridPropertyTypes.PATTERN_TYPE, patternGridData.patternType()) {
            @Override
            protected void onChangedOnClient(final PatternType newValue) {
                super.onChangedOnClient(newValue);
                if (listener != null) {
                    listener.patternTypeChanged(newValue);
                }
            }
        });
        registerProperty(new ClientProperty<>(PropertyTypes.FUZZY_MODE, false) {
            @Override
            protected void onChangedOnClient(final Boolean newValue) {
                super.onChangedOnClient(newValue);
                if (listener != null) {
                    listener.fuzzyModeChanged(newValue);
                }
            }
        });
    }

    PatternGridContainerMenu(final int syncId,
                             final Inventory playerInventory,
                             final PatternGridBlockEntity grid) {
        super(Menus.INSTANCE.getPatternGrid(), syncId, playerInventory, grid);
        this.patternInput = grid.getPatternInput();
        this.patternOutput = grid.getPatternOutput();
        this.craftingMatrix = grid.getCraftingMatrix();
        this.craftingResult = grid.getCraftingResult();
        this.patternGrid = grid;
        onScreenReady(0);
        registerProperty(new ServerProperty<>(
            PropertyTypes.REDSTONE_MODE,
            grid::getRedstoneMode,
            grid::setRedstoneMode
        ));
        registerProperty(new ServerProperty<>(
            PatternGridPropertyTypes.PATTERN_TYPE,
            grid::getPatternType,
            grid::setPatternType
        ));
        registerProperty(new ServerProperty<>(
            PropertyTypes.FUZZY_MODE,
            grid::isFuzzyMode,
            grid::setFuzzyMode
        ));
    }

    void setListener(final PatternGridListener listener) {
        this.listener = listener;
    }

    PatternType getPatternType() {
        return getProperty(PatternGridPropertyTypes.PATTERN_TYPE).getValue();
    }

    void setPatternType(final PatternType patternType) {
        getProperty(PatternGridPropertyTypes.PATTERN_TYPE).setValue(patternType);
    }

    boolean isFuzzyMode() {
        return Boolean.TRUE.equals(getProperty(PropertyTypes.FUZZY_MODE).getValue());
    }

    void setFuzzyMode(final boolean fuzzyMode) {
        getProperty(PropertyTypes.FUZZY_MODE).setValue(fuzzyMode);
    }

    boolean canCreatePattern() {
        if (patternInput.getItem(0).isEmpty() && patternOutput.getItem(0).isEmpty()) {
            return false;
        }
        return switch (getPatternType()) {
            case CRAFTING -> !craftingResult.getItem(0).isEmpty();
            default -> false;
        };
    }

    @Override
    public void onScreenReady(final int playerInventoryY) {
        super.onScreenReady(playerInventoryY);
        transferManager.clear();
        addPatternSlots(playerInventoryY);
        addCraftingMatrixSlots(playerInventoryY);
    }

    private void addPatternSlots(final int playerInventoryY) {
        addSlot(new ValidatedSlot(
            patternInput,
            0,
            152,
            playerInventoryY - Y_OFFSET_BETWEEN_PLAYER_INVENTORY_AND_PATTERN_INPUT_SLOT,
            PatternGridBlockEntity::isValidPattern
        ));
        addSlot(new ValidatedSlot(
            patternOutput,
            0,
            152,
            playerInventoryY
                - Y_OFFSET_BETWEEN_PLAYER_INVENTORY_AND_PATTERN_INPUT_SLOT
                + SPACING_BETWEEN_PATTERN_INPUT_AND_PATTERN_OUTPUT_SLOTS,
            PatternGridBlockEntity::isValidPattern
        ) {
            @Override
            public boolean mayPlace(final ItemStack stack) {
                return patternOutput.canPlaceItem(0, stack);
            }

            @Override
            public void set(final ItemStack stack) {
                super.set(stack);
                if (patternGrid != null && !stack.isEmpty()) {
                    patternGrid.copyPattern(stack);
                }
            }
        });
        transferManager.addBiTransfer(playerInventory, patternInput);
        transferManager.addTransfer(patternOutput, playerInventory);
    }

    private void addCraftingMatrixSlots(final int playerInventoryY) {
        for (int y = 0; y < 3; ++y) {
            for (int x = 0; x < 3; ++x) {
                final int slotX = 12 + ((x % 3) * 18);
                final int slotY = playerInventoryY
                    - Y_OFFSET_BETWEEN_PLAYER_INVENTORY_AND_FIRST_CRAFTING_MATRIX_SLOT
                    + ((y % 3) * 18);
                addSlot(new FilterSlot(craftingMatrix, x + y * 3, slotX, slotY) {
                    @Override
                    public boolean isActive() {
                        return getPatternType() == PatternType.CRAFTING;
                    }
                });
            }
        }
        addSlot(new DisabledSlot(
            craftingResult,
            0,
            116 + 4,
            playerInventoryY - Y_OFFSET_BETWEEN_PLAYER_INVENTORY_AND_FIRST_CRAFTING_MATRIX_SLOT + 18
        ) {
            @Override
            public boolean isActive() {
                return getPatternType() == PatternType.CRAFTING;
            }
        });
    }

    public void clear() {
        if (patternGrid != null) {
            patternGrid.clear();
        }
    }

    void sendClear() {
        C2SPackets.sendPatternGridClear();
    }

    public void createPattern() {
        if (patternGrid != null) {
            patternGrid.createPattern();
        }
    }

    void sendCreatePattern() {
        C2SPackets.sendPatternGridCreatePattern();
    }

    interface PatternGridListener {
        void patternTypeChanged(PatternType value);

        void fuzzyModeChanged(boolean value);
    }
}
