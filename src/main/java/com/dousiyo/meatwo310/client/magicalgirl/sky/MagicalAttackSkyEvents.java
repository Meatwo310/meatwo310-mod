package com.dousiyo.meatwo310.client.magicalgirl.sky;

import com.dousiyo.meatwo310.Meatwo310;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, value = Dist.CLIENT)
public final class MagicalAttackSkyEvents {
    private MagicalAttackSkyEvents() {}

    @SubscribeEvent
    public static void onComputeFogColor(ViewportEvent.ComputeFogColor event) {
        if (!(event.getCamera().getEntity().level() instanceof ClientLevel level)) {
            return;
        }
        Vec3 cameraPos = event.getCamera().getPosition();
        float partialTicks = (float) event.getPartialTick();
        event.setRed(MagicalAttackSkyColor.blendRed(level, cameraPos, event.getRed(), partialTicks));
        event.setGreen(MagicalAttackSkyColor.blendGreen(level, cameraPos, event.getGreen(), partialTicks));
        event.setBlue(MagicalAttackSkyColor.blendBlue(level, cameraPos, event.getBlue(), partialTicks));
    }
}
