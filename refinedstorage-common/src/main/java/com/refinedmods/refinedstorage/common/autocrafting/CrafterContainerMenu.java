package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.content.Menus;
import com.refinedmods.refinedstorage.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage.common.support.FilteredContainer;
import com.refinedmods.refinedstorage.common.support.RedstoneMode;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeSlot;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static com.refinedmods.refinedstorage.common.autocrafting.CrafterBlockEntity.PATTERNS;
import static com.refinedmods.refinedstorage.common.autocrafting.CrafterBlockEntity.isValidPattern;

public class CrafterContainerMenu extends AbstractBaseContainerMenu {
    private static final int PATTERN_SLOT_X = 8;
    private static final int PATTERN_SLOT_Y = 20;

    @Nullable
    private CrafterBlockEntity crafter;

    public CrafterContainerMenu(final int syncId, final Inventory playerInventory) {
        super(Menus.INSTANCE.getCrafter(), syncId);
        registerProperty(new ClientProperty<>(PropertyTypes.REDSTONE_MODE, RedstoneMode.IGNORE));
        addSlots(
            new FilteredContainer(PATTERNS, stack -> isValidPattern(stack, playerInventory.player.level())),
            new UpgradeContainer(UpgradeDestinations.CRAFTER, null),
            playerInventory.player
        );
    }

    public CrafterContainerMenu(final int syncId, final Inventory playerInventory, final CrafterBlockEntity crafter) {
        super(Menus.INSTANCE.getCrafter(), syncId);
        this.crafter = crafter;
        registerProperty(new ServerProperty<>(
            PropertyTypes.REDSTONE_MODE,
            crafter::getRedstoneMode,
            crafter::setRedstoneMode
        ));
        addSlots(
            crafter.getPatternContainer(),
            crafter.getUpgradeContainer(),
            playerInventory.player
        );
    }

    private void addSlots(final FilteredContainer patternContainer,
                          final UpgradeContainer upgradeContainer,
                          final Player player) {
        for (int i = 0; i < patternContainer.getContainerSize(); ++i) {
            addSlot(createPatternSlot(patternContainer, i, player.level()));
        }
        for (int i = 0; i < upgradeContainer.getContainerSize(); ++i) {
            addSlot(new UpgradeSlot(upgradeContainer, i, 187, 6 + (i * 18)));
        }
        addPlayerInventory(player.getInventory(), 8, 55);
        transferManager.addBiTransfer(player.getInventory(), upgradeContainer);
        transferManager.addBiTransfer(player.getInventory(), patternContainer);
    }

    private Slot createPatternSlot(final FilteredContainer patternContainer,
                                   final int i,
                                   final Level level) {
        final int x = PATTERN_SLOT_X + (18 * i);
        return new PatternSlot(patternContainer, i, x, PATTERN_SLOT_Y, level);
    }

    public boolean containsPattern(final ItemStack stack) {
        for (final Slot slot : slots) {
            if (slot instanceof PatternSlot patternSlot && patternSlot.getItem() == stack) {
                return true;
            }
        }
        return false;
    }
}
