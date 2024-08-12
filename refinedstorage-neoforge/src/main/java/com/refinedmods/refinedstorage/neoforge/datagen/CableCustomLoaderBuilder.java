package com.refinedmods.refinedstorage.neoforge.datagen;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.neoforged.neoforge.client.model.generators.BlockModelBuilder;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

class CableCustomLoaderBuilder extends CustomLoaderBuilder<BlockModelBuilder> {
    private final DyeColor color;

    CableCustomLoaderBuilder(final ResourceLocation loaderId,
                             final BlockModelBuilder parent,
                             final ExistingFileHelper existingFileHelper,
                             final DyeColor color) {
        super(loaderId, parent, existingFileHelper, true);
        this.color = color;
    }

    @Override
    public JsonObject toJson(final JsonObject json) {
        final JsonObject value = super.toJson(json);
        value.addProperty("color", color.getName());
        return value;
    }
}
