package com.dousiyo.meatwo310.magicalgirl;

import com.atsuishio.superbwarfare.entity.projectile.ProjectileEntity;
import com.atsuishio.superbwarfare.init.ModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

import net.minecraft.world.effect.MobEffectInstance;

public final class Phase3ProjectileUtil {
    private Phase3ProjectileUtil() {
    }

    public static Entity spawnSuperbProjectile(
            ServerLevel level,
            LivingEntity shooter,
            Vec3 position,
            Vec3 direction,
            float damage,
            float velocity,
            float spread,
            float gravity
    ) {
        return spawnSuperbProjectile(level, shooter, position, direction, damage, velocity, spread, gravity, null, 0.0F, false);
    }

    public static Entity spawnSuperbProjectile(
            ServerLevel level,
            LivingEntity shooter,
            Vec3 position,
            Vec3 direction,
            float damage,
            float velocity,
            float spread,
            float gravity,
            ArrayList<MobEffectInstance> effects,
            float knockback,
            boolean penetrating
    ) {
        Entity entity = ModEntities.PROJECTILE.get().create(level);
        if (entity == null || direction.lengthSqr() < 0.0001D) {
            return null;
        }

        Vec3 shotDir = addSpread(direction.normalize(), level, spread);
        entity.setPos(position.x, position.y, position.z);
        if (entity instanceof Projectile projectile) {
            projectile.setOwner(shooter);
        }
        if (entity instanceof ProjectileEntity projectile) {
            projectile.shooter(shooter)
                    .damage(damage)
                    .velocity(velocity)
                    .bypassArmorRate(0.0F)
                    .knockback(knockback)
                    .setPenetrating(penetrating);
            projectile.setGravity(gravity);
            if (effects != null && !effects.isEmpty()) {
                projectile.effect(effects);
            }
        }
        entity.setDeltaMovement(shotDir.scale(velocity));
        level.addFreshEntity(entity);
        return entity;
    }

    private static Vec3 addSpread(Vec3 direction, ServerLevel level, float spread) {
        if (spread <= 0.0F) {
            return direction;
        }
        double amount = spread * 0.0125D;
        return direction.add(
                (level.random.nextDouble() - 0.5D) * amount,
                (level.random.nextDouble() - 0.5D) * amount,
                (level.random.nextDouble() - 0.5D) * amount
        ).normalize();
    }
}
