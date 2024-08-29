package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.common.content.Menus;
import com.refinedmods.refinedstorage.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage.common.support.FilteredContainer;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.support.packet.c2s.C2SPackets;
import com.refinedmods.refinedstorage.common.support.packet.s2c.S2CPackets;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeSlot;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.RateLimiter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

    private final Player player;
    private final boolean partOfChain;
    private final boolean headOfChain;
    private final RateLimiter nameRateLimiter = RateLimiter.create(0.5);

    @Nullable
    private CrafterBlockEntity crafter;
    @Nullable
    private Listener listener;
    private Component name;

    public CrafterContainerMenu(final int syncId, final Inventory playerInventory, final CrafterData data) {
        super(Menus.INSTANCE.getCrafter(), syncId);
        this.player = playerInventory.player;
        registerProperty(new ClientProperty<>(CrafterPropertyTypes.LOCK_MODE, LockMode.NEVER));
        registerProperty(new ClientProperty<>(CrafterPropertyTypes.PRIORITY, 0));
        addSlots(
            new FilteredContainer(PATTERNS, stack -> isValidPattern(stack, playerInventory.player.level())),
            new UpgradeContainer(UpgradeDestinations.CRAFTER)
        );
        this.name = Component.empty();
        this.partOfChain = data.partOfChain();
        this.headOfChain = data.headOfChain();
    }

    public CrafterContainerMenu(final int syncId, final Inventory playerInventory, final CrafterBlockEntity crafter) {
        super(Menus.INSTANCE.getCrafter(), syncId);
        this.crafter = crafter;
        this.player = playerInventory.player;
        this.name = crafter.getDisplayName();
        this.partOfChain = false;
        this.headOfChain = false;
        registerProperty(new ServerProperty<>(
            CrafterPropertyTypes.LOCK_MODE,
            crafter::getLockMode,
            crafter::setLockMode
        ));
        registerProperty(new ServerProperty<>(
            CrafterPropertyTypes.PRIORITY,
            crafter::getPriority,
            crafter::setPriority
        ));
        addSlots(crafter.getPatternContainer(), crafter.getUpgradeContainer());
    }

    boolean canChangeName() {
        return !partOfChain;
    }

    boolean isPartOfChain() {
        return partOfChain;
    }

    boolean isHeadOfChain() {
        return headOfChain;
    }

    void setListener(@Nullable final Listener listener) {
        this.listener = listener;
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (crafter == null) {
            return;
        }
        if (nameRateLimiter.tryAcquire()) {
            detectNameChange();
        }
    }

    private void detectNameChange() {
        if (crafter == null) {
            return;
        }
        final Component newName = crafter.getDisplayName();
        if (!newName.equals(name)) {
            this.name = newName;
            S2CPackets.sendCrafterNameUpdate((ServerPlayer) player, newName);
        }
    }

    private void addSlots(final FilteredContainer patternContainer, final UpgradeContainer upgradeContainer) {
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

    public void changeName(final String newName) {
        if (partOfChain) {
            return;
        }
        if (crafter != null) {
            crafter.setCustomName(newName);
            detectNameChange();
        } else {
            C2SPackets.sendCrafterNameChange(newName);
        }
    }

    public void nameChanged(final Component newName) {
        if (listener != null) {
            listener.nameChanged(newName);
        }
    }

    @FunctionalInterface
    public interface Listener {
        void nameChanged(Component name);
    }
}
