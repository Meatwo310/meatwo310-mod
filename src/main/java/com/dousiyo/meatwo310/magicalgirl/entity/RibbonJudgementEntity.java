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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

public class RibbonJudgementEntity extends Entity {
    private static final EntityDataAccessor<Boolean> RED_LASER = SynchedEntityData.defineId(RibbonJudgementEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CASTER_ID = SynchedEntityData.defineId(RibbonJudgementEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TARGET_ID = SynchedEntityData.defineId(RibbonJudgementEntity.class, EntityDataSerializers.INT);
    private UUID casterUuid;
    private UUID targetUuid;
    private int life;
    private int lastBeamHitTick = -20;

    public RibbonJudgementEntity(EntityType<? extends RibbonJudgementEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public RibbonJudgementEntity(Level level, LivingEntity caster, LivingEntity target) {
        this(level, caster, target, false);
    }

    public RibbonJudgementEntity(Level level, LivingEntity caster, LivingEntity target, boolean redLaser) {
        this(ModEntities.RIBBON_JUDGEMENT.get(), level);
        this.casterUuid = caster.getUUID();
        this.targetUuid = target.getUUID();
        this.entityData.set(RED_LASER, redLaser);
        this.entityData.set(CASTER_ID, caster.getId());
        this.entityData.set(TARGET_ID, target.getId());
        this.setPos(target.position());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(RED_LASER, false);
        this.entityData.define(CASTER_ID, -1);
        this.entityData.define(TARGET_ID, -1);
    }

    @Override
    public void tick() {
        super.tick();
        this.life++;
        if (this.level() instanceof ServerLevel serverLevel) {
            tickServer(serverLevel);
        }
        if (this.life > maxLife()) {
            this.discard();
        }
    }

    private void tickServer(ServerLevel serverLevel) {
        LivingEntity target = findLiving(serverLevel, this.targetUuid);
        LivingEntity caster = findLiving(serverLevel, this.casterUuid);
        if (target == null || caster == null) {
            this.discard();
            return;
        }
        this.entityData.set(CASTER_ID, caster.getId());
        this.entityData.set(TARGET_ID, target.getId());
        this.setPos(target.getX(), target.getY(), target.getZ());
        boolean redLaser = isRedLaser();
        int bindTicks = redLaser ? 42 : 50;
        if (this.life <= bindTicks) {
            target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, this.life < 25 ? 1 : 4, false, false));
            target.addEffect(new MobEffectInstance(MobEffects.JUMP, 20, -2, false, false));
            serverLevel.sendParticles(redLaser ? ParticleTypes.ELECTRIC_SPARK : ParticleTypes.WITCH, target.getX(), target.getY() + 0.2D, target.getZ(), 4, 0.45D, 0.2D, 0.45D, 0.01D);
        }
        int chargeTick = redLaser ? 56 : 70;
        int beamStart = redLaser ? 70 : 86;
        int beamEnd = redLaser ? 128 : 106;
        int beamInterval = redLaser ? 2 : 5;
        if (this.life == chargeTick) {
            serverLevel.playSound(null, caster.blockPosition(), SoundEvents.BEACON_POWER_SELECT, SoundSource.HOSTILE, 1.2F, 1.55F);
        }
        if (this.life >= beamStart && this.life <= beamEnd && this.life - this.lastBeamHitTick >= beamInterval) {
            this.lastBeamHitTick = this.life;
            fireBeam(serverLevel, caster, target, redLaser);
        }
    }

    private void fireBeam(ServerLevel serverLevel, LivingEntity caster, LivingEntity target, boolean redLaser) {
        Vec3 start = caster.getEyePosition().add(0.0D, 0.15D, 0.0D).add(caster.getLookAngle().scale(0.8D));
        Vec3 dir = target.getEyePosition().subtract(start).normalize();
        Vec3 end = start.add(dir.scale(28.0D));
        BlockHitResult blockHit = serverLevel.clip(new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, caster));
        if (blockHit.getType() != HitResult.Type.MISS) {
            end = blockHit.getLocation();
        }
        serverLevel.sendParticles(redLaser ? ParticleTypes.FLAME : ParticleTypes.END_ROD, start.x, start.y, start.z, redLaser ? 10 : 18, dir.x, dir.y, dir.z, redLaser ? 0.18D : 0.35D);
        AABB beamBox = new AABB(start, end).inflate(0.75D);
        double beamLength = start.distanceTo(end);
        for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, beamBox, RibbonJudgementEntity::canHit)) {
            Vec3 nearest = nearestPointOnSegment(start, end, entity.getEyePosition());
            if (nearest.distanceTo(entity.getEyePosition()) <= 0.65D && start.distanceTo(nearest) <= beamLength) {
                entity.hurt(this.damageSources().magic(), redLaser ? MagicalGirlDamage.RIBBON_JUDGEMENT_RED : MagicalGirlDamage.RIBBON_JUDGEMENT);
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
        return Math.min(1.0F, (this.life + partialTicks) / maxLife());
    }

    public int getLife() {
        return this.life;
    }

    public boolean isRedLaser() {
        return this.entityData.get(RED_LASER);
    }

    public int getCasterEntityId() {
        return this.entityData.get(CASTER_ID);
    }

    public int getTargetEntityId() {
        return this.entityData.get(TARGET_ID);
    }

    public float getBeamAlpha(float partialTicks) {
        float beamStart = isRedLaser() ? 70.0F : 86.0F;
        float beamEnd = isRedLaser() ? 128.0F : 106.0F;
        float age = this.life + partialTicks;
        if (age < beamStart || age > beamEnd) {
            return 0.0F;
        }
        return Mth.clamp(Math.min((age - beamStart) / 8.0F, (beamEnd - age) / 8.0F), 0.0F, 1.0F);
    }

    private int maxLife() {
        return isRedLaser() ? 150 : 135;
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.life = tag.getInt("Life");
        this.lastBeamHitTick = tag.getInt("LastBeamHitTick");
        this.entityData.set(RED_LASER, tag.getBoolean("RedLaser"));
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
        tag.putBoolean("RedLaser", isRedLaser());
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
