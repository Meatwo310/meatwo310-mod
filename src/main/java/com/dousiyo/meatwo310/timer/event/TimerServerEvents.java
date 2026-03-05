package com.dousiyo.meatwo310.timer.event;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.timer.core.TimerManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class TimerServerEvents {
    private TimerServerEvents() {}

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.getServer() == null) {
            return;
        }
        ServerLevel overworld = event.getServer().overworld();
        if (overworld == null) {
            return;
        }
        TimerManager.get(overworld).serverTick(overworld);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        TimerManager.get(player.serverLevel()).removeAllInstances(player.getUUID());
    }
}
