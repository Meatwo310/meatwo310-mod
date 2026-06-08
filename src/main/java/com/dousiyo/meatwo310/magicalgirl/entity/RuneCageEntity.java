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
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class RuneCageEntity extends Entity {
    private static final EntityDataAccessor<Integer> CASTER_ID = SynchedEntityData.defineId(RuneCageEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(RuneCageEntity.class, EntityDataSerializers.INT);
    private UUID casterUuid;
    private UUID targetUuid;
    private int life;
    private int lastBeamHitTick = -20;
    private final Map<UUID, Double> immobilizedSpeeds = new HashMap<>();
    private final Set<UUID> immobilizedThisTick = new HashSet<>();

    public RuneCageEntity(EntityType<? extends RuneCageEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public RuneCageEntity(Level level, double x, double y, double z) {
        this(ModEntities.RUNE_CAGE.get(), level);
        this.setPos(x, y, z);
    }

    public RuneCageEntity(Level level, double x, double y, double z, LivingEntity caster, LivingEntity target) {
        this(level, x, y, z);
        this.casterUuid = caster.getUUID();
        this.targetUuid = target.getUUID();
        this.entityData.set(CASTER_ID, caster.getId());
        this.entityData.set(TARGET_ID, target.getId());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CASTER_ID, -1);
        this.entityData.define(TARGET_ID, -1);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel) {
            tickServer(serverLevel);
        }
        if (++this.life > 120) {
            if (this.level() instanceof ServerLevel serverLevel) {
                restoreAllMovementSpeeds(serverLevel);
            }
            this.discard();
        }
    }

    private void tickServer(ServerLevel serverLevel) {
        this.immobilizedThisTick.clear();
        double radius = 3.0D;
        int interval = Math.max(1, ServerConfig.MAGICAL_GIRL_PARTICLE_INTERVAL.get());
        if (this.tickCount % interval == 0) {
            for (int i = 0; i < 6; i += 2) {
                double angle = Math.PI * 2.0D * i / 6.0D;
                serverLevel.sendParticles(ParticleTypes.ENCHANT, this.getX() + Math.cos(angle) * radius, this.getY() + 1.1D, this.getZ() + Math.sin(angle) * radius, 1, 0.03D, 0.45D, 0.03D, 0.0D);
            }
        }
        AABB box = this.getBoundingBox().inflate(radius + 0.4D, 1.8D, radius + 0.4D);
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, box, RuneCageEntity::canHit)) {
            double dist = target.position().subtract(this.position()).horizontalDistance();
            if (dist > radius - 0.6D && dist < radius + 0.8D) {
                immobilize(target);
                double dx = target.getX() - this.getX();
                double dz = target.getZ() - this.getZ();
                double len = Math.sqrt(dx * dx + dz * dz);
                if (len > 0.01D) {
                    target.push(-(dx / len) * 0.18D, 0.03D, -(dz / len) * 0.18D);
                }
            }
        }
        LivingEntity caster = findLiving(serverLevel, this.casterUuid);
        LivingEntity beamTarget = findLiving(serverLevel, this.targetUuid);
        if (caster == null || beamTarget == null) {
            restoreStaleMovementSpeeds(serverLevel);
            return;
        }
        this.entityData.set(CASTER_ID, caster.getId());
        this.entityData.set(TARGET_ID, beamTarget.getId());
        if (this.life <= 82) {
            immobilize(beamTarget);
        }
        if (this.life == 84) {
            serverLevel.playSound(null, caster.blockPosition(), SoundEvents.BEACON_POWER_SELECT, SoundSource.HOSTILE, 1.2F, 1.35F);
        }
        if (this.life >= 96 && this.life <= 116 && this.life - this.lastBeamHitTick >= 4) {
            this.lastBeamHitTick = this.life;
            fireBeam(serverLevel, caster, beamTarget);
        }
        restoreStaleMovementSpeeds(serverLevel);
    }

    private void immobilize(LivingEntity target) {
        AttributeInstance movementSpeed = target.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeed == null) {
            return;
        }
        UUID uuid = target.getUUID();
        this.immobilizedSpeeds.putIfAbsent(uuid, movementSpeed.getBaseValue());
        this.immobilizedThisTick.add(uuid);
        movementSpeed.setBaseValue(0.0D);
    }

    private void restoreStaleMovementSpeeds(ServerLevel serverLevel) {
        this.immobilizedSpeeds.entrySet().removeIf(entry -> {
            if (this.immobilizedThisTick.contains(entry.getKey())) {
                return false;
            }
            restoreMovementSpeed(serverLevel, entry.getKey(), entry.getValue());
            return true;
        });
    }

    private void restoreAllMovementSpeeds(ServerLevel serverLevel) {
        for (Map.Entry<UUID, Double> entry : this.immobilizedSpeeds.entrySet()) {
            restoreMovementSpeed(serverLevel, entry.getKey(), entry.getValue());
        }
        this.immobilizedSpeeds.clear();
        this.immobilizedThisTick.clear();
    }

    private void restoreMovementSpeed(ServerLevel serverLevel, UUID uuid, double baseValue) {
        Entity entity = serverLevel.getEntity(uuid);
        if (entity instanceof LivingEntity living) {
            AttributeInstance movementSpeed = living.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeed != null) {
                movementSpeed.setBaseValue(baseValue);
            }
        }
    }

    private void fireBeam(ServerLevel serverLevel, LivingEntity caster, LivingEntity target) {
        Vec3 start = caster.getEyePosition().add(0.0D, 0.15D, 0.0D).add(caster.getLookAngle().scale(0.8D));
        Vec3 dir = target.getEyePosition().subtract(start).normalize();
        Vec3 end = start.add(dir.scale(30.0D));
        BlockHitResult blockHit = serverLevel.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        if (blockHit.getType() != HitResult.Type.MISS) {
            end = blockHit.getLocation();
        }
        serverLevel.sendParticles(ParticleTypes.END_ROD, start.x, start.y, start.z, 24, dir.x, dir.y, dir.z, 0.38D);
        AABB beamBox = new AABB(start, end).inflate(0.75D);
        double beamLength = start.distanceTo(end);
        for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, beamBox, RuneCageEntity::canHit)) {
            Vec3 nearest = nearestPointOnSegment(start, end, entity.getEyePosition());
            if (nearest.distanceTo(entity.getEyePosition()) <= 0.65D && start.distanceTo(nearest) <= beamLength) {
                entity.hurt(this.damageSources().magic(), MagicalGirlDamage.RUNE_CAGE_BEAM);
            }
        }
    }

    private static Vec3 nearestPointOnSegment(Vec3 start, Vec3 end, Vec3 point) {
        Vec3 line = end.subtract(start);
        double lenSqr = line.lengthSqr();
        if (lenSqr < 0.0001D) {
            return start;
        }
        double t = point.subtract(start).dot(line) / lenSqr;
        return start.add(line.scale(Math.max(0.0D, Math.min(1.0D, t))));
    }

    private LivingEntity findLiving(ServerLevel serverLevel, UUID uuid) {
        if (uuid == null) {
            return null;
        }
        Entity entity = serverLevel.getEntity(uuid);
        return entity instanceof LivingEntity living && living.isAlive() ? living : null;
    }

    private static boolean canHit(LivingEntity entity) {
        return !(entity instanceof MagicalGirlBossEntity)
                && (!(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator()));
    }

    public float getProgress(float partialTicks) {
        return Math.min(1.0F, (this.life + partialTicks) / 20.0F);
    }

    public int getCasterEntityId() {
        return this.entityData.get(CASTER_ID);
    }

    public int getTargetEntityId() {
        return this.entityData.get(TARGET_ID);
    }

    public float getBeamAlpha(float partialTicks) {
        float age = this.life + partialTicks;
        if (age < 96.0F || age > 116.0F) {
            return 0.0F;
        }
        return Math.min((age - 96.0F) / 5.0F, (116.0F - age) / 5.0F);
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            restoreAllMovementSpeeds(serverLevel);
        }
        super.remove(reason);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.life = tag.getInt("Life");
        this.lastBeamHitTick = tag.getInt("LastBeamHitTick");
        if (tag.hasUUID("Caster")) {
            this.casterUuid = tag.getUUID("Caster");
        }
        if (tag.hasUUID("Target")) {
            this.targetUuid = tag.getUUID("Target");
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("Life", this.life);
        tag.putInt("LastBeamHitTick", this.lastBeamHitTick);
        if (this.casterUuid != null) {
            tag.putUUID("Caster", this.casterUuid);
        }
        if (this.targetUuid != null) {
            tag.putUUID("Target", this.targetUuid);
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
