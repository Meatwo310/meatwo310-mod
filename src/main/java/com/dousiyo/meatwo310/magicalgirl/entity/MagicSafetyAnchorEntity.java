package com.dousiyo.meatwo310.magicalgirl.entity;

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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class MagicSafetyAnchorEntity extends Entity {
    public static final String MAGIC_BULLET_TAG = "Meatwo310MagicBullet";
    public static final float RADIUS = 6.5F;
    public static final int LIFETIME = 20 * 45;
    private static final DustParticleOptions BARRIER_PARTICLE = new DustParticleOptions(new Vector3f(0.22F, 0.92F, 1.0F), 1.3F);
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(MagicSafetyAnchorEntity.class, EntityDataSerializers.INT);
    private int lifetime = LIFETIME;

    public MagicSafetyAnchorEntity(EntityType<? extends MagicSafetyAnchorEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public MagicSafetyAnchorEntity(Level level, double x, double y, double z) {
        this(ModEntities.MAGIC_SAFETY_ANCHOR.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_LIFETIME, this.lifetime);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel) {
            protectArea(serverLevel);
            spawnParticles(serverLevel);
        }
        if (this.tickCount >= this.lifetime) {
            this.discard();
        }
    }

    private void protectArea(ServerLevel level) {
        AABB box = this.getBoundingBox().inflate(RADIUS, 3.0D, RADIUS);
        for (MagicalHazardEntity hazard : level.getEntitiesOfClass(MagicalHazardEntity.class, box)) {
            double allowed = RADIUS + hazard.getRenderRadius();
            if (horizontalDistanceSqr(hazard) <= allowed * allowed) {
                hazard.discard();
            }
        }
        for (Entity entity : level.getEntities(this, box, entity -> entity.getPersistentData().getBoolean(MAGIC_BULLET_TAG))) {
            if (horizontalDistanceSqr(entity) <= RADIUS * RADIUS) {
                entity.discard();
            }
        }
        int playerInterval = Math.max(1, ServerConfig.MAGICAL_GIRL_ANCHOR_CHECK_INTERVAL.get());
        if (this.tickCount % playerInterval != 0) {
            return;
        }
        for (Player player : level.getEntitiesOfClass(Player.class, box, this::canProtectPlayer)) {
            if (horizontalDistanceSqr(player) <= RADIUS * RADIUS) {
                player.clearFire();
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 1, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, true, true));
            }
        }
    }

    private boolean canProtectPlayer(Player player) {
        return !player.isSpectator();
    }

    private double horizontalDistanceSqr(Entity entity) {
        double dx = entity.getX() - this.getX();
        double dz = entity.getZ() - this.getZ();
        return dx * dx + dz * dz;
    }

    private void spawnParticles(ServerLevel level) {
        int interval = Math.max(1, ServerConfig.MAGICAL_GIRL_PARTICLE_INTERVAL.get());
        if (this.tickCount % interval != 0) {
            return;
        }
        double y = this.getY() + 0.15D;
        for (int i = 0; i < 12; i++) {
            double angle = (this.tickCount * 0.08D) + (Math.PI * 2.0D * i / 24.0D);
            double x = this.getX() + Math.cos(angle) * RADIUS;
            double z = this.getZ() + Math.sin(angle) * RADIUS;
            level.sendParticles(BARRIER_PARTICLE, x, y, z, 1, 0.01D, 0.04D, 0.01D, 0.0D);
        }
        if (this.tickCount % 10 == 0) {
            level.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 1.0D, this.getZ(), 8, 0.4D, 0.6D, 0.4D, 0.02D);
        }
    }

    public float getLifeProgress(float partialTicks) {
        return Math.min(1.0F, (this.tickCount + partialTicks) / Math.max(1.0F, this.entityData.get(DATA_LIFETIME)));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.lifetime = tag.getInt("Lifetime");
        if (this.lifetime <= 0) {
            this.lifetime = LIFETIME;
        }
        this.entityData.set(DATA_LIFETIME, this.lifetime);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("Lifetime", this.lifetime);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
