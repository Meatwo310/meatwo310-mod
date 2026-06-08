package com.dousiyo.meatwo310.client.magicalgirl.renderer;

import com.dousiyo.meatwo310.Meatwo310;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Phantom;

public class MagicalPhantomRenderer extends PhantomRenderer {
    private static final ResourceLocation TEXTURE = Meatwo310.loc("textures/entity/magical_phantom.png");

    public MagicalPhantomRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Phantom phantom) {
        return TEXTURE;
    }
}
