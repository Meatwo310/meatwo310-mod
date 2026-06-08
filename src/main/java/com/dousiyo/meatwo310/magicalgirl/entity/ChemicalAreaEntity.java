package com.dousiyo.meatwo310.magicalgirl.entity;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlDamage;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlLines;
import com.dousiyo.meatwo310.magicalgirl.MagicalGirlPhase;
import com.dousiyo.meatwo310.magicalgirl.Phase3AttackState;
import com.dousiyo.meatwo310.magicalgirl.Phase3ProjectileUtil;
import com.dousiyo.meatwo310.registry.ModEntities;
import com.dousiyo.meatwo310.config.ServerConfig;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Level.ExplosionInteraction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.UUID;

public class ChemicalAreaEntity extends Entity {
    private static final DustParticleOptions CHEMICAL = new DustParticleOptions(new Vector3f(0.35F, 1.0F, 0.1F), 1.6F);
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(ChemicalAreaEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_WARMUP = SynchedEntityData.defineId(ChemicalAreaEntity.class, EntityDataSerializers.INT);
    private int lifeTicks = 20 * 8;
    private int effectInterval = 10;
    private int warmupTicks = 20;
    private float radius = 2.1F;
    private float damage = MagicalGirlDamage.CHEMICAL_AREA_DEFAULT;
    private boolean flammable = true;
    private UUID ownerUuid;

    public ChemicalAreaEntity(EntityType<? extends ChemicalAreaEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public ChemicalAreaEntity(Level level, Vec3 pos, float radius, int lifeTicks, int warmupTicks, float damage, boolean flammable, UUID ownerUuid) {
        this(ModEntities.CHEMICAL_AREA.get(), level);
        this.setPos(pos);
        this.radius = radius;
        this.lifeTicks = lifeTicks;
        this.warmupTicks = warmupTicks;
        this.damage = damage;
        this.flammable = flammable;
        this.ownerUuid = ownerUuid;
        this.entityData.set(DATA_RADIUS, radius);
        this.entityData.set(DATA_WARMUP, warmupTicks);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_RADIUS, this.radius);
        this.entityData.define(DATA_WARMUP, this.warmupTicks);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel) {
            spawnParticles(serverLevel);
            if (this.tickCount >= this.warmupTicks && this.tickCount % this.effectInterval == 0) {
                affectTargets();
            }
        }
        if (this.tickCount >= this.lifeTicks) {
            this.discard();
        }
    }

    private void spawnParticles(ServerLevel level) {
        int interval = Math.max(1, ServerConfig.MAGICAL_GIRL_PARTICLE_INTERVAL.get());
        if (this.tickCount % interval != 0) {
            return;
        }
        double y = this.getY() + 0.12D;
        int ring = this.tickCount < this.warmupTicks ? 9 : 16;
        for (int i = 0; i < ring; i++) {
            double angle = this.tickCount * 0.09D + Math.PI * 2.0D * i / ring;
            double x = this.getX() + Math.cos(angle) * this.radius;
            double z = this.getZ() + Math.sin(angle) * this.radius;
            level.sendParticles(CHEMICAL, x, y, z, 1, 0.03D, 0.02D, 0.03D, 0.0D);
        }
        if (this.tickCount >= this.warmupTicks) {
            level.sendParticles(ParticleTypes.ITEM_SLIME, this.getX(), y + 0.1D, this.getZ(), 5, this.radius * 0.45D, 0.1D, this.radius * 0.45D, 0.02D);
        }
    }

    private void affectTargets() {
        AABB box = this.getBoundingBox().inflate(this.radius, 1.3D, this.radius);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, box, this::canHit)) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            if (dx * dx + dz * dz <= this.radius * this.radius) {
                target.addEffect(new MobEffectInstance(MobEffects.POISON, 50, 0));
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 50, 0));
                target.hurt(this.damageSources().magic(), this.damage);
            }
        }
    }

    private boolean canHit(LivingEntity entity) {
        if (entity instanceof MagicalGirlBossEntity) {
            return false;
        }
        if (entity.getUUID().equals(this.ownerUuid)) {
            return false;
        }
        return !(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator());
    }

    public boolean isFlammable() {
        return this.flammable;
    }

    public void reactAndExplode() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            this.discard();
            return;
        }
        serverLevel.sendParticles(ParticleTypes.FLASH, this.getX(), this.getY() + 0.4D, this.getZ(), 2, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY() + 0.5D, this.getZ(), 80, this.radius, 0.5D, this.radius, 0.08D);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.6F, 0.85F);
        float explosionRadius = Math.max(3.2F, this.radius + 1.2F);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(explosionRadius, 2.0D, explosionRadius), this::canHit)) {
            Vec3 away = target.position().subtract(this.position());
            double distance = Math.max(0.75D, away.length());
            if (distance <= explosionRadius) {
                target.hurt(this.damageSources().explosion(null), (float) Math.max(MagicalGirlDamage.CHEMICAL_AREA_EXPLOSION_MIN, MagicalGirlDamage.CHEMICAL_AREA_EXPLOSION_BASE - distance * 1.2D));
                Vec3 push = away.normalize().scale(0.75D);
                target.push(push.x, 0.28D, push.z);
            }
        }
        serverLevel.explode(this, this.getX(), this.getY() + 0.2D, this.getZ(), explosionRadius * 0.45F, false, ExplosionInteraction.NONE);
        this.discard();
    }

    public float getRenderRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    public float getWarmupProgress(float partialTicks) {
        int warmup = Math.max(1, this.entityData.get(DATA_WARMUP));
        return Math.min(1.0F, (this.tickCount + partialTicks) / warmup);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.lifeTicks = tag.getInt("LifeTicks");
        this.effectInterval = Math.max(1, tag.getInt("EffectInterval"));
        this.warmupTicks = tag.getInt("WarmupTicks");
        this.radius = tag.getFloat("Radius");
        this.damage = tag.getFloat("Damage");
        this.flammable = tag.getBoolean("Flammable");
        this.ownerUuid = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        this.entityData.set(DATA_RADIUS, this.radius);
        this.entityData.set(DATA_WARMUP, this.warmupTicks);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("LifeTicks", this.lifeTicks);
        tag.putInt("EffectInterval", this.effectInterval);
        tag.putInt("WarmupTicks", this.warmupTicks);
        tag.putFloat("Radius", this.radius);
        tag.putFloat("Damage", this.damage);
        tag.putBoolean("Flammable", this.flammable);
        Optional.ofNullable(this.ownerUuid).ifPresent(uuid -> tag.putUUID("Owner", uuid));
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
