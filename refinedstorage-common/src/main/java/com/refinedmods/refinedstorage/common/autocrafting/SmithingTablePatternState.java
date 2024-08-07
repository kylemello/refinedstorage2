package com.refinedmods.refinedstorage.common.autocrafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record SmithingTablePatternState(ItemStack template,
                                        ItemStack base,
                                        ItemStack addition) {
    public static final Codec<SmithingTablePatternState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.CODEC.fieldOf("template").forGetter(SmithingTablePatternState::template),
        ItemStack.CODEC.fieldOf("base").forGetter(SmithingTablePatternState::base),
        ItemStack.CODEC.fieldOf("addition").forGetter(SmithingTablePatternState::addition)
    ).apply(instance, SmithingTablePatternState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SmithingTablePatternState> STREAM_CODEC =
        StreamCodec.composite(
            ItemStack.STREAM_CODEC, SmithingTablePatternState::template,
            ItemStack.STREAM_CODEC, SmithingTablePatternState::base,
            ItemStack.STREAM_CODEC, SmithingTablePatternState::addition,
            SmithingTablePatternState::new
        );
}
