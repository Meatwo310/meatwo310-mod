package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.StellaBurstProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class StellaBurstRenderer extends EntityRenderer<StellaBurstProjectileEntity> {
    private static final ResourceLocation MOTE = Meatwo310.loc("textures/particle/magical_girl/small_mote.png");

    public StellaBurstRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull StellaBurstProjectileEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        poseStack.mulPose(Axis.ZP.rotationDegrees((entity.tickCount + partialTicks) * 18.0F));
        renderStar(poseStack, buffer.getBuffer(RenderType.lightning()), 0.52F, 0.23F, 255, 190, 64);
        poseStack.mulPose(Axis.ZP.rotationDegrees(36.0F));
        renderStar(poseStack, buffer.getBuffer(RenderType.lightning()), 0.34F, 0.13F, 255, 255, 255);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void renderStar(PoseStack poseStack, VertexConsumer consumer, float outer, float inner, int red, int green, int blue) {
        Matrix4f matrix = poseStack.last().pose();
        for (int i = 0; i < 5; i++) {
            double pointAngle = -Math.PI / 2.0D + Math.PI * 2.0D * i / 5.0D;
            double leftAngle = pointAngle - Math.PI / 5.0D;
            double rightAngle = pointAngle + Math.PI / 5.0D;
            vertex(matrix, consumer, 0.0F, 0.0F, 0.02F, red, green, blue, 0.88F);
            vertex(matrix, consumer, (float) Math.cos(leftAngle) * inner, (float) Math.sin(leftAngle) * inner, 0.02F, red, green, blue, 0.88F);
            vertex(matrix, consumer, (float) Math.cos(pointAngle) * outer, (float) Math.sin(pointAngle) * outer, 0.02F, red, green, blue, 0.88F);
            vertex(matrix, consumer, (float) Math.cos(rightAngle) * inner, (float) Math.sin(rightAngle) * inner, 0.02F, red, green, blue, 0.88F);
        }
        for (int i = 0; i < 5; i++) {
            double pointAngle = -Math.PI / 2.0D + Math.PI * 2.0D * i / 5.0D;
            double leftAngle = pointAngle - Math.PI / 5.0D;
            double rightAngle = pointAngle + Math.PI / 5.0D;
            vertex(matrix, consumer, 0.0F, 0.0F, -0.02F, red, green, blue, 0.58F);
            vertex(matrix, consumer, (float) Math.cos(rightAngle) * inner, (float) Math.sin(rightAngle) * inner, -0.02F, red, green, blue, 0.58F);
            vertex(matrix, consumer, (float) Math.cos(pointAngle) * outer, (float) Math.sin(pointAngle) * outer, -0.02F, red, green, blue, 0.58F);
            vertex(matrix, consumer, (float) Math.cos(leftAngle) * inner, (float) Math.sin(leftAngle) * inner, -0.02F, red, green, blue, 0.58F);
        }
    }

    private static void vertex(Matrix4f matrix, VertexConsumer consumer, float x, float y, float z, int red, int green, int blue, float alpha) {
        consumer.vertex(matrix, x, y, z).color(red / 255.0F, green / 255.0F, blue / 255.0F, alpha).endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull StellaBurstProjectileEntity entity) {
        return MOTE;
    }
}
