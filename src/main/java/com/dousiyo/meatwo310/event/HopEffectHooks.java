package com.dousiyo.meatwo310.event;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.config.ServerConfig;
import com.dousiyo.meatwo310.registry.ModEffects;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HopEffectHooks {

    private static final double KNOCKBACK_MULTIPLIER = 2.2;

    private static boolean hasHop(Player player) {
        return player.hasEffect(ModEffects.HOP.get());
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasHop(player)) return;

        event.setDamageMultiplier(0.0F);
        event.setDistance(0.0F);
        player.fallDistance = 0.0F;
    }

    @SubscribeEvent
    public static void onLivingKnockback(LivingKnockBackEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!hasHop(player)) return;

        event.setStrength((float) (event.getStrength() * KNOCKBACK_MULTIPLIER));
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        Explosion explosion = event.getExplosion();

        Map<Player, Vec3> hitPlayers = explosion.getHitPlayers();
        if (hitPlayers.isEmpty()) return;
        int maxAffected = ServerConfig.EXPLOSION_MAX_AFFECTED_ENTITIES.get();
        int affected = 0;

        for (Map.Entry<Player, Vec3> entry : hitPlayers.entrySet()) {
            if (affected >= maxAffected) break;
            Player player = entry.getKey();
            if (!hasHop(player)) continue;

            Vec3 baseKb = entry.getValue();

            Vec3 extra = baseKb.scale(KNOCKBACK_MULTIPLIER - 1.0);

            player.setDeltaMovement(player.getDeltaMovement().add(extra));
            player.hasImpulse = true;

            entry.setValue(baseKb.scale(KNOCKBACK_MULTIPLIER));
            affected++;
        }
    }
}
