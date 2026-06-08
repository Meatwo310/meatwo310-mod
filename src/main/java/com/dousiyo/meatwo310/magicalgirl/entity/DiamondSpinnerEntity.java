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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DiamondSpinnerEntity extends Entity {
    private static final EntityDataAccessor<Integer> DATA_STATE = SynchedEntityData.defineId(DiamondSpinnerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_SPIN = SynchedEntityData.defineId(DiamondSpinnerEntity.class, EntityDataSerializers.FLOAT);
    private UUID ownerUuid;
    private UUID targetUuid;
    private Vec3 lockedDirection = Vec3.ZERO;
    private int life;
    private int orbitSlot;
    private int orbitCount = 1;
    private int launchStartTick;

    public DiamondSpinnerEntity(EntityType<? extends DiamondSpinnerEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public DiamondSpinnerEntity(Level level, Vec3 pos, LivingEntity target) {
        this(ModEntities.DIAMOND_SPINNER.get(), level);
        this.setPos(pos);
        this.targetUuid = target.getUUID();
    }

    public DiamondSpinnerEntity(Level level, LivingEntity owner, LivingEntity target, int orbitSlot, int orbitCount) {
        this(ModEntities.DIAMOND_SPINNER.get(), level);
        this.ownerUuid = owner.getUUID();
        this.targetUuid = target.getUUID();
        this.orbitSlot = orbitSlot;
        this.orbitCount = Math.max(1, orbitCount);
        this.launchStartTick = 44 + orbitSlot * 6;
        this.setPos(orbitPosition(owner.position(), owner.getBbHeight()));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_STATE, 0);
        this.entityData.define(DATA_SPIN, 0.0F);
    }

    @Override
    public void tick() {
        super.tick();
        this.life++;
        this.entityData.set(DATA_SPIN, this.entityData.get(DATA_SPIN) + spinSpeed());
        if (this.level() instanceof ServerLevel serverLevel) {
            tickServer(serverLevel);
        }
        if (this.life > 220) {
            burstAndDiscard();
        }
    }

    private void tickServer(ServerLevel serverLevel) {
        int riseEndTick = this.launchStartTick + 18;
        if (this.life < this.launchStartTick) {
            this.entityData.set(DATA_STATE, 0);
            Entity owner = findOwner(serverLevel);
            if (owner instanceof LivingEntity livingOwner) {
                this.setPos(orbitPosition(livingOwner.position(), livingOwner.getBbHeight()));
            }
            serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 2, 0.25D, 0.25D, 0.25D, 0.01D);
            return;
        }
        LivingEntity target = findTarget(serverLevel);
        if (this.life < riseEndTick) {
            this.entityData.set(DATA_STATE, 1);
            this.setPos(this.position().add(0.0D, 0.055D, 0.0D));
            serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY(), this.getZ(), 2, 0.18D, 0.18D, 0.18D, 0.01D);
            if (target != null) {
                this.lockedDirection = arcLaunchVelocity(target);
            }
            return;
        }
        if (this.entityData.get(DATA_STATE) != 2) {
            this.entityData.set(DATA_STATE, 2);
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.TRIDENT_THROW, SoundSource.HOSTILE, 1.1F, 1.45F);
            if (this.lockedDirection.lengthSqr() < 0.01D) {
                this.lockedDirection = this.getLookAngle().scale(1.1D).add(0.0D, 0.32D, 0.0D);
            }
            this.setDeltaMovement(this.lockedDirection);
        }
        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.045D, 0.0D));
        Vec3 from = this.position();
        Vec3 to = from.add(this.getDeltaMovement());
        BlockHitResult blockHit = serverLevel.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (blockHit.getType() != HitResult.Type.MISS) {
            this.setPos(blockHit.getLocation());
            impactGround();
            return;
        }
        this.setPos(to);
        serverLevel.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 3, 0.06D, 0.06D, 0.06D, 0.0D);
        hurtFirstTarget(serverLevel);
    }

    private float spinSpeed() {
        return this.life < this.launchStartTick ? 7.0F + this.life * 1.25F : this.life < this.launchStartTick + 18 ? 18.0F : 34.0F;
    }

    private Vec3 orbitPosition(Vec3 ownerPos, double ownerHeight) {
        double baseAngle = Math.PI * 2.0D * this.orbitSlot / Math.max(1, this.orbitCount);
        double angle = baseAngle + this.life * 0.14D;
        double radius = 2.7D;
        double y = ownerPos.y + ownerHeight * 0.62D + 0.35D + Math.sin(this.life * 0.12D + baseAngle) * 0.18D;
        return new Vec3(ownerPos.x + Math.cos(angle) * radius, y, ownerPos.z + Math.sin(angle) * radius);
    }

    private Vec3 arcLaunchVelocity(LivingEntity target) {
        Vec3 targetPos = target.getEyePosition();
        Vec3 delta = targetPos.subtract(this.position());
        double horizontalDistance = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
        double flightTicks = Math.max(18.0D, Math.min(42.0D, horizontalDistance / 0.82D));
        double gravity = 0.045D;
        double vx = delta.x / flightTicks;
        double vz = delta.z / flightTicks;
        double vy = (delta.y + 0.5D * gravity * flightTicks * flightTicks) / flightTicks;
        return new Vec3(vx, vy, vz);
    }

    private Entity findOwner(ServerLevel serverLevel) {
        return this.ownerUuid == null ? null : serverLevel.getEntity(this.ownerUuid);
    }

    private LivingEntity findTarget(ServerLevel serverLevel) {
        if (this.targetUuid == null) {
            return null;
        }
        Entity entity = serverLevel.getEntity(this.targetUuid);
        return entity instanceof LivingEntity living && living.isAlive() ? living : null;
    }

    private void hurtFirstTarget(ServerLevel serverLevel) {
        AABB box = this.getBoundingBox().inflate(0.55D);
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, box, DiamondSpinnerEntity::canHit)) {
            target.hurt(this.damageSources().magic(), MagicalGirlDamage.DIAMOND_SPINNER);
            Vec3 away = this.getDeltaMovement();
            if (away.lengthSqr() > 0.01D) {
                away = away.normalize();
                target.push(away.x * 0.55D, 0.22D, away.z * 0.55D);
            }
            burstAndDiscard();
            return;
        }
    }

    private void burstAndDiscard() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY(), this.getZ(), 18, 0.35D, 0.35D, 0.35D, 0.06D);
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.HOSTILE, 1.0F, 1.2F);
        }
        this.discard();
    }

    private void impactGround() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY(), this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            serverLevel.playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.0F, 1.2F);
        }
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.2F, Level.ExplosionInteraction.NONE);
        this.discard();
    }

    private static boolean canHit(LivingEntity entity) {
        return !(entity instanceof MagicalGirlBossEntity)
                && (!(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator()));
    }

    public int getAttackState() {
        return this.entityData.get(DATA_STATE);
    }

    public float getSpin(float partialTicks) {
        return this.entityData.get(DATA_SPIN) + spinSpeed() * partialTicks;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.life = tag.getInt("Life");
        if (tag.hasUUID("Owner")) {
            this.ownerUuid = tag.getUUID("Owner");
        }
        if (tag.hasUUID("Target")) {
            this.targetUuid = tag.getUUID("Target");
        }
        this.lockedDirection = new Vec3(tag.getDouble("DirX"), tag.getDouble("DirY"), tag.getDouble("DirZ"));
        this.orbitSlot = tag.getInt("OrbitSlot");
        this.orbitCount = Math.max(1, tag.getInt("OrbitCount"));
        this.launchStartTick = tag.contains("LaunchStartTick") ? tag.getInt("LaunchStartTick") : 44 + this.orbitSlot * 6;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("Life", this.life);
        if (this.ownerUuid != null) {
            tag.putUUID("Owner", this.ownerUuid);
        }
        if (this.targetUuid != null) {
            tag.putUUID("Target", this.targetUuid);
        }
        tag.putDouble("DirX", this.lockedDirection.x);
        tag.putDouble("DirY", this.lockedDirection.y);
        tag.putDouble("DirZ", this.lockedDirection.z);
        tag.putInt("OrbitSlot", this.orbitSlot);
        tag.putInt("OrbitCount", this.orbitCount);
        tag.putInt("LaunchStartTick", this.launchStartTick);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
