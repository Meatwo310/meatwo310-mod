package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.RibbonJudgementEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class RibbonJudgementRenderer extends EntityRenderer<RibbonJudgementEntity> {
    private static final ResourceLocation CIRCLE = Meatwo310.loc("textures/magic_circle/summon_circle.png");
    private static final ResourceLocation STRIP = Meatwo310.loc("textures/particle/magical_girl/small_mote.png");

    public RibbonJudgementRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull RibbonJudgementEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        int life = entity.getLife();
        boolean redLaser = entity.isRedLaser();
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.04D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) * 2.4F));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        MagicalRenderUtil.renderQuad(poseStack, buffer, CIRCLE, packedLight, redLaser ? 1.45F : 1.1F, 255, redLaser ? 45 : 170, redLaser ? 55 : 230, 220);
        poseStack.popPose();

        float rise = Math.min(1.0F, (life + partialTicks) / 35.0F);
        int segments = Math.max(3, (int) (18 * rise));
        for (int i = 0; i < segments; i++) {
            double t = i / 17.0D;
            double angle = t * Math.PI * 5.4D + (entity.tickCount + partialTicks) * 0.08D;
            poseStack.pushPose();
            poseStack.translate(Math.cos(angle) * 0.55D, t * 2.05D, Math.sin(angle) * 0.55D);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            MagicalRenderUtil.renderQuad(poseStack, buffer, STRIP, packedLight, redLaser ? 0.16F : 0.12F, 255, redLaser ? 30 : 76, redLaser ? 42 : 184, 220);
            poseStack.popPose();
        }
        renderBeam(entity, partialTicks, poseStack, buffer);
        if (life >= (redLaser ? 56 : 70) && life <= (redLaser ? 132 : 110)) {
            poseStack.pushPose();
            poseStack.translate(0.0D, 1.1D, 0.0D);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            MagicalRenderUtil.renderQuad(poseStack, buffer, STRIP, packedLight, life >= (redLaser ? 70 : 86) ? 1.0F : 0.45F, 255, redLaser ? 50 : 255, redLaser ? 50 : 255, life >= (redLaser ? 70 : 86) ? 210 : 140);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void renderBeam(RibbonJudgementEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        float alpha = entity.getBeamAlpha(partialTicks);
        if (alpha <= 0.0F) {
            return;
        }
        Entity caster = entity.level().getEntity(entity.getCasterEntityId());
        Entity target = entity.level().getEntity(entity.getTargetEntityId());
        if (caster == null || target == null) {
            return;
        }
        Vec3 entityPos = interpolatedPosition(entity, partialTicks);
        Vec3 start = interpolatedPosition(caster, partialTicks).add(0.0D, caster.getBbHeight() * 0.82D, 0.0D).subtract(entityPos);
        Vec3 end = interpolatedPosition(target, partialTicks).add(0.0D, target.getBbHeight() * 0.75D, 0.0D).subtract(entityPos);
        Vec3 dir = end.subtract(start);
        if (dir.lengthSqr() < 0.001D) {
            return;
        }
        dir = dir.normalize();
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().subtract(entityPos).subtract(start);
        Vec3 side = dir.cross(camera);
        if (side.lengthSqr() < 0.001D) {
            side = dir.cross(new Vec3(0.0D, 1.0D, 0.0D));
        }
        if (side.lengthSqr() < 0.001D) {
            side = new Vec3(1.0D, 0.0D, 0.0D);
        }
        side = side.normalize();

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();
        int outerAlpha = (int) (180 * alpha);
        int innerAlpha = (int) (245 * alpha);
        renderBeamQuad(matrix, consumer, start, end, side.scale(entity.isRedLaser() ? 0.18D : 0.11D), 255, entity.isRedLaser() ? 32 : 230, entity.isRedLaser() ? 32 : 255, outerAlpha);
        renderBeamQuad(matrix, consumer, start, end, side.scale(entity.isRedLaser() ? 0.055D : 0.035D), 255, 245, 245, innerAlpha);
    }

    private static Vec3 interpolatedPosition(Entity entity, float partialTicks) {
        double x = entity.xOld + (entity.getX() - entity.xOld) * partialTicks;
        double y = entity.yOld + (entity.getY() - entity.yOld) * partialTicks;
        double z = entity.zOld + (entity.getZ() - entity.zOld) * partialTicks;
        return new Vec3(x, y, z);
    }

    private static void renderBeamQuad(Matrix4f matrix, VertexConsumer consumer, Vec3 start, Vec3 end, Vec3 side, int red, int green, int blue, int alpha) {
        vertex(matrix, consumer, start.add(side), red, green, blue, alpha);
        vertex(matrix, consumer, end.add(side), red, green, blue, alpha);
        vertex(matrix, consumer, end.subtract(side), red, green, blue, alpha);
        vertex(matrix, consumer, start.subtract(side), red, green, blue, alpha);
    }

    private static void vertex(Matrix4f matrix, VertexConsumer consumer, Vec3 pos, int red, int green, int blue, int alpha) {
        consumer.vertex(matrix, (float) pos.x, (float) pos.y, (float) pos.z).color(red, green, blue, alpha).endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RibbonJudgementEntity entity) {
        return STRIP;
    }
}
