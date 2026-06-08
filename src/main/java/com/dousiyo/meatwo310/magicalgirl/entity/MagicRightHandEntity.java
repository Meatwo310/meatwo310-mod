package com.dousiyo.meatwo310.magicalgirl.entity;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlDamage;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlLines;
import com.dousiyo.meatwo310.magicalgirl.MagicalGirlPhase;
import com.dousiyo.meatwo310.magicalgirl.Phase3AttackState;
import com.dousiyo.meatwo310.magicalgirl.Phase3ProjectileUtil;
import com.dousiyo.meatwo310.registry.ModEntities;
import com.dousiyo.meatwo310.config.ServerConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class MagicRightHandEntity extends PathfinderMob {
    private int lifetime = 70;
    private boolean slammed;

    public MagicRightHandEntity(EntityType<? extends MagicRightHandEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public MagicRightHandEntity(Level level, double x, double y, double z) {
        this(ModEntities.MAGIC_RIGHT_HAND.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        if (this.level() instanceof ServerLevel serverLevel) {
            int interval = Math.max(1, ServerConfig.MAGICAL_GIRL_PARTICLE_INTERVAL.get());
            if (this.tickCount % interval == 0) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, this.getX(), this.getY() + 1.0D, this.getZ(), 5, 0.8D, 0.8D, 0.8D, 0.02D);
            }
            if (!this.slammed && this.tickCount == 28) {
                this.slammed = true;
                AABB area = this.getBoundingBox().inflate(3.0D, 1.5D, 3.0D);
                for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, area, MagicRightHandEntity::canHit)) {
                    target.hurt(this.damageSources().mobAttack(this), MagicalGirlDamage.MAGIC_RIGHT_HAND);
                    target.knockback(1.4D, this.getX() - target.getX(), this.getZ() - target.getZ());
                }
                serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.3D, this.getZ(), 2, 0.2D, 0.1D, 0.2D, 0.0D);
            }
        }
        if (this.tickCount >= this.lifetime) {
            this.discard();
        }
    }

    private static boolean canHit(LivingEntity entity) {
        return !(entity instanceof MagicalGirlBossEntity)
                && !(entity instanceof MagicRightHandEntity)
                && (!(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator()));
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(@NotNull net.minecraft.world.entity.Entity entity) {
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.lifetime = tag.getInt("Lifetime");
        this.slammed = tag.getBoolean("Slammed");
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Lifetime", this.lifetime);
        tag.putBoolean("Slammed", this.slammed);
    }
}
