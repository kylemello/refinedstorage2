package com.refinedmods.refinedstorage.common.autocrafting;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class PatternBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
    @Nullable
    private static PatternBlockEntityWithoutLevelRenderer instance;

    private PatternBlockEntityWithoutLevelRenderer(
        final BlockEntityRenderDispatcher blockEntityRenderDispatcher,
        final EntityModelSet entityModelSet
    ) {
        super(blockEntityRenderDispatcher, entityModelSet);
    }

    @Override
    public void renderByItem(final ItemStack stack,
                             final ItemDisplayContext displayContext,
                             final PoseStack poseStack,
                             final MultiBufferSource buffer,
                             final int packedLight,
                             final int packedOverlay) {
        if (PatternRendering.canDisplayOutput(stack)) {
            PatternRendering.getOutput(stack).ifPresent(
                output -> super.renderByItem(output, displayContext, poseStack, buffer, packedLight, packedOverlay)
            );
        }
    }

    public static PatternBlockEntityWithoutLevelRenderer getInstance() {
        if (instance == null) {
            instance = new PatternBlockEntityWithoutLevelRenderer(
                Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels()
            );
        }
        return instance;
    }
}
