package com.dousiyo.meatwo310.magicalgirl.entity;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlDamage;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlLines;
import com.dousiyo.meatwo310.magicalgirl.MagicalGirlPhase;
import com.dousiyo.meatwo310.magicalgirl.Phase3AttackState;
import com.dousiyo.meatwo310.magicalgirl.Phase3ProjectileUtil;
import com.dousiyo.meatwo310.registry.ModEntities;
import com.dousiyo.meatwo310.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.DustParticleOptions;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class MagicalHazardEntity extends Entity {
    public static final int VARIANT_YELLOW_IGNITION = 3;
    public static final int VARIANT_RADIATION = 4;
    private static final DustParticleOptions YELLOW_SMOKE = new DustParticleOptions(new Vector3f(1.0F, 0.84F, 0.16F), 2.0F);
    private static final DustParticleOptions RADIATION_SMOKE = new DustParticleOptions(new Vector3f(0.0F, 0.72F, 0.18F), 2.0F);
    private static final EntityDataAccessor<Float> DATA_RADIUS = SynchedEntityData.defineId(MagicalHazardEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> DATA_VARIANT = SynchedEntityData.defineId(MagicalHazardEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_WARMUP = SynchedEntityData.defineId(MagicalHazardEntity.class, EntityDataSerializers.INT);
    private int lifetime = 80;
    private int warmup = 30;
    private float radius = 3.0F;
    private float damage = MagicalGirlDamage.RADIATION_HAZARD;
    private int variant = 0;
    private boolean ignited;

    public MagicalHazardEntity(EntityType<? extends MagicalHazardEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public MagicalHazardEntity(Level level, double x, double y, double z, int lifetime, int warmup, float radius, float damage, int variant) {
        this(ModEntities.MAGICAL_HAZARD.get(), level);
        this.setPos(x, y, z);
        this.lifetime = lifetime;
        this.warmup = warmup;
        this.radius = radius;
        this.damage = damage;
        this.variant = variant;
        this.entityData.set(DATA_RADIUS, radius);
        this.entityData.set(DATA_VARIANT, variant);
        this.entityData.set(DATA_WARMUP, warmup);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_RADIUS, this.radius);
        this.entityData.define(DATA_VARIANT, this.variant);
        this.entityData.define(DATA_WARMUP, this.warmup);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel serverLevel) {
            spawnParticles(serverLevel);
            if ((this.variant == VARIANT_YELLOW_IGNITION || this.variant == VARIANT_RADIATION) && this.tickCount >= this.warmup && !this.ignited) {
                this.ignited = true;
                if (this.variant == VARIANT_YELLOW_IGNITION) {
                    igniteArea(serverLevel);
                }
                hurtEntities();
            } else if (this.tickCount >= this.warmup && this.tickCount % 10 == 0) {
                hurtEntities();
            }
        }
        if (this.tickCount >= this.lifetime) {
            this.discard();
        }
    }

    private void spawnParticles(ServerLevel level) {
        int interval = Math.max(1, ServerConfig.MAGICAL_GIRL_HAZARD_PARTICLE_INTERVAL.get());
        if (this.tickCount % interval != 0) {
            return;
        }
        double y = this.getY() + 0.08D;
        if (this.variant == VARIANT_YELLOW_IGNITION || this.variant == VARIANT_RADIATION) {
            DustParticleOptions particle = this.variant == VARIANT_RADIATION ? RADIATION_SMOKE : YELLOW_SMOKE;
            int warningCount = this.variant == VARIANT_RADIATION ? 9 : 24;
            double warningHeight = this.variant == VARIANT_RADIATION ? 1.1D : 1.8D;
            level.sendParticles(particle, this.getX(), this.getY() + 1.0D, this.getZ(), warningCount, this.radius, warningHeight, this.radius, 0.02D);
            if (this.tickCount >= this.warmup) {
                if (this.variant == VARIANT_YELLOW_IGNITION) {
                    level.sendParticles(ParticleTypes.FLAME, this.getX(), this.getY() + 0.6D, this.getZ(), 32, this.radius, 0.5D, this.radius, 0.06D);
                    level.sendParticles(ParticleTypes.LAVA, this.getX(), this.getY() + 0.2D, this.getZ(), 6, this.radius * 0.55D, 0.2D, this.radius * 0.55D, 0.02D);
                } else {
                    level.sendParticles(RADIATION_SMOKE, this.getX(), this.getY() + 0.6D, this.getZ(), 12, this.radius, 0.35D, this.radius, 0.03D);
                }
            }
            return;
        }
        for (int i = 0; i < 9; i++) {
            double angle = (this.tickCount * 0.12D) + (Math.PI * 2.0D * i / 18.0D);
            double x = this.getX() + Math.cos(angle) * this.radius;
            double z = this.getZ() + Math.sin(angle) * this.radius;
            level.sendParticles(this.variant == 1 ? ParticleTypes.SMOKE : ParticleTypes.WITCH, x, y, z, 1, 0.02D, 0.02D, 0.02D, 0.0D);
        }
        if (this.tickCount >= this.warmup) {
            level.sendParticles(this.variant == 2 ? ParticleTypes.FLAME : ParticleTypes.DRAGON_BREATH, this.getX(), y + 0.3D, this.getZ(), 6, this.radius * 0.35D, 0.2D, this.radius * 0.35D, 0.02D);
        }
    }

    private void hurtEntities() {
        double height = this.variant == 1 ? 3.0D : 1.2D;
        AABB box = new AABB(
                this.getX() - this.radius,
                this.getY(),
                this.getZ() - this.radius,
                this.getX() + this.radius,
                this.getY() + height,
                this.getZ() + this.radius
        );
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, box, MagicalHazardEntity::canHit)) {
            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            if (dx * dx + dz * dz <= this.radius * this.radius && target.getBoundingBox().minY <= this.getY() + height) {
                target.hurt(this.damageSources().magic(), this.damage);
                if (this.variant == 1) {
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                    target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 35, 0));
                } else if (this.variant == 2) {
                    target.setSecondsOnFire(4);
                } else if (this.variant == VARIANT_YELLOW_IGNITION) {
                    target.setSecondsOnFire(8);
                } else if (this.variant == VARIANT_RADIATION) {
                    target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * 30, 0));
                    target.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 30, 0));
                }
            }
        }
    }

    private void igniteArea(ServerLevel level) {
        int attempts = Math.max(80, (int) (this.radius * 6.0F));
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        double radiusSqr = this.radius * this.radius;
        for (int i = 0; i < attempts; i++) {
            double dx = (this.random.nextDouble() * 2.0D - 1.0D) * this.radius;
            double dz = (this.random.nextDouble() * 2.0D - 1.0D) * this.radius;
            if (dx * dx + dz * dz > radiusSqr) {
                continue;
            }
            int x = MthFloor(this.getX() + dx);
            int z = MthFloor(this.getZ() + dz);
            int y = MthFloor(this.getY()) + this.random.nextInt(5) - 2;
            pos.set(x, y, z);
            if (!level.isEmptyBlock(pos)) {
                pos.move(0, 1, 0);
            }
            if (level.isEmptyBlock(pos) && Blocks.FIRE.defaultBlockState().canSurvive(level, pos)) {
                level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 11);
            }
        }
    }

    private static int MthFloor(double value) {
        int intValue = (int) value;
        return value < intValue ? intValue - 1 : intValue;
    }

    public float getRenderRadius() {
        return this.entityData.get(DATA_RADIUS);
    }

    public int getRenderVariant() {
        return this.entityData.get(DATA_VARIANT);
    }

    public float getWarmupProgress(float partialTicks) {
        int renderWarmup = Math.max(1, this.entityData.get(DATA_WARMUP));
        return Math.min(1.0F, (this.tickCount + partialTicks) / renderWarmup);
    }

    private static boolean canHit(LivingEntity entity) {
        return !(entity instanceof MagicalGirlBossEntity)
                && (!(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator()));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.lifetime = tag.getInt("Lifetime");
        this.warmup = tag.getInt("Warmup");
        this.radius = tag.getFloat("Radius");
        this.damage = tag.getFloat("Damage");
        this.variant = tag.getInt("Variant");
        this.ignited = tag.getBoolean("Ignited");
        this.entityData.set(DATA_RADIUS, this.radius);
        this.entityData.set(DATA_VARIANT, this.variant);
        this.entityData.set(DATA_WARMUP, this.warmup);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("Lifetime", this.lifetime);
        tag.putInt("Warmup", this.warmup);
        tag.putFloat("Radius", this.radius);
        tag.putFloat("Damage", this.damage);
        tag.putInt("Variant", this.variant);
        tag.putBoolean("Ignited", this.ignited);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
