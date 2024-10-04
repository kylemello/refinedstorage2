package com.refinedmods.refinedstorage.common.autocrafting.preview;

import com.refinedmods.refinedstorage.common.util.PlatformUtil;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AutocraftingPreview(AutocraftingPreviewType type, List<AutocraftingPreviewItem> items) {
    public static final StreamCodec<RegistryFriendlyByteBuf, AutocraftingPreview> STREAM_CODEC = StreamCodec.composite(
        PlatformUtil.enumStreamCodec(AutocraftingPreviewType.values()), AutocraftingPreview::type,
        ByteBufCodecs.collection(ArrayList::new, AutocraftingPreviewItem.STREAM_CODEC), AutocraftingPreview::items,
        AutocraftingPreview::new
    );
}
