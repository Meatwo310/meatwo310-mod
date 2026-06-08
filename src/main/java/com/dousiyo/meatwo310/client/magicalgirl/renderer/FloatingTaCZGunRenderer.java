package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.FloatingTaCZGunEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class FloatingTaCZGunRenderer extends EntityRenderer<FloatingTaCZGunEntity> {
    private static final ResourceLocation TEXTURE = Meatwo310.loc("textures/particle/magical_girl/small_mote.png");

    public FloatingTaCZGunRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull FloatingTaCZGunEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        Vec3 aimTarget = clientAimTarget(entity, partialTicks);
        Vec3 visualDir = aimTarget == null ? Vec3.directionFromRotation(entity.getRenderPitch(), entity.getRenderYaw()) : aimTarget.subtract(entity.position()).normalize();
        if (visualDir.lengthSqr() < 0.001D) {
            visualDir = Vec3.directionFromRotation(entity.getRenderPitch(), entity.getRenderYaw());
        }
        float yaw = (float) Math.toDegrees(Math.atan2(-visualDir.z, visualDir.x));
        float pitch = (float) Math.toDegrees(Math.asin(visualDir.y));

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));
        poseStack.scale(1.15F, 1.15F, 1.15F);
        Minecraft.getInstance().getItemRenderer().renderStatic(
                entity.createGunStack(),
                ItemDisplayContext.FIXED,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId()
        );
        poseStack.popPose();

        if (entity.getGunState() == 3) {
            poseStack.pushPose();
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            MagicalRenderUtil.renderQuad(poseStack, buffer, TEXTURE, packedLight, 0.55F, 255, 190, 90, 210);
            poseStack.popPose();
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private static Vec3 clientAimTarget(FloatingTaCZGunEntity entity, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Player localPlayer = minecraft.player;
        if (localPlayer != null && localPlayer.isAlive()) {
            return interpolatedBodyPosition(localPlayer, partialTicks);
        }
        if (entity.getTargetEntityId() < 0) {
            return null;
        }
        Entity target = entity.level().getEntity(entity.getTargetEntityId());
        return target == null ? null : interpolatedBodyPosition(target, partialTicks);
    }

    private static Vec3 interpolatedBodyPosition(Entity target, float partialTicks) {
        double x = target.xOld + (target.getX() - target.xOld) * partialTicks;
        double y = target.yOld + (target.getY() - target.yOld) * partialTicks + target.getBbHeight() * 0.55D;
        double z = target.zOld + (target.getZ() - target.zOld) * partialTicks;
        return new Vec3(x, y, z);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull FloatingTaCZGunEntity entity) {
        return TEXTURE;
    }
}
