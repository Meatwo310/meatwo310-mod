package com.dousiyo.meatwo310.client.renderer;

import com.dousiyo.meatwo310.entity.ConversationNpcEntity;
import com.dousiyo.meatwo310.Meatwo310;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ConversationNpcRenderer extends HumanoidMobRenderer<ConversationNpcEntity, PlayerModel<ConversationNpcEntity>> {
    private static final ResourceLocation TEXTURE = Meatwo310.loc("textures/entity/meatwo310.png");

    public ConversationNpcRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER_SLIM), true), 0.5F);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull ConversationNpcEntity entity) {
        return TEXTURE;
    }
}
