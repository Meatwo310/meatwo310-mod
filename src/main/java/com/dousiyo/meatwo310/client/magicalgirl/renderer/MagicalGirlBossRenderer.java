package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicalGirlBossEntity;
import com.dousiyo.meatwo310.magicalgirl.MagicalGirlPhase;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

public class MagicalGirlBossRenderer extends GeoEntityRenderer<MagicalGirlBossEntity> {
    private static final ResourceLocation SUMMON_CIRCLE = Meatwo310.loc("textures/magic_circle/summon_circle.png");
    private static final ResourceLocation LASER_CIRCLE = Meatwo310.loc("textures/magic_circle/laser_circle.png");
    private static final ResourceLocation PHASEBREAK_CIRCLE = Meatwo310.loc("textures/magic_circle/phasebreak_circle.png");
    private static final ResourceLocation MEMORY_CIRCLE = Meatwo310.loc("textures/magic_circle/memory_circle.png");

    public MagicalGirlBossRenderer(EntityRendererProvider.Context context) {
        super(context, new MagicalGirlGeoModel());
        this.shadowRadius = 0.5F;
    }

    @Override
    public void render(@NotNull MagicalGirlBossEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        if (!entity.isPassenger() && !entity.isSealed() && !entity.isBreakingSeal() && !isBreakingIntroSeal(entity)) {
            renderMagicCircle(entity, partialTicks, poseStack, buffer);
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        if (entity.isSealed() || entity.isBreakingSeal() || isBreakingIntroSeal(entity)) {
            renderSealCrystal(entity, partialTicks, poseStack, buffer);
        }
        if (entity.getVisualPhase() == MagicalGirlPhase.DEFEAT || entity.getVisualPhase() == MagicalGirlPhase.FLASHBACK || entity.getVisualPhase() == MagicalGirlPhase.END_HOOK) {
            renderDefeatDissolve(entity, partialTicks, poseStack, buffer);
        }
    }

    private static boolean isBreakingIntroSeal(MagicalGirlBossEntity entity) {
        return entity.getVisualPhase() == MagicalGirlPhase.INTRO && entity.getPhaseTicks() < 80;
    }

    @Override
    public void scaleModelForRender(float widthScale, float heightScale, PoseStack poseStack, MagicalGirlBossEntity animatable, BakedGeoModel model, boolean isReRender, float partialTick, int packedLight, int packedOverlay) {
        poseStack.scale(0.96F, 0.96F, 0.96F);
        super.scaleModelForRender(widthScale, heightScale, poseStack, animatable, model, isReRender, partialTick, packedLight, packedOverlay);
    }

    private void renderMagicCircle(MagicalGirlBossEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.pushPose();
        poseStack.translate(0.0D, -0.04D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount * 2.0F) % 360.0F));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        float size = entity.hasRedEyes() ? 4.8F : 3.8F;
        float pulse = 0.5F + 0.5F * Mth.sin((entity.tickCount + partialTicks) * 0.16F);
        ResourceLocation texture = circleTexture(entity.getVisualPhase());
        VertexConsumer glowConsumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
        PoseStack.Pose pose = poseStack.last();
        renderCircleQuad(pose, glowConsumer, size * (1.08F + pulse * 0.04F), 95 + (int) (pulse * 45.0F), 0.002F);
        renderCircleQuad(pose, glowConsumer, size * (1.20F + pulse * 0.05F), 35 + (int) (pulse * 25.0F), 0.001F);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        renderCircleQuad(pose, consumer, size, 255, 0.003F);
        poseStack.popPose();
    }

    private static void renderCircleQuad(PoseStack.Pose pose, VertexConsumer consumer, float size, int alpha, float z) {
        consumer.vertex(pose.pose(), -size, -size, z).color(255, 255, 255, alpha).uv(0.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(pose.pose(), size, -size, z).color(255, 255, 255, alpha).uv(1.0F, 1.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(pose.pose(), size, size, z).color(255, 255, 255, alpha).uv(1.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
        consumer.vertex(pose.pose(), -size, size, z).color(255, 255, 255, alpha).uv(0.0F, 0.0F).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(LightTexture.FULL_BRIGHT).normal(pose.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }

    private ResourceLocation circleTexture(MagicalGirlPhase phase) {
        return switch (phase) {
            case BREAK_1, BREAK_2, FINAL_PHASE -> PHASEBREAK_CIRCLE;
            case PHASE_2 -> LASER_CIRCLE;
            case DEFEAT, FLASHBACK, END_HOOK -> MEMORY_CIRCLE;
            default -> SUMMON_CIRCLE;
        };
    }

    private void renderSealCrystal(MagicalGirlBossEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        float age = entity.tickCount + partialTicks;
        boolean breaking = entity.getVisualPhase() == MagicalGirlPhase.INTRO || entity.isBreakingSeal();
        float breakProgress = entity.isBreakingSeal()
                ? entity.getSealEscapeBreakProgress(partialTicks)
                : breaking ? Mth.clamp((entity.getPhaseTicks() + partialTicks) / 80.0F, 0.0F, 1.0F) : 0.0F;
        float alpha = breaking ? 0.55F * (1.0F - breakProgress) : 0.48F;
        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());

        poseStack.pushPose();
        poseStack.translate(0.0D, 1.0D + breakProgress * 0.18D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(age * (breaking ? 2.8F : 0.45F)));
        Matrix4f matrix = poseStack.last().pose();
        renderCrystalShell(matrix, consumer, 1.72F + breakProgress * 0.9F, 2.95F, alpha);
        renderCrystalEdges(matrix, consumer, 1.78F + breakProgress * 0.95F, 3.02F, Math.min(0.85F, alpha + 0.2F));
        if (breaking && breakProgress > 0.0F) {
            renderCrystalShards(matrix, consumer, age, breakProgress);
        }
        poseStack.popPose();
    }

    private static void renderCrystalShell(Matrix4f matrix, VertexConsumer consumer, float radius, float halfHeight, float alpha) {
        triQuadDoubleSided(matrix, consumer, 0.0F, halfHeight, 0.0F, radius, 0.0F, 0.0F, 0.0F, 0.0F, radius, 0.62F, 0.88F, 1.0F, alpha);
        triQuadDoubleSided(matrix, consumer, 0.0F, halfHeight, 0.0F, 0.0F, 0.0F, radius, -radius, 0.0F, 0.0F, 0.74F, 0.58F, 1.0F, alpha);
        triQuadDoubleSided(matrix, consumer, 0.0F, halfHeight, 0.0F, -radius, 0.0F, 0.0F, 0.0F, 0.0F, -radius, 0.62F, 0.88F, 1.0F, alpha);
        triQuadDoubleSided(matrix, consumer, 0.0F, halfHeight, 0.0F, 0.0F, 0.0F, -radius, radius, 0.0F, 0.0F, 0.74F, 0.58F, 1.0F, alpha);
        triQuadDoubleSided(matrix, consumer, 0.0F, -halfHeight, 0.0F, 0.0F, 0.0F, radius, radius, 0.0F, 0.0F, 0.48F, 0.78F, 1.0F, alpha);
        triQuadDoubleSided(matrix, consumer, 0.0F, -halfHeight, 0.0F, -radius, 0.0F, 0.0F, 0.0F, 0.0F, radius, 0.62F, 0.48F, 1.0F, alpha);
        triQuadDoubleSided(matrix, consumer, 0.0F, -halfHeight, 0.0F, 0.0F, 0.0F, -radius, -radius, 0.0F, 0.0F, 0.48F, 0.78F, 1.0F, alpha);
        triQuadDoubleSided(matrix, consumer, 0.0F, -halfHeight, 0.0F, radius, 0.0F, 0.0F, 0.0F, 0.0F, -radius, 0.62F, 0.48F, 1.0F, alpha);
    }

    private static void renderCrystalEdges(Matrix4f matrix, VertexConsumer consumer, float radius, float halfHeight, float alpha) {
        float edge = 0.018F;
        renderCollapseRing(matrix, consumer, 0.0F, radius, 0.0F, alpha * 0.65F);
        quad(matrix, consumer, -edge, halfHeight, -edge, edge, halfHeight, edge, radius, 0.0F, edge, radius, 0.0F, -edge, 0.8F, 0.96F, 1.0F, alpha);
        quad(matrix, consumer, -edge, halfHeight, -edge, edge, halfHeight, edge, -radius, 0.0F, edge, -radius, 0.0F, -edge, 0.8F, 0.96F, 1.0F, alpha);
        quad(matrix, consumer, -edge, -halfHeight, -edge, edge, -halfHeight, edge, radius, 0.0F, edge, radius, 0.0F, -edge, 0.8F, 0.96F, 1.0F, alpha);
        quad(matrix, consumer, -edge, -halfHeight, -edge, edge, -halfHeight, edge, -radius, 0.0F, edge, -radius, 0.0F, -edge, 0.8F, 0.96F, 1.0F, alpha);
    }

    private static void renderCrystalShards(Matrix4f matrix, VertexConsumer consumer, float age, float breakProgress) {
        for (int i = 0; i < 14; i++) {
            float angle = i * 2.399963F + age * 0.04F;
            float radius = 0.58F + breakProgress * (0.75F + (i % 4) * 0.16F);
            float y = -0.45F + (i % 7) * 0.18F + breakProgress * (0.45F + (i % 3) * 0.18F);
            float x = Mth.cos(angle) * radius;
            float z = Mth.sin(angle) * radius;
            float size = 0.05F + (i % 3) * 0.018F;
            float alpha = 0.5F * (1.0F - breakProgress);
            quad(matrix, consumer,
                    x, y + size, z,
                    x + Mth.cos(angle + 1.2F) * size, y, z + Mth.sin(angle + 1.2F) * size,
                    x, y - size, z,
                    x - Mth.cos(angle + 1.2F) * size, y, z - Mth.sin(angle + 1.2F) * size,
                    0.62F, 0.9F, 1.0F, alpha);
        }
    }

    private void renderDefeatDissolve(MagicalGirlBossEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        float age = entity.getPhaseTicks() + partialTicks;
        float collapse = entity.getVisualPhase() == MagicalGirlPhase.FLASHBACK || entity.getVisualPhase() == MagicalGirlPhase.END_HOOK
                ? Mth.clamp(age / 260.0F, 0.0F, 1.0F)
                : Mth.clamp(age / 150.0F, 0.0F, 1.0F);
        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());

        poseStack.pushPose();
        poseStack.translate(0.0D, 1.0D, 0.0D);
        Matrix4f matrix = poseStack.last().pose();
        float alpha = 0.46F * (1.0F - collapse * 0.45F);
        renderCollapseRing(matrix, consumer, age, 0.62F + collapse * 0.22F, 0.05F, alpha);
        renderCollapseRing(matrix, consumer, -age * 0.72F, 1.05F - collapse * 0.28F, 0.95F, alpha * 0.75F);
        renderMemoryShards(matrix, consumer, age, collapse);
        poseStack.popPose();
    }

    private static void renderCollapseRing(Matrix4f matrix, VertexConsumer consumer, float rotation, float radius, float y, float alpha) {
        int segments = 28;
        float thickness = 0.015F;
        for (int i = 0; i < segments; i++) {
            if (i % 4 == 1) {
                continue;
            }
            float a0 = ((float) i / segments) * Mth.TWO_PI + rotation * 0.035F;
            float a1 = ((float) (i + 1) / segments) * Mth.TWO_PI + rotation * 0.035F;
            float x0 = Mth.cos(a0) * radius;
            float z0 = Mth.sin(a0) * radius;
            float x1 = Mth.cos(a1) * radius;
            float z1 = Mth.sin(a1) * radius;
            quad(matrix, consumer, x0, y - thickness, z0, x1, y - thickness, z1, x1, y + thickness, z1, x0, y + thickness, z0, 0.45F, 0.72F, 1.0F, alpha);
        }
    }

    private static void renderMemoryShards(Matrix4f matrix, VertexConsumer consumer, float age, float collapse) {
        int shards = 10;
        for (int i = 0; i < shards; i++) {
            float base = i * 2.399963F + age * 0.018F;
            float radius = 0.38F + (i % 4) * 0.18F + collapse * 0.5F;
            float y = 0.15F + (i % 5) * 0.34F - collapse * 0.22F;
            float x = Mth.cos(base) * radius;
            float z = Mth.sin(base) * radius;
            float tiltX = Mth.cos(base + 1.7F) * (0.08F + collapse * 0.08F);
            float tiltZ = Mth.sin(base + 1.7F) * (0.08F + collapse * 0.08F);
            float height = 0.18F + (i % 3) * 0.05F;
            float width = 0.018F;
            float alpha = 0.34F * (1.0F - collapse * 0.25F);
            quad(matrix, consumer,
                    x - width, y, z - width,
                    x + width, y, z + width,
                    x + tiltX + width, y + height, z + tiltZ + width,
                    x + tiltX - width, y + height, z + tiltZ - width,
                    0.92F, 0.54F, 1.0F, alpha);
        }
    }

    private static void quad(Matrix4f matrix, VertexConsumer consumer, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float red, float green, float blue, float alpha) {
        consumer.vertex(matrix, x0, y0, z0).color(red, green, blue, alpha).endVertex();
        consumer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        consumer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        consumer.vertex(matrix, x3, y3, z3).color(red, green, blue, alpha).endVertex();
    }

    private static void vertex(Matrix4f matrix, VertexConsumer consumer, float x, float y, float z, float red, float green, float blue, float alpha) {
        consumer.vertex(matrix, x, y, z).color(red, green, blue, alpha).endVertex();
    }

    private static void triQuadDoubleSided(Matrix4f matrix, VertexConsumer consumer, float x0, float y0, float z0, float x1, float y1, float z1, float x2, float y2, float z2, float red, float green, float blue, float alpha) {
        vertex(matrix, consumer, x0, y0, z0, red, green, blue, alpha);
        vertex(matrix, consumer, x1, y1, z1, red, green, blue, alpha);
        vertex(matrix, consumer, x2, y2, z2, red, green, blue, alpha);
        vertex(matrix, consumer, x2, y2, z2, red, green, blue, alpha);
        vertex(matrix, consumer, x0, y0, z0, red, green, blue, alpha);
        vertex(matrix, consumer, x2, y2, z2, red, green, blue, alpha);
        vertex(matrix, consumer, x1, y1, z1, red, green, blue, alpha);
        vertex(matrix, consumer, x1, y1, z1, red, green, blue, alpha);
    }

    private static final class MagicalGirlGeoModel extends GeoModel<MagicalGirlBossEntity> {
        private static final ResourceLocation MODEL = Meatwo310.loc("geo/magical_girl.geo.json");
        private static final ResourceLocation TEXTURE = Meatwo310.loc("textures/entity/magical_girl.png");
        private static final ResourceLocation ANIMATION = Meatwo310.loc("animations/magical_girl.animation.json");

        @Override
        public ResourceLocation getModelResource(MagicalGirlBossEntity animatable) {
            return MODEL;
        }

        @Override
        public ResourceLocation getTextureResource(MagicalGirlBossEntity animatable) {
            return TEXTURE;
        }

        @Override
        public ResourceLocation getAnimationResource(MagicalGirlBossEntity animatable) {
            return ANIMATION;
        }
    }
}
