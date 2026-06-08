package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.GrandSpellNovaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class GrandSpellNovaRenderer extends EntityRenderer<GrandSpellNovaEntity> {
    private static final ResourceLocation CIRCLE = Meatwo310.loc("textures/magic_circle/phasebreak_circle.png");
    private static final ResourceLocation BEAM = Meatwo310.loc("textures/particle/magical_girl/small_mote.png");

    public GrandSpellNovaRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull GrandSpellNovaEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        int life = entity.getLife();
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.05D, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees((entity.tickCount + partialTicks) * 1.6F));
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        MagicalRenderUtil.renderQuad(poseStack, buffer, CIRCLE, packedLight, 8.0F, 255, 230, 130, 180);
        poseStack.popPose();

        if (life >= 95) {
            float size = life >= 105 ? 3.2F : 1.2F + (life - 95) * 0.18F;
            for (int i = 0; i < 8; i++) {
                poseStack.pushPose();
                poseStack.translate(0.0D, i * 0.85D, 0.0D);
                poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
                MagicalRenderUtil.renderQuad(poseStack, buffer, BEAM, packedLight, size, 255, 255, 255, 160);
                poseStack.popPose();
            }
        }
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull GrandSpellNovaEntity entity) {
        return BEAM;
    }
}
