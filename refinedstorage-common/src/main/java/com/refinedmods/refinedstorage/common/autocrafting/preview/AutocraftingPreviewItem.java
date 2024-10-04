package com.refinedmods.refinedstorage.common.autocrafting.preview;

import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.support.resource.ResourceCodecs;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AutocraftingPreviewItem(PlatformResourceKey resource, long available, long missing, long toCraft) {
    static final StreamCodec<RegistryFriendlyByteBuf, AutocraftingPreviewItem> STREAM_CODEC = StreamCodec.composite(
        ResourceCodecs.STREAM_CODEC, AutocraftingPreviewItem::resource,
        ByteBufCodecs.VAR_LONG, AutocraftingPreviewItem::available,
        ByteBufCodecs.VAR_LONG, AutocraftingPreviewItem::missing,
        ByteBufCodecs.VAR_LONG, AutocraftingPreviewItem::toCraft,
        AutocraftingPreviewItem::new
    );
}
