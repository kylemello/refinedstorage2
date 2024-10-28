package com.refinedmods.refinedstorage.common.autocrafting.autocraftermanager;

import com.refinedmods.refinedstorage.common.content.Menus;
import com.refinedmods.refinedstorage.common.support.AbstractBaseContainerMenu;
import com.refinedmods.refinedstorage.common.support.RedstoneMode;
import com.refinedmods.refinedstorage.common.support.containermenu.ClientProperty;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.containermenu.ServerProperty;
import com.refinedmods.refinedstorage.common.support.stretching.ScreenSizeListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class AutocrafterManagerContainerMenu extends AbstractBaseContainerMenu implements ScreenSizeListener {
    private final Inventory playerInventory;
    private final List<Item> items;
    private final List<AutocrafterManagerSlot> autocrafterSlots = new ArrayList<>();

    public AutocrafterManagerContainerMenu(final int syncId,
                                           final Inventory playerInventory,
                                           final AutocrafterManagerData data) {
        super(Menus.INSTANCE.getAutocrafterManager(), syncId);
        this.playerInventory = playerInventory;
        registerProperty(new ClientProperty<>(PropertyTypes.REDSTONE_MODE, RedstoneMode.IGNORE));
        this.items = data.items().stream().map(item -> Item.from(item, new SimpleContainer(item.slotCount()))).toList();
        resized(0, 0, 0);
    }

    public AutocrafterManagerContainerMenu(final int syncId,
                                           final Inventory playerInventory,
                                           final AutocrafterManagerBlockEntity autocrafterManager,
                                           final List<Container> containers) {
        super(Menus.INSTANCE.getAutocrafterManager(), syncId);
        this.playerInventory = playerInventory;
        registerProperty(new ServerProperty<>(
            PropertyTypes.REDSTONE_MODE,
            autocrafterManager::getRedstoneMode,
            autocrafterManager::setRedstoneMode
        ));
        this.items = Collections.emptyList();
        addServerSideSlots(containers);
    }

    private void addServerSideSlots(final List<Container> containers) {
        for (final Container container : containers) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                addSlot(new Slot(container, i, 0, 0));
            }
        }
        addPlayerInventory(playerInventory, 0, 0);
    }

    public boolean containsPattern(final ItemStack stack) {
        for (final Slot slot : autocrafterSlots) {
            if (slot.getItem() == stack) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void resized(final int playerInventoryY, final int topYStart, final int topYEnd) {
        resetSlots();
        autocrafterSlots.clear();
        final int rowX = 7 + 1;
        final int startY = topYStart - 18;
        int rowY = topYStart;
        for (final AutocrafterManagerContainerMenu.Item item : getViewItems()) {
            for (int i = 0; i < item.slotCount(); i++) {
                final int slotX = rowX + ((i % 9) * 18);
                final int slotY = rowY + 18 + ((i / 9) * 18);
                final var slot = new AutocrafterManagerSlot(item.backingInventory, i, slotX, slotY, startY, topYEnd);
                addSlot(slot);
                autocrafterSlots.add(slot);
            }
            rowY += item.getRowsIncludingTitle() * 18;
        }
        addPlayerInventory(playerInventory, 8, playerInventoryY);
    }

    List<Item> getViewItems() {
        return items;
    }

    List<AutocrafterManagerSlot> getAutocrafterSlots() {
        return autocrafterSlots;
    }

    record Item(Component name, int slotCount, Container backingInventory) {
        private static Item from(final AutocrafterManagerData.Item item, final Container backingInventory) {
            return new Item(item.name(), item.slotCount(), backingInventory);
        }

        int getRowsIncludingTitle() {
            return 1 + Math.ceilDiv(slotCount, 9);
        }
    }
}
