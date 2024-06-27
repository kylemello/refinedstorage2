package com.refinedmods.refinedstorage2.platform.fabric.storage;

import com.refinedmods.refinedstorage2.api.storage.StorageState;
import com.refinedmods.refinedstorage2.platform.common.storage.AbstractDiskContainerBlockEntity;
import com.refinedmods.refinedstorage2.platform.common.storage.Disk;
import com.refinedmods.refinedstorage2.platform.fabric.support.render.QuadTranslator;

import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

public abstract class AbstractDiskContainerBakedModel extends ForwardingBakedModel {
    private final Map<Item, BakedModel> diskModels;
    private final BakedModel inactiveLedModel;
    private final QuadTranslator[] diskTranslations;

    protected AbstractDiskContainerBakedModel(final Map<Item, BakedModel> diskModels,
                                              final BakedModel inactiveLedModel,
                                              final QuadTranslator[] diskTranslations) {
        this.diskModels = diskModels;
        this.inactiveLedModel = inactiveLedModel;
        this.diskTranslations = diskTranslations;
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }

    @Override
    public void emitBlockQuads(final BlockAndTintGetter blockView,
                               final BlockState state,
                               final BlockPos pos,
                               final Supplier<RandomSource> randomSupplier,
                               final RenderContext context) {
        final Object renderAttachment = blockView.getBlockEntityRenderData(pos);
        if (renderAttachment instanceof Disk[] disks) {
            emitDiskQuads(blockView, state, pos, randomSupplier, context, disks);
        }
    }

    @Override
    public void emitItemQuads(final ItemStack stack,
                              final Supplier<RandomSource> randomSupplier,
                              final RenderContext context) {
        final CompoundTag tag = BlockItem.getBlockEntityData(stack);
        if (tag == null) {
            return;
        }
        for (int i = 0; i < diskTranslations.length; ++i) {
            final Item diskItem = AbstractDiskContainerBlockEntity.getDisk(tag, i);
            emitDiskQuads(stack, randomSupplier, context, diskItem, i);
        }
    }

    private void emitDiskQuads(final BlockAndTintGetter blockView,
                               final BlockState state,
                               final BlockPos pos,
                               final Supplier<RandomSource> randomSupplier,
                               final RenderContext context,
                               final Disk[] disks) {
        for (int i = 0; i < diskTranslations.length; ++i) {
            final Disk disk = disks[i];
            emitDiskQuads(blockView, state, pos, randomSupplier, context, disk, i);
        }
    }

    private void emitDiskQuads(final BlockAndTintGetter blockView,
                               final BlockState state,
                               final BlockPos pos,
                               final Supplier<RandomSource> randomSupplier,
                               final RenderContext context,
                               final Disk disk,
                               final int index) {
        if (disk.state() == StorageState.NONE) {
            return;
        }
        final BakedModel model = diskModels.get(disk.item());
        if (model == null) {
            return;
        }
        context.pushTransform(diskTranslations[index]);
        model.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        context.popTransform();
    }

    private void emitDiskQuads(final ItemStack stack,
                               final Supplier<RandomSource> randomSupplier,
                               final RenderContext context,
                               @Nullable final Item diskItem,
                               final int index) {
        if (diskItem == null) {
            return;
        }
        final BakedModel diskModel = diskModels.get(diskItem);
        if (diskModel == null) {
            return;
        }
        context.pushTransform(diskTranslations[index]);
        diskModel.emitItemQuads(stack, randomSupplier, context);
        inactiveLedModel.emitItemQuads(stack, randomSupplier, context);
        context.popTransform();
    }
}
