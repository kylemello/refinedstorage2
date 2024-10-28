package com.refinedmods.refinedstorage.common.autocrafting.autocraftermanager;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AutocrafterManagerData(List<Item> items) {
    private static final StreamCodec<RegistryFriendlyByteBuf, Item> ITEM_STREAM_CODEC = StreamCodec.composite(
        ComponentSerialization.STREAM_CODEC, Item::name,
        ByteBufCodecs.INT, Item::slotCount,
        Item::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AutocrafterManagerData> STREAM_CODEC = StreamCodec
        .composite(ByteBufCodecs.collection(ArrayList::new, ITEM_STREAM_CODEC), AutocrafterManagerData::items,
            AutocrafterManagerData::new);

    public record Item(Component name, int slotCount) {
    }
}
