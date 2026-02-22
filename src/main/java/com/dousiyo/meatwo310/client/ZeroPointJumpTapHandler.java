package com.dousiyo.meatwo310.client;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.registry.ModEffects;
import com.dousiyo.meatwo310.network.BlinkTeleportC2SPacket;
import com.dousiyo.meatwo310.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ZeroPointJumpTapHandler {
    private static final int DOUBLE_TAP_WINDOW_TICKS = 8;

    private static long lastTapGameTime = -1L;

    private ZeroPointJumpTapHandler() {}

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            lastTapGameTime = -1L;
            return;
        }

        if (event.getAction() != GLFW.GLFW_PRESS) {
            return;
        }
        if (mc.screen != null) return;
        if (event.getKey() != mc.options.keyJump.getKey().getValue()) return;

        if (!mc.player.hasEffect(ModEffects.BLINK.get())) {
            return;
        }

        long now = mc.level.getGameTime();
        long delta = now - lastTapGameTime;
        if (lastTapGameTime >= 0L && delta > 0L && delta <= DOUBLE_TAP_WINDOW_TICKS) {
            ModNetwork.CHANNEL.sendToServer(new BlinkTeleportC2SPacket());
            lastTapGameTime = -1L;
        } else {
            lastTapGameTime = now;
        }
    }
}
