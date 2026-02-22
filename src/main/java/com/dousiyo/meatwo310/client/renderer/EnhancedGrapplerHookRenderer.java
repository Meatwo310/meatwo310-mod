package com.dousiyo.meatwo310.client.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.entity.EnhancedGrapplerProjectile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EnhancedGrapplerHookRenderer extends EntityRenderer<EnhancedGrapplerProjectile> {
    private static final ResourceLocation MODEL_LOCATION = Meatwo310.loc("entity/enhanced_grappler_hook");
    private static final ResourceLocation TEXTURE_LOCATION = Meatwo310.loc("textures/entity/enhanced_grappler_hook.png");

    public EnhancedGrapplerHookRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(EnhancedGrapplerProjectile entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(entity.getYRot() - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(entity.getXRot()));

        poseStack.scale(0.5F, 0.5F, 0.5F);
        poseStack.translate(-0.5D, -0.5D, -0.5D);

        Minecraft minecraft = Minecraft.getInstance();
        BakedModel model = minecraft.getModelManager().getModel(MODEL_LOCATION);

        if (model != null && model != minecraft.getModelManager().getMissingModel()) {
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutout(TEXTURE_LOCATION));

            RandomSource randomSource = RandomSource.create();
            for (Direction direction : Direction.values()) {
                randomSource.setSeed(42L);
                List<BakedQuad> quads = model.getQuads(null, direction, randomSource);
                renderQuads(poseStack, vertexConsumer, quads, packedLight);
            }

            randomSource.setSeed(42L);
            List<BakedQuad> unculledQuads = model.getQuads(null, null, randomSource);
            renderQuads(poseStack, vertexConsumer, unculledQuads, packedLight);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void renderQuads(PoseStack poseStack, VertexConsumer consumer, List<BakedQuad> quads, int packedLight) {
        PoseStack.Pose pose = poseStack.last();
        for (BakedQuad quad : quads) {
            consumer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY);
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EnhancedGrapplerProjectile entity) {
        return TEXTURE_LOCATION;
    }
}
