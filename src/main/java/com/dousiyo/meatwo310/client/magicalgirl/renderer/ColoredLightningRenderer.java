package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.magicalgirl.entity.ColoredLightningEntity;
import com.dousiyo.meatwo310.config.ServerConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class ColoredLightningRenderer extends EntityRenderer<ColoredLightningEntity> {
    private static final ResourceLocation EMPTY = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/unknown_pack.png");

    public ColoredLightningRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull ColoredLightningEntity entity, float entityYaw, float partialTicks, @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        float[] offsetsX = new float[8];
        float[] offsetsZ = new float[8];
        float x = 0.0F;
        float z = 0.0F;
        RandomSource random = RandomSource.create(entity.getBoltSeed());

        for (int i = 7; i >= 0; --i) {
            offsetsX[i] = x;
            offsetsZ[i] = z;
            x += (float) (random.nextInt(11) - 5) * 0.16F;
            z += (float) (random.nextInt(11) - 5) * 0.16F;
        }

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        Matrix4f matrix = poseStack.last().pose();
        float segmentHeight = entity.getLightningHeight() / 8.0F;

        int layers = Math.max(1, Math.min(4, ServerConfig.MAGICAL_GIRL_LIGHTNING_RENDER_LAYERS.get()));
        int branches = Math.max(1, Math.min(3, ServerConfig.MAGICAL_GIRL_LIGHTNING_RENDER_BRANCHES.get()));
        for (int layer = 0; layer < layers; ++layer) {
            RandomSource layerRandom = RandomSource.create(entity.getBoltSeed());
            for (int branch = 0; branch < branches; ++branch) {
                int start = 7;
                int end = 0;
                if (branch > 0) {
                    start = 7 - branch;
                    end = start - 2;
                }

                float nextX = offsetsX[start] - x;
                float nextZ = offsetsZ[start] - z;
                for (int index = start; index >= end; --index) {
                    float prevX = nextX;
                    float prevZ = nextZ;
                    if (branch == 0) {
                        nextX += (float) (layerRandom.nextInt(11) - 5) * 0.16F;
                        nextZ += (float) (layerRandom.nextInt(11) - 5) * 0.16F;
                    } else {
                        nextX += (float) (layerRandom.nextInt(31) - 15) * 0.1F;
                        nextZ += (float) (layerRandom.nextInt(31) - 15) * 0.1F;
                    }

                    float outer = 0.05F + layer * 0.09F;
                    float inner = 0.05F + layer * 0.09F;
                    if (branch == 0) {
                        outer *= index * 0.1F + 1.0F;
                        inner *= ((float) index - 1.0F) * 0.1F + 1.0F;
                    }

                    quad(matrix, consumer, nextX, nextZ, index, prevX, prevZ, segmentHeight, outer, inner, entity.getRed(), entity.getGreen(), entity.getBlue(), false, false, true, false);
                    quad(matrix, consumer, nextX, nextZ, index, prevX, prevZ, segmentHeight, outer, inner, entity.getRed(), entity.getGreen(), entity.getBlue(), true, false, true, true);
                    quad(matrix, consumer, nextX, nextZ, index, prevX, prevZ, segmentHeight, outer, inner, entity.getRed(), entity.getGreen(), entity.getBlue(), true, true, false, true);
                    quad(matrix, consumer, nextX, nextZ, index, prevX, prevZ, segmentHeight, outer, inner, entity.getRed(), entity.getGreen(), entity.getBlue(), false, true, false, false);
                }
            }
        }
    }

    private static void quad(Matrix4f matrix, VertexConsumer consumer, float x1, float z1, int index, float x2, float z2, float segmentHeight, float outer, float inner, float red, float green, float blue, boolean sideA, boolean sideB, boolean sideC, boolean sideD) {
        consumer.vertex(matrix, x1 + (sideA ? inner : -inner), index * segmentHeight, z1 + (sideB ? inner : -inner)).color(red, green, blue, 0.3F).endVertex();
        consumer.vertex(matrix, x2 + (sideA ? outer : -outer), (index + 1) * segmentHeight, z2 + (sideB ? outer : -outer)).color(red, green, blue, 0.3F).endVertex();
        consumer.vertex(matrix, x2 + (sideC ? outer : -outer), (index + 1) * segmentHeight, z2 + (sideD ? outer : -outer)).color(red, green, blue, 0.3F).endVertex();
        consumer.vertex(matrix, x1 + (sideC ? inner : -inner), index * segmentHeight, z1 + (sideD ? inner : -inner)).color(red, green, blue, 0.3F).endVertex();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ColoredLightningEntity entity) {
        return EMPTY;
    }
}
