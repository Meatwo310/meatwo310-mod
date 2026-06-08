package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicSafetyAnchorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MagicSafetyAnchorRenderer extends EntityRenderer<MagicSafetyAnchorEntity> {
    private static final ResourceLocation CIRCLE = Meatwo310.loc("textures/magic_circle/summon_circle.png");
    private static final ResourceLocation CORE = Meatwo310.loc("textures/particle/magical_girl/dark_mote.png");

    public MagicSafetyAnchorRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull MagicSafetyAnchorEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        float age = entity.tickCount + partialTicks;
        float pulse = 0.92F + 0.08F * (float) Math.sin(age * 0.12F);

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.04D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(age * 1.6F));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        MagicalRenderUtil.renderQuad(poseStack, buffer, CIRCLE, packedLight, MagicSafetyAnchorEntity.RADIUS * pulse, 255, 255, 255, 255);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.translate(0.0D, 1.0D, 0.0D);
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.ZP.rotationDegrees(-age * 2.4F));
        MagicalRenderUtil.renderQuad(poseStack, buffer, CORE, packedLight, 0.65F, 255, 255, 255, 255);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MagicSafetyAnchorEntity entity) {
        return CIRCLE;
    }
}
