package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicalHazardEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class MagicalHazardRenderer extends EntityRenderer<MagicalHazardEntity> {
    private static final ResourceLocation SUMMON_CIRCLE = Meatwo310.loc("textures/magic_circle/summon_circle.png");
    private static final ResourceLocation LASER_CIRCLE = Meatwo310.loc("textures/magic_circle/laser_circle.png");
    private static final ResourceLocation PHASEBREAK_CIRCLE = Meatwo310.loc("textures/magic_circle/phasebreak_circle.png");
    private static final ResourceLocation SMALL_MOTE = Meatwo310.loc("textures/particle/magical_girl/small_mote.png");

    public MagicalHazardRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull MagicalHazardEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        int variant = entity.getRenderVariant();
        float radius = entity.getRenderRadius();
        float progress = entity.getWarmupProgress(partialTicks);

        if (variant != MagicalHazardEntity.VARIANT_YELLOW_IGNITION) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 0.04D, 0.0D);
            poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) * (variant == 2 ? -3.0F : 2.0F)));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            MagicalRenderUtil.renderQuad(poseStack, buffer, circleTexture(variant), packedLight, radius, 255, 255, 255, 255);
            poseStack.popPose();
        }

        renderMoteCloud(entity, partialTicks, poseStack, buffer, packedLight, variant, radius, progress);

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderMoteCloud(MagicalHazardEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int variant, float radius, float progress) {
        int count = variant == MagicalHazardEntity.VARIANT_YELLOW_IGNITION || variant == MagicalHazardEntity.VARIANT_RADIATION ? 64 : 24;
        int[] base = moteColor(variant, false);
        int[] accent = moteColor(variant, true);
        float age = entity.tickCount + partialTicks;
        float spread = variant == MagicalHazardEntity.VARIANT_YELLOW_IGNITION ? radius * 0.92F : radius * 0.82F;

        for (int i = 0; i < count; i++) {
            double seed = i * 12.9898D + variant * 78.233D;
            double ring = 0.18D + pseudo(seed) * 0.82D;
            double angle = Math.PI * 2.0D * pseudo(seed + 31.7D) + age * (0.008D + pseudo(seed + 4.1D) * 0.012D);
            double x = Math.cos(angle) * spread * ring;
            double z = Math.sin(angle) * spread * ring;
            double y = 0.22D + pseudo(seed + 9.4D) * 1.25D + Math.sin(age * 0.045D + i) * 0.08D;
            float size = 0.07F + (float) pseudo(seed + 17.2D) * 0.09F;
            if (variant == MagicalHazardEntity.VARIANT_YELLOW_IGNITION || variant == MagicalHazardEntity.VARIANT_RADIATION) {
                size += 0.04F;
            }
            int alpha = (int) (70 + 115 * progress + pseudo(seed + 3.3D) * 35);
            int[] color = i % 3 == 0 ? accent : base;

            poseStack.pushPose();
            poseStack.translate(x, y, z);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            poseStack.mulPose(Axis.ZP.rotationDegrees((float) (age * 2.2F + i * 23.0F)));
            MagicalRenderUtil.renderQuad(poseStack, buffer, SMALL_MOTE, packedLight, size, color[0], color[1], color[2], Math.min(220, alpha));
            poseStack.popPose();
        }
    }

    private static double pseudo(double value) {
        double mixed = Math.sin(value) * 43758.5453D;
        return mixed - Math.floor(mixed);
    }

    private int[] moteColor(int variant, boolean accent) {
        if (variant == MagicalHazardEntity.VARIANT_YELLOW_IGNITION) return accent ? new int[]{255, 128, 24} : new int[]{255, 218, 58};
        if (variant == MagicalHazardEntity.VARIANT_RADIATION) return accent ? new int[]{35, 190, 62} : new int[]{22, 138, 48};
        if (variant == 0) return accent ? new int[]{255, 110, 205} : new int[]{250, 166, 236};
        if (variant == 1) return accent ? new int[]{65, 38, 98} : new int[]{142, 78, 210};
        return accent ? new int[]{255, 96, 40} : new int[]{126, 190, 118};
    }

    private ResourceLocation circleTexture(int variant) {
        if (variant == 0) return SUMMON_CIRCLE;
        if (variant == 1) return LASER_CIRCLE;
        return PHASEBREAK_CIRCLE;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MagicalHazardEntity entity) {
        return SMALL_MOTE;
    }
}
