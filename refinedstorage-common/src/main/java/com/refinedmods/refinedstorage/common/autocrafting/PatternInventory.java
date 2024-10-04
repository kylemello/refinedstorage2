package com.refinedmods.refinedstorage.common.autocrafting;

import com.refinedmods.refinedstorage.api.core.NullableType;
import com.refinedmods.refinedstorage.common.support.FilteredContainer;

import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import static com.refinedmods.refinedstorage.common.autocrafting.AutocrafterBlockEntity.PATTERNS;
import static com.refinedmods.refinedstorage.common.autocrafting.AutocrafterBlockEntity.isValidPattern;

class PatternInventory extends FilteredContainer {
    @Nullable
    private Listener listener;

    PatternInventory(final Supplier<@NullableType Level> levelSupplier) {
        super(PATTERNS,
            stack -> Optional.ofNullable(levelSupplier.get()).map(level -> isValidPattern(stack, level)).orElse(false));
    }

    void setListener(@Nullable final Listener listener) {
        this.listener = listener;
    }

    @Override
    public ItemStack removeItem(final int slot, final int amount) {
        // Forge InvWrapper calls this instead of setItem.
        final ItemStack result = super.removeItem(slot, amount);
        if (listener != null) {
            listener.patternChanged(slot);
        }
        return result;
    }

    @Override
    public void setItem(final int slot, final ItemStack stack) {
        super.setItem(slot, stack);
        if (listener != null) {
            listener.patternChanged(slot);
        }
    }

    interface Listener {
        void patternChanged(int slot);
    }
}
