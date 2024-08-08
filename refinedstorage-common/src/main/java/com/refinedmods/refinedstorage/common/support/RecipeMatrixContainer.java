package com.refinedmods.refinedstorage.common.support;

import javax.annotation.Nullable;

import net.minecraft.world.inventory.TransientCraftingContainer;

public class RecipeMatrixContainer extends TransientCraftingContainer {
    @Nullable
    private final Runnable listener;

    public RecipeMatrixContainer(@Nullable final Runnable listener, final int width, final int height) {
        super(new CraftingMatrixContainerMenu(listener), width, height);
        this.listener = listener;
    }

    public void changed() {
        if (listener != null) {
            listener.run();
        }
    }
}
