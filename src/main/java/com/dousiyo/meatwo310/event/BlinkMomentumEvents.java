package com.dousiyo.meatwo310.event;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.network.BlinkTeleportC2SPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BlinkMomentumEvents {
    private BlinkMomentumEvents() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (!(event.player instanceof ServerPlayer player)) return;
        BlinkTeleportC2SPacket.applyMomentumResetIfDue(player);
    }
}
