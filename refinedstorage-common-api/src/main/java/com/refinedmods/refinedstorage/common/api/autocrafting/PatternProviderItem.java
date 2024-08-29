package com.refinedmods.refinedstorage.common.api.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apiguardian.api.API;

@API(status = API.Status.STABLE, since = "2.0.0-milestone.4.6")
public interface PatternProviderItem {
    @Nullable
    UUID getId(ItemStack stack);

    Optional<Pattern> getPattern(ItemStack stack, Level level);
}
