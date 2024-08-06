package com.refinedmods.refinedstorage.common.autocrafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record StonecutterPatternState(ItemStack input, ItemStack selectedOutput) {
    public static final Codec<StonecutterPatternState> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ItemStack.CODEC.fieldOf("input").forGetter(StonecutterPatternState::input),
        ItemStack.CODEC.fieldOf("selectedOutput").forGetter(StonecutterPatternState::selectedOutput)
    ).apply(instance, StonecutterPatternState::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, StonecutterPatternState> STREAM_CODEC =
        StreamCodec.composite(
            ItemStack.STREAM_CODEC, StonecutterPatternState::input,
            ItemStack.STREAM_CODEC, StonecutterPatternState::selectedOutput,
            StonecutterPatternState::new
        );
}
