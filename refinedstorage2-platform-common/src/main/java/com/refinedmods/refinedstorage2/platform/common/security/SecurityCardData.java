package com.refinedmods.refinedstorage2.platform.common.security;

import com.refinedmods.refinedstorage2.platform.api.PlatformApi;
import com.refinedmods.refinedstorage2.platform.api.security.PlatformPermission;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReference;
import com.refinedmods.refinedstorage2.platform.api.support.network.bounditem.SlotReferenceFactory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record SecurityCardData(SlotReference slotReference, List<Permission> permissions) {
    public static final StreamCodec<RegistryFriendlyByteBuf, SecurityCardData> STREAM_CODEC = StreamCodec.composite(
        SlotReferenceFactory.STREAM_CODEC, SecurityCardData::slotReference,
        ByteBufCodecs.collection(
            ArrayList::new,
            StreamCodec.composite(
                PlatformApi.INSTANCE.getPermissionRegistry().streamCodec(), Permission::permission,
                ByteBufCodecs.BOOL, Permission::allowed,
                ByteBufCodecs.BOOL, Permission::dirty,
                Permission::new
            )
        ), SecurityCardData::permissions,
        SecurityCardData::new
    );

    record Permission(PlatformPermission permission, boolean allowed, boolean dirty) {
    }
}
