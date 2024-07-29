package com.refinedmods.refinedstorage.neoforge.autocrafting;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

public class PatternGeometryLoader implements IGeometryLoader<PatternUnbakedGeometry> {
    @Override
    public PatternUnbakedGeometry read(final JsonObject jsonObject,
                                       final JsonDeserializationContext jsonDeserializationContext)
        throws JsonParseException {
        return new PatternUnbakedGeometry();
    }
}
