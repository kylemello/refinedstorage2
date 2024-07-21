package com.refinedmods.refinedstorage.common.storage.diskdrive;

import com.refinedmods.refinedstorage.common.api.storage.StorageInfo;

import java.util.Optional;

import net.minecraft.world.item.ItemStack;

interface StorageDiskInfoAccessor {
    Optional<StorageInfo> getInfo(ItemStack stack);
}
