package com.dousiyo.meatwo310.magicalgirl.entity;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlDamage;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlLines;
import com.dousiyo.meatwo310.magicalgirl.MagicalGirlPhase;
import com.dousiyo.meatwo310.magicalgirl.Phase3AttackState;
import com.dousiyo.meatwo310.magicalgirl.Phase3ProjectileUtil;
import com.dousiyo.meatwo310.registry.ModEntities;
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
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MagicalTwisterEntity extends Entity {
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(MagicalTwisterEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_HEIGHT = SynchedEntityData.defineId(MagicalTwisterEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(MagicalTwisterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WARMUP = SynchedEntityData.defineId(MagicalTwisterEntity.class, EntityDataSerializers.INT);
    private int lifetime = 20 * 7;
    private int warmup = 14;
    private float radius = 8.5F;
    private float height = 18.0F;
    private float damage = MagicalGirlDamage.TWISTER_TICK_DEFAULT;
    private UUID ownerUuid;

    public MagicalTwisterEntity(EntityType<? extends MagicalTwisterEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public MagicalTwisterEntity(Level level, Vec3 pos, float radius, float height, int lifetime, int warmup, float damage, UUID ownerUuid) {
        this(ModEntities.MAGICAL_TWISTER.get(), level);
        this.setPos(pos);
        this.radius = radius;
        this.height = height;
        this.lifetime = lifetime;
        this.warmup = warmup;
        this.damage = damage;
        this.ownerUuid = ownerUuid;
        this.entityData.set(DATA_RADIUS, radius);
        this.entityData.set(DATA_HEIGHT, height);
        this.entityData.set(DATA_LIFETIME, lifetime);
        this.entityData.set(DATA_WARMUP, warmup);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_RADIUS, this.radius);
        this.entityData.define(DATA_HEIGHT, this.height);
        this.entityData.define(DATA_LIFETIME, this.lifetime);
        this.entityData.define(DATA_WARMUP, this.warmup);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel) {
            affectTargets();
            spawnSpiralParticles(serverLevel);
            if (this.tickCount == 1) {
                serverLevel.playSound(null, this.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 0.8F, 1.75F);
            } else if (this.tickCount % 12 == 0) {
                serverLevel.playSound(null, this.blockPosition(), SoundEvents.ELYTRA_FLYING, SoundSource.HOSTILE, 0.9F, 1.55F);
            }
            if (this.tickCount % 8 == 0) {
                serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY() + this.height * 0.45D, this.getZ(), 18, this.radius * 0.45D, this.height * 0.24D, this.radius * 0.45D, 0.08D);
                serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + this.height * 0.36D, this.getZ(), 8, this.radius * 0.25D, this.height * 0.16D, this.radius * 0.25D, 0.05D);
            }
        }
        if (this.tickCount >= this.lifetime) {
            finishBurst();
            this.discard();
        }
    }

    private void spawnSpiralParticles(ServerLevel serverLevel) {
        double scale = getEffectScale(0.0F);
        if (scale <= 0.05D || this.tickCount % 2 != 0) {
            return;
        }
        int arms = 4;
        int layers = 8;
        double spin = this.tickCount * 0.42D;
        for (int arm = 0; arm < arms; arm++) {
            double armOffset = Math.PI * 2.0D * arm / arms;
            for (int layer = 0; layer < layers; layer++) {
                double t = layer / (double) Math.max(1, layers - 1);
                double currentRadius = (this.radius * (1.0D - t * 0.68D) + 0.7D) * scale;
                double angle = spin + armOffset + t * 7.6D;
                double x = this.getX() + Math.cos(angle) * currentRadius;
                double y = this.getY() + 0.4D + this.height * t * scale;
                double z = this.getZ() + Math.sin(angle) * currentRadius;
                serverLevel.sendParticles(ParticleTypes.CLOUD, x, y, z, 1, 0.05D, 0.04D, 0.05D, 0.018D);
                if ((layer + arm) % 3 == 0) {
                    serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, x, y, z, 1, 0.03D, 0.03D, 0.03D, 0.01D);
                }
                if ((layer + this.tickCount) % 5 == 0) {
                    serverLevel.sendParticles(ParticleTypes.ASH, x, y, z, 1, 0.02D, 0.02D, 0.02D, 0.008D);
                }
            }
        }
    }

    private void affectTargets() {
        double effectScale = getEffectScale(0.0F);
        double currentRadius = this.radius * effectScale;
        if (currentRadius <= 0.2D) {
            return;
        }
        AABB box = new AABB(
                this.getX() - currentRadius,
                this.getY() - 0.5D,
                this.getZ() - currentRadius,
                this.getX() + currentRadius,
                this.getY() + this.height,
                this.getZ() + currentRadius
        );
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, box, this::canHit)) {
            Vec3 offset = target.position().subtract(this.position());
            double horizontalSqr = offset.x * offset.x + offset.z * offset.z;
            if (horizontalSqr > currentRadius * currentRadius) {
                continue;
            }
            double distance = Math.max(0.35D, Math.sqrt(horizontalSqr));
            Vec3 inward = new Vec3(-offset.x, 0.0D, -offset.z).normalize();
            Vec3 tangent = new Vec3(-inward.z, 0.0D, inward.x);
            double pull = Mth.clamp((currentRadius - distance + 2.4D) / currentRadius, 0.28D, 1.0D);
            double spin = Mth.clamp(0.92D - distance * 0.035D, 0.48D, 0.92D);
            double lift = 0.18D + pull * 0.12D;
            target.push(inward.x * 0.2D * pull + tangent.x * spin, lift, inward.z * 0.2D * pull + tangent.z * spin);
            target.setYRot(target.getYRot() + 52.0F);
            target.yBodyRot += 52.0F;
            target.yHeadRot += 52.0F;
            target.hurtMarked = true;
            if (this.tickCount >= this.warmup && this.tickCount % 6 == 0) {
                target.hurt(this.damageSources().magic(), this.damage);
            }
        }
    }

    private void finishBurst() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 1.0D, this.getZ(), 120, this.radius * 0.4D, 1.0D, this.radius * 0.4D, 0.18D);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.2F, 1.35F);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(this.radius + 1.5D, this.height, this.radius + 1.5D), this::canHit)) {
            Vec3 away = new Vec3(target.getX() - this.getX(), 0.0D, target.getZ() - this.getZ());
            if (away.lengthSqr() < 0.01D) {
                away = new Vec3(1.0D, 0.0D, 0.0D);
            }
            away = away.normalize();
            target.hurt(this.damageSources().magic(), this.damage * MagicalGirlDamage.TWISTER_FINISH_MULTIPLIER);
            target.push(away.x * 0.7D, 1.35D, away.z * 0.7D);
            target.hurtMarked = true;
        }
    }

    private boolean canHit(LivingEntity entity) {
        if (entity instanceof MagicalGirlBossEntity) {
            return false;
        }
        if (this.ownerUuid != null && entity.getUUID().equals(this.ownerUuid)) {
            return false;
        }
        return !(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator());
    }

    public float getRenderRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    public float getRenderHeight() {
        return this.entityData.get(DATA_HEIGHT);
    }

    public float getEffectScale(float partialTicks) {
        float age = this.tickCount + partialTicks;
        float warmupScale = Math.min(1.0F, age / Math.max(1, this.entityData.get(DATA_WARMUP)));
        float remaining = Math.max(0.0F, this.entityData.get(DATA_LIFETIME) - age);
        float fade = Math.min(1.0F, remaining / 12.0F);
        return warmupScale * fade;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.lifetime = tag.getInt("Lifetime");
        this.warmup = tag.getInt("Warmup");
        this.radius = tag.getFloat("Radius");
        this.height = tag.getFloat("Height");
        this.damage = tag.getFloat("Damage");
        if (tag.hasUUID("OwnerUuid")) {
            this.ownerUuid = tag.getUUID("OwnerUuid");
        }
        this.entityData.set(DATA_RADIUS, this.radius);
        this.entityData.set(DATA_HEIGHT, this.height);
        this.entityData.set(DATA_LIFETIME, this.lifetime);
        this.entityData.set(DATA_WARMUP, this.warmup);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("Lifetime", this.lifetime);
        tag.putInt("Warmup", this.warmup);
        tag.putFloat("Radius", this.radius);
        tag.putFloat("Height", this.height);
        tag.putFloat("Damage", this.damage);
        if (this.ownerUuid != null) {
            tag.putUUID("OwnerUuid", this.ownerUuid);
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
