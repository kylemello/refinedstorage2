package com.refinedmods.refinedstorage2.platform.forge.storage;

import com.refinedmods.refinedstorage2.api.storage.StorageState;
import com.refinedmods.refinedstorage2.platform.common.storage.AbstractDiskContainerBlockEntity;
import com.refinedmods.refinedstorage2.platform.common.storage.Disk;
import com.refinedmods.refinedstorage2.platform.forge.support.render.ItemBakedModel;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public class DiskContainerItemOverrides<T> extends ItemOverrides {
    private final LoadingCache<T, BakedModel> cache;
    private final BakedModel originalModel;
    private final Vector3f[] diskTranslations;
    private final Function<Disk[], T> cacheKeyFactory;

    public DiskContainerItemOverrides(final LoadingCache<T, List<BakedQuad>> blockCache,
                                      final BakedModel originalModel,
                                      final Vector3f[] diskTranslations,
                                      final Function<Disk[], T> cacheKeyFactory,
                                      final BiFunction<T, Direction, T> cacheKeySideFactory) {
        this.cache = CacheBuilder.newBuilder().build(CacheLoader.from(cacheKey -> new ItemBakedModel(
            originalModel,
            blockCache.getUnchecked(cacheKey),
            Map.of(
                Direction.NORTH, blockCache.getUnchecked(cacheKeySideFactory.apply(cacheKey, Direction.NORTH)),
                Direction.EAST, blockCache.getUnchecked(cacheKeySideFactory.apply(cacheKey, Direction.EAST)),
                Direction.SOUTH, blockCache.getUnchecked(cacheKeySideFactory.apply(cacheKey, Direction.SOUTH)),
                Direction.WEST, blockCache.getUnchecked(cacheKeySideFactory.apply(cacheKey, Direction.WEST)),
                Direction.UP, blockCache.getUnchecked(cacheKeySideFactory.apply(cacheKey, Direction.UP)),
                Direction.DOWN, blockCache.getUnchecked(cacheKeySideFactory.apply(cacheKey, Direction.DOWN))
            )
        )));
        this.originalModel = originalModel;
        this.diskTranslations = diskTranslations;
        this.cacheKeyFactory = cacheKeyFactory;
    }

    @Nullable
    @Override
    public BakedModel resolve(final BakedModel bakedModel,
                              final ItemStack stack,
                              @Nullable final ClientLevel level,
                              @Nullable final LivingEntity entity,
                              final int seed) {
        final CompoundTag tag = BlockItem.getBlockEntityData(stack);
        if (tag == null) {
            return originalModel.getOverrides().resolve(bakedModel, stack, level, entity, seed);
        }
        final Disk[] disks = new Disk[diskTranslations.length];
        for (int i = 0; i < diskTranslations.length; ++i) {
            final Item diskItem = AbstractDiskContainerBlockEntity.getDisk(tag, i);
            disks[i] = new Disk(diskItem, diskItem == null ? StorageState.NONE : StorageState.INACTIVE);
        }
        return cache.getUnchecked(cacheKeyFactory.apply(disks));
    }
}
