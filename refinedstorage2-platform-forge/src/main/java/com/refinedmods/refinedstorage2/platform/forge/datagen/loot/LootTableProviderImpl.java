package com.refinedmods.refinedstorage2.platform.forge.datagen.loot;

import java.util.List;
import java.util.Set;

import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class LootTableProviderImpl extends LootTableProvider {
    public LootTableProviderImpl(final PackOutput output) {
        super(output, Set.of(), List.of(new SubProviderEntry(
            BlockDropProvider::new,
            LootContextParamSets.BLOCK
        )));
    }
}
