package com.dousiyo.meatwo310.client;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.client.hud.KillMessageHud;
import com.dousiyo.meatwo310.network.CountdownHudS2CPacket;
import com.dousiyo.meatwo310.network.KillMessageS2CPacket;
import com.dousiyo.meatwo310.network.TimerHudUpdateS2CPacket;
import com.dousiyo.meatwo310.timer.client.ClientCountdownState;
import com.dousiyo.meatwo310.timer.client.ClientTimerState;
import com.dousiyo.meatwo310.timer.client.CountdownHudOverlay;
import com.dousiyo.meatwo310.timer.client.TimerHudOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEventsModBus {
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent e) {
        KillMessageS2CPacket.CLIENT_HANDLER =
                msg -> KillMessageHud.show(msg.victimName(), msg.hasVictimColor(), msg.victimColor());
        TimerHudUpdateS2CPacket.CLIENT_HANDLER = ClientTimerState::apply;
        CountdownHudS2CPacket.CLIENT_HANDLER = ClientCountdownState::apply;

        e.registerAboveAll("kill_message", (gui, g, partialTick, w, h) -> {
            KillMessageHud.render(g, w, h);
        });
        e.registerAboveAll("countdown_hud", (gui, g, partialTick, w, h) -> {
            CountdownHudOverlay.render(g, w, h);
        });
        e.registerAboveAll("timer_hud", (gui, g, partialTick, w, h) -> {
            TimerHudOverlay.render(g, w, h);
        });
    }
}
