package com.dousiyo.meatwo310.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class KnockbackExplosion {

    public static void create(Level level, Entity source, double x, double y, double z, float radius) {
        if (level.isClientSide) {
            return;
        }

        Vec3 center = new Vec3(x, y, z);

        AABB aabb = new AABB(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );

        List<Entity> entities = level.getEntities(null, aabb);

        for (Entity entity : entities) {
            if (!(entity instanceof LivingEntity)) {
                continue;
            }

            Vec3 entityPos = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
            double distance = center.distanceTo(entityPos);

            if (distance > radius) {
                continue;
            }

            double normalizedDistance = distance / radius;

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
        }

        level.explode(
                source,
                x, y, z,
                0.0F,
                Level.ExplosionInteraction.NONE
        );
    }
}
