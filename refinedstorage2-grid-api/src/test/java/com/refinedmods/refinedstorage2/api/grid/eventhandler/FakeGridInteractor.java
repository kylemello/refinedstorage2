package com.refinedmods.refinedstorage2.api.grid.eventhandler;

import com.refinedmods.refinedstorage2.api.core.Action;
import com.refinedmods.refinedstorage2.api.stack.item.Rs2ItemStack;
import com.refinedmods.refinedstorage2.api.storage.disk.ItemStorageDisk;
import com.refinedmods.refinedstorage2.api.storage.disk.StorageDisk;

import java.util.Collection;

public class FakeGridInteractor implements GridInteractor {
    public static final String NAME = "Fake interactor";

    private Rs2ItemStack cursorStack = Rs2ItemStack.EMPTY;
    private StorageDisk<Rs2ItemStack> inventory = new ItemStorageDisk(1000);

    public void resetInventoryAndSetCapacity(int capacity) {
        inventory = new ItemStorageDisk(capacity);
    }

    @Override
    public Rs2ItemStack getCursorStack() {
        return cursorStack;
    }

    @Override
    public void setCursorStack(Rs2ItemStack stack) {
        this.cursorStack = stack;
    }

    @Override
    public Rs2ItemStack insertIntoInventory(Rs2ItemStack stack, int preferredSlot, Action action) {
        return inventory.insert(stack, stack.getAmount(), action).orElse(Rs2ItemStack.EMPTY);
    }

    @Override
    public Rs2ItemStack extractFromInventory(Rs2ItemStack template, int slot, long count, Action action) {
        return inventory.extract(template, count, action).orElse(Rs2ItemStack.EMPTY);
    }

    @Override
    public String getName() {
        return NAME;
    }

    public Collection<Rs2ItemStack> getInventory() {
        return inventory.getStacks();
    }
}
