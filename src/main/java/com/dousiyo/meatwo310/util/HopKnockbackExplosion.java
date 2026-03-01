package com.dousiyo.meatwo310.util;

import com.dousiyo.meatwo310.config.ServerConfig;
import com.dousiyo.meatwo310.registry.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class HopKnockbackExplosion {

    public static void create(Level level, Entity source, double x, double y, double z, float radius) {
        if (level.isClientSide) {
            return;
        }

        double effectiveRadius = Math.min(radius, ServerConfig.EXPLOSION_MAX_GRENADE_RADIUS.get());
        double radiusSqr = effectiveRadius * effectiveRadius;
        int maxAffected = ServerConfig.EXPLOSION_MAX_AFFECTED_ENTITIES.get();

        Vec3 center = new Vec3(x, y, z);

        AABB aabb = new AABB(
                x - effectiveRadius, y - effectiveRadius, z - effectiveRadius,
                x + effectiveRadius, y + effectiveRadius, z + effectiveRadius
        );

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb);
        int affected = 0;

        for (LivingEntity entity : entities) {
            if (affected >= maxAffected) {
                break;
            }

            Vec3 entityPos = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
            double distanceSqr = center.distanceToSqr(entityPos);

            if (distanceSqr > radiusSqr) {
                continue;
            }

            double distance = Math.sqrt(distanceSqr);
            double normalizedDistance = distance / effectiveRadius;
            double strength = Math.cos(normalizedDistance * Math.PI * 0.5);

            double minStrength = 0.25;
            double maxStrength = 1.20;
            strength = minStrength + (maxStrength - minStrength) * strength;

            Vec3 knockbackDir = entityPos.subtract(center);
            if (knockbackDir.lengthSqr() < 0.001) {
                knockbackDir = new Vec3(0, 1, 0);
            } else {
                knockbackDir = knockbackDir.normalize();
            }

            double horizontalMultiplier = 1.8;
            double verticalBase = 0.6;

            double knockX = knockbackDir.x * strength * horizontalMultiplier;
            double knockZ = knockbackDir.z * strength * horizontalMultiplier;
            double knockY = knockbackDir.y * strength * 0.9 + verticalBase * strength;

            Vec3 currentMotion = entity.getDeltaMovement();
            entity.setDeltaMovement(currentMotion.add(knockX, knockY, knockZ));

            entity.hurtMarked = true;

            if (entity instanceof Player player) {
                player.addEffect(new MobEffectInstance(ModEffects.HOP.get(), 20 * 5, 0));
            }
            affected++;
        }

        level.explode(
                source,
                x, y, z,
                0.0F,
                Level.ExplosionInteraction.NONE
        );
    }
}
