package com.dousiyo.meatwo310.magicalgirl.entity;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlDamage;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlLines;
import com.dousiyo.meatwo310.magicalgirl.MagicalGirlPhase;
import com.dousiyo.meatwo310.magicalgirl.Phase3AttackState;
import com.dousiyo.meatwo310.magicalgirl.Phase3ProjectileUtil;
import com.dousiyo.meatwo310.registry.ModEntities;
import com.tacz.guns.api.item.IGun;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class FloatingTaCZGunEntity extends Entity {
    private static final ResourceLocation DEFAULT_GUN = ResourceLocation.fromNamespaceAndPath("tacz", "m4a1");
    private static final EntityDataAccessor<String> DATA_GUN_ID = SynchedEntityData.defineId(FloatingTaCZGunEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Float> DATA_RENDER_YAW = SynchedEntityData.defineId(FloatingTaCZGunEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_RENDER_PITCH = SynchedEntityData.defineId(FloatingTaCZGunEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_STATE = SynchedEntityData.defineId(FloatingTaCZGunEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_TARGET_ID = SynchedEntityData.defineId(FloatingTaCZGunEntity.class, EntityDataSerializers.INT);
    private UUID ownerBossUuid;
    private UUID targetUuid;
    private int lifeTicks = 80;
    private int aimTicks = 30;
    private int lockTicks = 10;
    private int fireDelayTicks;
    private int stateTicks;
    private boolean fired;
    private int burstShotsFired;
    private ResourceLocation gunId = DEFAULT_GUN;
    private static final int BURST_SHOTS = 7;
    private static final int BURST_INTERVAL_TICKS = 3;

    public FloatingTaCZGunEntity(EntityType<? extends FloatingTaCZGunEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public FloatingTaCZGunEntity(Level level, ResourceLocation gunId, UUID ownerBossUuid, UUID targetUuid, int fireDelayTicks) {
        this(ModEntities.FLOATING_TACZ_GUN.get(), level);
        this.gunId = gunId;
        this.ownerBossUuid = ownerBossUuid;
        this.targetUuid = targetUuid;
        this.fireDelayTicks = fireDelayTicks;
        this.entityData.set(DATA_GUN_ID, gunId.toString());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_GUN_ID, DEFAULT_GUN.toString());
        this.entityData.define(DATA_RENDER_YAW, 0.0F);
        this.entityData.define(DATA_RENDER_PITCH, 0.0F);
        this.entityData.define(DATA_STATE, 0);
        this.entityData.define(DATA_TARGET_ID, -1);
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(Vec3.ZERO);
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Entity target = this.targetUuid == null ? null : serverLevel.getEntity(this.targetUuid);
        Entity owner = this.ownerBossUuid == null ? null : serverLevel.getEntity(this.ownerBossUuid);
        if (!(target instanceof LivingEntity livingTarget) || !(owner instanceof LivingEntity shooter)) {
            if (this.tickCount > 20) {
                this.discard();
            }
            return;
        }
        lookAt(targetBodyPosition(livingTarget));
        this.entityData.set(DATA_TARGET_ID, livingTarget.getId());
        if (this.tickCount < 12) {
            this.entityData.set(DATA_STATE, 0);
        } else if (this.tickCount < this.aimTicks) {
            this.entityData.set(DATA_STATE, 1);
        } else if (this.tickCount < this.aimTicks + this.lockTicks + this.fireDelayTicks) {
            this.entityData.set(DATA_STATE, 2);
        } else if (!this.fired) {
            this.entityData.set(DATA_STATE, 3);
            int fireTick = this.tickCount - (this.aimTicks + this.lockTicks + this.fireDelayTicks);
            if (this.burstShotsFired < BURST_SHOTS && fireTick >= this.burstShotsFired * BURST_INTERVAL_TICKS) {
                fireBurstShot(serverLevel, shooter, livingTarget);
                this.burstShotsFired++;
            }
            if (this.burstShotsFired >= BURST_SHOTS) {
                this.fired = true;
            }
        } else {
            this.entityData.set(DATA_STATE, 4);
        }
        if (++this.stateTicks >= this.lifeTicks + this.fireDelayTicks) {
            this.discard();
        }
    }

    private void lookAt(Vec3 target) {
        Vec3 dir = target.subtract(this.position());
        if (dir.lengthSqr() < 0.0001D) {
            return;
        }
        double horizontal = Math.sqrt(dir.x * dir.x + dir.z * dir.z);
        float yaw = (float) (Mth.atan2(dir.z, dir.x) * (180.0D / Math.PI)) - 90.0F;
        float pitch = (float) (-(Mth.atan2(dir.y, horizontal) * (180.0D / Math.PI)));
        this.entityData.set(DATA_RENDER_YAW, yaw);
        this.entityData.set(DATA_RENDER_PITCH, pitch);
        this.setYRot(yaw);
        this.setXRot(pitch);
        this.yRotO = yaw;
        this.xRotO = pitch;
    }

    public void aimAt(Vec3 target) {
        lookAt(target);
    }

    private void fireBurstShot(ServerLevel level, LivingEntity shooter, LivingEntity target) {
        Vec3 muzzle = getMuzzlePosition();
        Vec3 dir = targetBodyPosition(target).subtract(muzzle).normalize();
        dir = dir.add(
                (this.random.nextDouble() - 0.5D) * 0.045D,
                (this.random.nextDouble() - 0.5D) * 0.035D,
                (this.random.nextDouble() - 0.5D) * 0.045D
        ).normalize();
        Phase3ProjectileUtil.spawnSuperbProjectile(level, shooter, muzzle, dir, MagicalGirlDamage.FLOATING_TACZ_GUN_SHOT, 2.4F, 0.22F, 0.0F);
        level.playSound(null, this.blockPosition(), SoundEvents.CROSSBOW_SHOOT, SoundSource.HOSTILE, 0.55F, 1.55F + this.random.nextFloat() * 0.25F);
        level.sendParticles(net.minecraft.core.particles.ParticleTypes.SMOKE, muzzle.x, muzzle.y, muzzle.z, 4, 0.04D, 0.04D, 0.04D, 0.01D);
    }

    public Vec3 getMuzzlePosition() {
        Vec3 look = Vec3.directionFromRotation(getRenderPitch(), getRenderYaw());
        return this.position().add(look.scale(0.75D));
    }

    public static Vec3 targetBodyPosition(Entity target) {
        return target.position().add(0.0D, target.getBbHeight() * 0.55D, 0.0D);
    }

    public ItemStack createGunStack() {
        ItemStack stack = new ItemStack(com.tacz.guns.init.ModItems.MODERN_KINETIC_GUN.get());
        if (stack.getItem() instanceof IGun gun) {
            gun.setGunId(stack, getGunId());
        }
        return stack;
    }

    public ResourceLocation getGunId() {
        ResourceLocation parsed = ResourceLocation.tryParse(this.entityData.get(DATA_GUN_ID));
        return parsed == null ? DEFAULT_GUN : parsed;
    }

    public float getRenderYaw() {
        return this.entityData.get(DATA_RENDER_YAW);
    }

    public float getRenderPitch() {
        return this.entityData.get(DATA_RENDER_PITCH);
    }

    public int getGunState() {
        return this.entityData.get(DATA_STATE);
    }

    public int getTargetEntityId() {
        return this.entityData.get(DATA_TARGET_ID);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.gunId = ResourceLocation.tryParse(tag.getString("GunId"));
        if (this.gunId == null) {
            this.gunId = DEFAULT_GUN;
        }
        this.ownerBossUuid = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        this.targetUuid = tag.hasUUID("Target") ? tag.getUUID("Target") : null;
        this.lifeTicks = tag.getInt("LifeTicks");
        this.fireDelayTicks = tag.getInt("FireDelayTicks");
        this.fired = tag.getBoolean("Fired");
        this.burstShotsFired = tag.getInt("BurstShotsFired");
        this.entityData.set(DATA_GUN_ID, this.gunId.toString());
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putString("GunId", this.gunId.toString());
        if (this.ownerBossUuid != null) {
            tag.putUUID("Owner", this.ownerBossUuid);
        }
        if (this.targetUuid != null) {
            tag.putUUID("Target", this.targetUuid);
        }
        tag.putInt("LifeTicks", this.lifeTicks);
        tag.putInt("FireDelayTicks", this.fireDelayTicks);
        tag.putBoolean("Fired", this.fired);
        tag.putInt("BurstShotsFired", this.burstShotsFired);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
