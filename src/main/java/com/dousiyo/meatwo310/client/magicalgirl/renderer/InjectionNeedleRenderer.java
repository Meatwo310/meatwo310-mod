package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.InjectionNeedleProjectileEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class InjectionNeedleRenderer extends EntityRenderer<InjectionNeedleProjectileEntity> {
    private static final ResourceLocation TEXTURE = Meatwo310.loc("textures/particle/magical_girl/small_mote.png");

    public InjectionNeedleRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull InjectionNeedleProjectileEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        Vec3 motion = entity.getDeltaMovement();
        if (motion.lengthSqr() > 0.001D) {
            poseStack.mulPose(Axis.YP.rotationDegrees((float) (Math.atan2(motion.x, motion.z) * 180.0D / Math.PI)));
            poseStack.mulPose(Axis.XP.rotationDegrees((float) (Math.atan2(motion.y, Math.sqrt(motion.x * motion.x + motion.z * motion.z)) * -180.0D / Math.PI)));
        }
        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();
        consumer.vertex(matrix, 0.0F, 0.0F, -0.55F).color(170, 255, 220, 230).endVertex();
        consumer.vertex(matrix, 0.0F, 0.0F, 0.55F).color(255, 255, 255, 230).endVertex();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull InjectionNeedleProjectileEntity entity) {
        return TEXTURE;
    }
}
