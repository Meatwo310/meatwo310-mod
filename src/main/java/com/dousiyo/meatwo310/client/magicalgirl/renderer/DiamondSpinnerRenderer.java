package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.DiamondSpinnerEntity;
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

public class DiamondSpinnerRenderer extends EntityRenderer<DiamondSpinnerEntity> {
    private static final ResourceLocation CORE = Meatwo310.loc("textures/particle/magical_girl/small_mote.png");
    private static final ResourceLocation RING = Meatwo310.loc("textures/magic_circle/laser_circle.png");

    public DiamondSpinnerRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull DiamondSpinnerEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        float spin = entity.getSpin(partialTicks);
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(spin));
        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        renderOctahedron(poseStack, buffer.getBuffer(RenderType.lightning()), entity.getAttackState() == 2);
        MagicalRenderUtil.renderQuad(poseStack, buffer, RING, packedLight, 0.72F, 218, 92, 255, 190);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        MagicalRenderUtil.renderQuad(poseStack, buffer, RING, packedLight, 0.72F, 120, 225, 255, 150);
        poseStack.popPose();

        poseStack.pushPose();
        poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        MagicalRenderUtil.renderQuad(poseStack, buffer, CORE, packedLight, entity.getAttackState() == 2 ? 0.36F : 0.26F, 255, 255, 255, 245);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void renderOctahedron(PoseStack poseStack, VertexConsumer consumer, boolean launched) {
        Matrix4f matrix = poseStack.last().pose();
        float radius = launched ? 0.55F : 0.45F;
        float height = launched ? 0.9F : 0.74F;
        float[][] v = new float[][]{
                {0.0F, height, 0.0F},
                {radius, 0.0F, 0.0F},
                {0.0F, 0.0F, radius},
                {-radius, 0.0F, 0.0F},
                {0.0F, 0.0F, -radius},
                {0.0F, -height, 0.0F}
        };
        face(matrix, consumer, v[0], v[1], v[2], 255, 105, 235, 0.72F);
        face(matrix, consumer, v[0], v[2], v[3], 185, 95, 255, 0.72F);
        face(matrix, consumer, v[0], v[3], v[4], 130, 220, 255, 0.62F);
        face(matrix, consumer, v[0], v[4], v[1], 255, 255, 255, 0.66F);
        face(matrix, consumer, v[5], v[2], v[1], 190, 90, 255, 0.52F);
        face(matrix, consumer, v[5], v[3], v[2], 255, 120, 210, 0.52F);
        face(matrix, consumer, v[5], v[4], v[3], 120, 220, 255, 0.46F);
        face(matrix, consumer, v[5], v[1], v[4], 255, 255, 255, 0.48F);
    }

    private static void face(Matrix4f matrix, VertexConsumer consumer, float[] a, float[] b, float[] c, int red, int green, int blue, float alpha) {
        vertex(matrix, consumer, a, red, green, blue, alpha);
        vertex(matrix, consumer, b, red, green, blue, alpha);
        vertex(matrix, consumer, c, red, green, blue, alpha);
        vertex(matrix, consumer, c, red, green, blue, alpha);
    }

    private static void vertex(Matrix4f matrix, VertexConsumer consumer, float[] pos, int red, int green, int blue, float alpha) {
        consumer.vertex(matrix, pos[0], pos[1], pos[2]).color(red / 255.0F, green / 255.0F, blue / 255.0F, alpha).endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull DiamondSpinnerEntity entity) {
        return CORE;
    }
}
