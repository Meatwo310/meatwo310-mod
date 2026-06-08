package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.RuneCageEntity;
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

public class RuneCageRenderer extends EntityRenderer<RuneCageEntity> {
    private static final ResourceLocation RUNE = Meatwo310.loc("textures/particle/magical_girl/small_mote.png");
    private static final ResourceLocation LINE = Meatwo310.loc("textures/magic_circle/phasebreak_circle.png");

    public RuneCageRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull RuneCageEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        float progress = entity.getProgress(partialTicks);
        double radius = 3.0D * progress;
        for (int i = 0; i < 6; i++) {
            double angle = Math.PI * 2.0D * i / 6.0D + (entity.tickCount + partialTicks) * 0.01D;
            poseStack.pushPose();
            poseStack.translate(Math.cos(angle) * radius, 1.1D, Math.sin(angle) * radius);
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            MagicalRenderUtil.renderQuad(poseStack, buffer, RUNE, packedLight, 0.32F, 255, 225, 120, 230);
            poseStack.popPose();
        }
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.08D, 0.0D);
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        MagicalRenderUtil.renderQuad(poseStack, buffer, LINE, packedLight, (float) radius, 190, 104, 255, 135);
        poseStack.popPose();
        renderBeam(entity, partialTicks, poseStack, buffer);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static void renderBeam(RuneCageEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer) {
        float alpha = Math.max(0.0F, Math.min(1.0F, entity.getBeamAlpha(partialTicks)));
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
        renderBeamQuad(matrix, consumer, start, end, side.scale(0.14D), 190, 104, 255, (int) (185 * alpha));
        renderBeamQuad(matrix, consumer, start, end, side.scale(0.045D), 255, 245, 255, (int) (245 * alpha));
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
    public @NotNull ResourceLocation getTextureLocation(@NotNull RuneCageEntity entity) {
        return RUNE;
    }
}
