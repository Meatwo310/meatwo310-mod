package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.ChemicalAreaEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ChemicalAreaRenderer extends EntityRenderer<ChemicalAreaEntity> {
    private static final ResourceLocation TEXTURE = Meatwo310.loc("textures/particle/magical_girl/gas_green_ash.png");

    public ChemicalAreaRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull ChemicalAreaEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        float radius = entity.getRenderRadius();
        float alpha = 90.0F + entity.getWarmupProgress(partialTicks) * 90.0F;
        MagicalRenderUtil.renderQuad(poseStack, buffer, TEXTURE, packedLight, radius, 125, 255, 35, (int) alpha);
        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ChemicalAreaEntity entity) {
        return TEXTURE;
    }
}
