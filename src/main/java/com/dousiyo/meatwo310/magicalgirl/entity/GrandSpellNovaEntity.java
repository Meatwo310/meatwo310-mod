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
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class GrandSpellNovaEntity extends Entity {
    private int life;

    public GrandSpellNovaEntity(EntityType<? extends GrandSpellNovaEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public GrandSpellNovaEntity(Level level, Vec3 pos) {
        this(ModEntities.GRAND_SPELL_NOVA.get(), level);
        this.setPos(pos);
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        this.life++;
        if (this.level() instanceof ServerLevel serverLevel) {
            tickServer(serverLevel);
        }
        if (this.life > 150) {
            this.discard();
        }
    }

    private void tickServer(ServerLevel serverLevel) {
        if (this.life == 1) {
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.6F, 0.75F);
        }
        if (this.life % 10 == 0 && this.life < 80) {
            double angle = Math.PI * 2.0D * this.life / 80.0D;
            Vec3 pos = this.position().add(Math.cos(angle) * 7.0D, 2.5D, Math.sin(angle) * 7.0D);
            Vec3 dir = this.position().add(0.0D, 1.0D, 0.0D).subtract(pos).normalize().scale(0.55D);
            serverLevel.addFreshEntity(new StellaBurstProjectileEntity(serverLevel, pos, dir, MagicalGirlDamage.GRAND_NOVA_STELLA_BURST));
        }
        if (this.life == 55 || this.life == 70 || this.life == 85) {
            Player target = serverLevel.getNearestPlayer(this.getX(), this.getY(), this.getZ(), 32.0D, false);
            if (target != null) {
                double angle = Math.PI * 2.0D * this.life / 120.0D;
                Vec3 pos = this.position().add(Math.cos(angle) * 4.0D, 2.4D, Math.sin(angle) * 4.0D);
                serverLevel.addFreshEntity(new DiamondSpinnerEntity(serverLevel, pos, target));
            }
        }
        int particleInterval = Math.max(1, ServerConfig.MAGICAL_GIRL_PARTICLE_INTERVAL.get());
        if (this.life < 105) {
            if (this.life % particleInterval == 0) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT, this.getX(), this.getY() + 0.15D, this.getZ(), 9, 8.0D, 0.2D, 8.0D, 0.02D);
            }
            return;
        }
        if (this.life == 105) {
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.5F, 1.25F);
            serverLevel.sendParticles(ParticleTypes.FLASH, this.getX(), this.getY() + 0.5D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        if (this.life >= 105 && this.life <= 125 && this.life % 5 == 0) {
            damageNova(serverLevel);
        }
    }

    private void damageNova(ServerLevel serverLevel) {
        AABB box = this.getBoundingBox().inflate(5.0D, 8.0D, 5.0D);
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, box, GrandSpellNovaEntity::canHit)) {
            double dist = target.position().subtract(this.position()).horizontalDistance();
            if (dist <= 4.6D) {
                target.hurt(this.damageSources().magic(), MagicalGirlDamage.GRAND_NOVA_TICK);
                Vec3 away = target.position().subtract(this.position());
                if (away.lengthSqr() > 0.01D) {
                    away = away.normalize();
                    target.push(away.x * 0.35D, 0.25D, away.z * 0.35D);
                }
            }
        }
    }

    private static boolean canHit(LivingEntity entity) {
        return !(entity instanceof MagicalGirlBossEntity)
                && (!(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator()));
    }

    public int getLife() {
        return this.life;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.life = tag.getInt("Life");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("Life", this.life);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
