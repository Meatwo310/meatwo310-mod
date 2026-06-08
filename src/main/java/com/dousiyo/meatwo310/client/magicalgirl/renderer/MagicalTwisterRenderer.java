package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicalTwisterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class MagicalTwisterRenderer extends EntityRenderer<MagicalTwisterEntity> {
    private static final ResourceLocation SMOKE = Meatwo310.loc("textures/particle/magical_girl/gas_soft_blue.png");
    private static final ResourceLocation VEIL = Meatwo310.loc("textures/particle/magical_girl/dark_veil.png");

    public MagicalTwisterRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public boolean shouldRender(@NotNull MagicalTwisterEntity entity, @NotNull Frustum frustum, double camX, double camY, double camZ) {
        return entity.distanceToSqr(camX, camY, camZ) < 256.0D * 256.0D;
    }

    @Override
    public void render(@NotNull MagicalTwisterEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        float scale = entity.getEffectScale(partialTicks);
        if (scale <= 0.01F) {
            return;
        }

        float age = entity.tickCount + partialTicks;
        float radius = entity.getRenderRadius() * scale;
        float height = entity.getRenderHeight() * scale;
        renderShell(entity, poseStack, buffer, packedLight, age, radius, height, SMOKE, 0.82F, 0.9F);
        renderShell(entity, poseStack, buffer, packedLight, age * 1.18F + 24.0F, radius * 0.68F, height * 0.96F, VEIL, 0.58F, 0.68F);

        poseStack.pushPose();
        poseStack.translate(0.0D, height * 0.08F, 0.0D);
        poseStack.scale(radius * 0.55F, height * 0.08F, radius * 0.55F);
        poseStack.mulPose(Axis.YP.rotationDegrees(age * 30.0F));
        renderHorizontalSwirl(poseStack, buffer, packedLight, age, 0.55F);
        poseStack.popPose();

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void renderShell(MagicalTwisterEntity entity, PoseStack poseStack, MultiBufferSource buffer, int packedLight, float age, float radius, float height, ResourceLocation texture, float alphaScale, float saturation) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
        int segments = 18;
        int arms = 7;
        float spin = age * 52.0F;
        for (int arm = 0; arm < arms; arm++) {
            float armOffset = 360.0F * arm / arms;
            for (int i = 0; i < segments; i++) {
                float t0 = i / (float) segments;
                float t1 = (i + 1) / (float) segments;
                float y0 = height * t0;
                float y1 = height * t1;
                float r0 = funnelRadius(radius, t0);
                float r1 = funnelRadius(radius, t1);
                float a0 = (float) Math.toRadians(spin + armOffset + t0 * 900.0F);
                float a1 = (float) Math.toRadians(spin + armOffset + t1 * 900.0F);
                float width0 = 1.15F + r0 * 0.18F;
                float width1 = 1.15F + r1 * 0.18F;
                float[] color = hsv(arm / (float) arms + t0 * 0.75F + age * 0.018F, saturation, 1.0F);
                int alpha = (int) (210.0F * alphaScale * entity.getEffectScale(0.0F));
                ribbonQuad(poseStack.last().pose(), consumer, a0, a1, r0, r1, y0, y1, width0, width1, color, alpha, packedLight, false);
                ribbonQuad(poseStack.last().pose(), consumer, a0, a1, r0, r1, y0, y1, width0, width1, color, alpha, packedLight, true);
            }
        }
    }

    private static void renderHorizontalSwirl(PoseStack poseStack, MultiBufferSource buffer, int packedLight, float age, float alphaScale) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(SMOKE));
        Matrix4f matrix = poseStack.last().pose();
        float[] color = hsv(age * 0.024F, 0.72F, 1.0F);
        int alpha = (int) (185.0F * alphaScale);
        consumer.vertex(matrix, -1.0F, 0.0F, -1.0F).color(color[0], color[1], color[2], alpha / 255.0F).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(matrix, 1.0F, 0.0F, -1.0F).color(color[0], color[1], color[2], alpha / 255.0F).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(matrix, 1.0F, 0.0F, 1.0F).color(color[0], color[1], color[2], alpha / 255.0F).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(matrix, -1.0F, 0.0F, 1.0F).color(color[0], color[1], color[2], alpha / 255.0F).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(poseStack.last().normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }

    private static float funnelRadius(float radius, float t) {
        return radius * (1.0F - t * 0.72F) + 0.9F;
    }

    private static void ribbonQuad(Matrix4f matrix, VertexConsumer consumer, float angle0, float angle1, float radius0, float radius1, float y0, float y1, float width0, float width1, float[] color, int alpha, int packedLight, boolean reverse) {
        float x0 = (float) Math.cos(angle0) * radius0;
        float z0 = (float) Math.sin(angle0) * radius0;
        float x1 = (float) Math.cos(angle1) * radius1;
        float z1 = (float) Math.sin(angle1) * radius1;
        float nx0 = (float) Math.cos(angle0 + Math.PI * 0.5D) * width0;
        float nz0 = (float) Math.sin(angle0 + Math.PI * 0.5D) * width0;
        float nx1 = (float) Math.cos(angle1 + Math.PI * 0.5D) * width1;
        float nz1 = (float) Math.sin(angle1 + Math.PI * 0.5D) * width1;
        if (reverse) {
            vertex(consumer, matrix, x0 + nx0, y0, z0 + nz0, color, alpha, packedLight, 0.0F, 1.0F);
            vertex(consumer, matrix, x1 + nx1, y1, z1 + nz1, color, alpha, packedLight, 1.0F, 1.0F);
            vertex(consumer, matrix, x1 - nx1, y1, z1 - nz1, color, alpha, packedLight, 1.0F, 0.0F);
            vertex(consumer, matrix, x0 - nx0, y0, z0 - nz0, color, alpha, packedLight, 0.0F, 0.0F);
            return;
        }
        vertex(consumer, matrix, x0 - nx0, y0, z0 - nz0, color, alpha, packedLight, 0.0F, 1.0F);
        vertex(consumer, matrix, x1 - nx1, y1, z1 - nz1, color, alpha, packedLight, 1.0F, 1.0F);
        vertex(consumer, matrix, x1 + nx1, y1, z1 + nz1, color, alpha, packedLight, 1.0F, 0.0F);
        vertex(consumer, matrix, x0 + nx0, y0, z0 + nz0, color, alpha, packedLight, 0.0F, 0.0F);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, float[] color, int alpha, int packedLight, float u, float v) {
        consumer.vertex(matrix, x, y, z)
                .color((int) (color[0] * 255.0F), (int) (color[1] * 255.0F), (int) (color[2] * 255.0F), alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    private static float[] hsv(float hue, float saturation, float value) {
        hue = hue - (float) Math.floor(hue);
        float scaled = hue * 6.0F;
        int sector = (int) Math.floor(scaled);
        float fraction = scaled - sector;
        float p = value * (1.0F - saturation);
        float q = value * (1.0F - saturation * fraction);
        float t = value * (1.0F - saturation * (1.0F - fraction));
        return switch (sector % 6) {
            case 0 -> new float[]{value, t, p};
            case 1 -> new float[]{q, value, p};
            case 2 -> new float[]{p, value, t};
            case 3 -> new float[]{p, q, value};
            case 4 -> new float[]{t, p, value};
            default -> new float[]{value, p, q};
        };
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MagicalTwisterEntity entity) {
        return SMOKE;
    }
}
