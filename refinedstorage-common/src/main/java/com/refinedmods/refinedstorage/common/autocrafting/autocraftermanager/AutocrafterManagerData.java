package com.refinedmods.refinedstorage.common.autocrafting.autocraftermanager;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AutocrafterManagerData(List<Group> groups, boolean active) {
    private static final StreamCodec<RegistryFriendlyByteBuf, Group> GROUP_STREAM_CODEC = StreamCodec.composite(
        ComponentSerialization.STREAM_CODEC, Group::name,
        ByteBufCodecs.INT, Group::slotCount,
        Group::new
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, AutocrafterManagerData> STREAM_CODEC = StreamCodec
        .composite(
            ByteBufCodecs.collection(ArrayList::new, GROUP_STREAM_CODEC), AutocrafterManagerData::groups,
            ByteBufCodecs.BOOL, AutocrafterManagerData::active,
            AutocrafterManagerData::new
        );

    public record Group(Component name, int slotCount) {
    }
}
