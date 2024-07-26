package com.refinedmods.refinedstorage.fabric.mixin;

import com.refinedmods.refinedstorage.common.autocrafting.PatternBlockEntityWithoutLevelRenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public abstract class AbstractBlockEntityWithoutLevelRendererMixin {
    @Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
    public void onRenderByItem(final ItemStack stack,
                               final ItemDisplayContext displayContext,
                               final PoseStack poseStack,
                               final MultiBufferSource buffer,
                               final int packedLight,
                               final int packedOverlay,
                               final CallbackInfo callbackInfo) {
        PatternBlockEntityWithoutLevelRenderer.getInstance().renderByItem(
            stack,
            displayContext,
            poseStack,
            buffer,
            packedLight,
            packedOverlay
        );
    }
}
