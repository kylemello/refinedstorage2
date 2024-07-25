package com.refinedmods.refinedstorage.common.api.support.network.item;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apiguardian.api.API;

@FunctionalInterface
@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.5")
public interface NetworkItemPlayerValidator {
    boolean isValid(PlayerCoordinates coordinates);

    record PlayerCoordinates(ResourceKey<Level> dimension, Vec3 position) {
    }
}
