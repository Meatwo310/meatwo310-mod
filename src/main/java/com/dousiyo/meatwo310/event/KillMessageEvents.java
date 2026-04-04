package com.dousiyo.meatwo310.event;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.network.KillMessageS2CPacket;
import com.dousiyo.meatwo310.network.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class KillMessageEvents {
    private KillMessageEvents() {}

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer victim)) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof ServerPlayer killer)) {
            return;
        }

        boolean hasVictimColor = false;
        int victimColor = 0;
        if (victim.getTeam() instanceof PlayerTeam team) {
            Integer teamColor = team.getColor().getColor();
            if (teamColor != null) {
                hasVictimColor = true;
                victimColor = teamColor;
            }
        }

        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> killer),
                new KillMessageS2CPacket(victim.getName().getString(), hasVictimColor, victimColor));
    }
}
