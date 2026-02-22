package com.dousiyo.meatwo310.client;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.client.hud.KillMessageHud;
import com.dousiyo.meatwo310.network.KillMessageS2CPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEventsModBus {
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent e) {
        KillMessageS2CPacket.CLIENT_HANDLER = KillMessageHud::show;
        e.registerAboveAll("kill_message", (gui, g, partialTick, w, h) -> {
            KillMessageHud.render(g, w, h);
        });
    }
}
