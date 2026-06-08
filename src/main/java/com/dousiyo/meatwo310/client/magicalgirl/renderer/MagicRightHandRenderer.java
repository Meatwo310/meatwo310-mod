package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicRightHandEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MagicRightHandRenderer extends EntityRenderer<MagicRightHandEntity> {
    private static final ResourceLocation SUMMON_CIRCLE = Meatwo310.loc("textures/magic_circle/summon_circle.png");
    private static final ResourceLocation PHASEBREAK_CIRCLE = Meatwo310.loc("textures/magic_circle/phasebreak_circle.png");
    private static final ResourceLocation HAND_TEXTURE = Meatwo310.loc("textures/entity/magic_right_hand.png");

    public MagicRightHandRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull MagicRightHandEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        float age = entity.tickCount + partialTicks;
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.04D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(age * 4.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        MagicalRenderUtil.renderQuad(poseStack, buffer, SUMMON_CIRCLE, packedLight, 2.8F, 255, 255, 255, 255);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.1D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(-age * 2.2F));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        MagicalRenderUtil.renderQuad(poseStack, buffer, PHASEBREAK_CIRCLE, packedLight, 3.3F, 255, 255, 255, 255);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.0D, 1.35D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        MagicalRenderUtil.renderQuad(poseStack, buffer, HAND_TEXTURE, packedLight, 1.5F, 255, 255, 255, 255);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MagicRightHandEntity entity) {
        return HAND_TEXTURE;
    }
}
