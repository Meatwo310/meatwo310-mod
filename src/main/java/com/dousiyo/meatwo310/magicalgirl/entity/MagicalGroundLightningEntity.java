package com.dousiyo.meatwo310.magicalgirl.entity;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlDamage;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlLines;
import com.dousiyo.meatwo310.magicalgirl.MagicalGirlPhase;
import com.dousiyo.meatwo310.magicalgirl.Phase3AttackState;
import com.dousiyo.meatwo310.magicalgirl.Phase3ProjectileUtil;
import com.dousiyo.meatwo310.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class MagicalGroundLightningEntity extends Entity {
    private static final int DROP_TICKS = 10;
    private int lifetime = 52;
    private double dirX = 1.0D;
    private double dirZ = 0.0D;
    private double originX;
    private double originY;
    private double originZ;
    private float damage = MagicalGirlDamage.GROUND_LIGHTNING;
    private boolean spawnedDropLightning;

    public MagicalGroundLightningEntity(EntityType<? extends MagicalGroundLightningEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public MagicalGroundLightningEntity(Level level, double x, double y, double z, double dirX, double dirZ) {
        this(ModEntities.MAGICAL_GROUND_LIGHTNING.get(), level);
        double length = Math.sqrt(dirX * dirX + dirZ * dirZ);
        if (length > 0.001D) {
            this.dirX = dirX / length;
            this.dirZ = dirZ / length;
        }
        this.setPos(x, y, z);
        this.originX = x;
        this.originY = y;
        this.originZ = z;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        if (this.level() instanceof ServerLevel serverLevel) {
            snapToGround(serverLevel);
            if (this.tickCount <= DROP_TICKS) {
                spawnDropLightning(serverLevel);
                if (this.tickCount == DROP_TICKS) {
                    hurtEntities();
                }
                return;
            }
            spawnArcParticles(serverLevel);
            if (this.tickCount % 3 == 0) {
                hurtEntities();
            }
            double speed = 0.78D;
            this.setPos(this.getX() + this.dirX * speed, this.getY(), this.getZ() + this.dirZ * speed);
        }
        if (this.tickCount >= this.lifetime + DROP_TICKS) {
            this.discard();
        }
    }

    private void snapToGround(ServerLevel level) {
        int x = Mth.floor(this.getX());
        int z = Mth.floor(this.getZ());
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        BlockPos below = new BlockPos(x, y - 1, z);
        if (!level.getBlockState(below).isAir()) {
            this.setPos(this.getX(), y + 0.04D, this.getZ());
        }
    }

    private void spawnArcParticles(ServerLevel level) {
        double sideX = -this.dirZ;
        double sideZ = this.dirX;
        for (int i = 0; i < 7; i++) {
            double forward = (i - 3) * 0.42D;
            double side = (this.random.nextDouble() - 0.5D) * 1.4D;
            double x = this.getX() + this.dirX * forward + sideX * side;
            double z = this.getZ() + this.dirZ * forward + sideZ * side;
            double y = this.getY() + 0.08D + this.random.nextDouble() * 0.18D;
            level.sendParticles(ParticleTypes.ELECTRIC_SPARK, x, y, z, 2, 0.05D, 0.01D, 0.05D, 0.02D);
            if (i % 3 == 0) {
                level.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.02D, 0.0D, 0.02D, 0.0D);
            }
        }
    }

    private void spawnDropLightning(ServerLevel level) {
        if (this.spawnedDropLightning) {
            return;
        }
        this.spawnedDropLightning = true;
        float height = (float) Math.max(0.6D, this.originY - this.getY());
        level.addFreshEntity(new ColoredLightningEntity(level, this.getX(), this.getY(), this.getZ(), height, 0.45F, 0.45F, 0.5F, true));
    }

    private void hurtEntities() {
        AABB box = this.getBoundingBox().inflate(1.35D, 0.8D, 1.35D);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, box, MagicalGroundLightningEntity::canHit)) {
            target.hurt(this.damageSources().magic(), this.damage);
        }
    }

    private static boolean canHit(LivingEntity entity) {
        return !(entity instanceof MagicalGirlBossEntity)
                && (!(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator()));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.lifetime = tag.getInt("Lifetime");
        this.dirX = tag.getDouble("DirX");
        this.dirZ = tag.getDouble("DirZ");
        this.originX = tag.getDouble("OriginX");
        this.originY = tag.getDouble("OriginY");
        this.originZ = tag.getDouble("OriginZ");
        this.damage = tag.getFloat("Damage");
        this.spawnedDropLightning = tag.getBoolean("SpawnedDropLightning");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("Lifetime", this.lifetime);
        tag.putDouble("DirX", this.dirX);
        tag.putDouble("DirZ", this.dirZ);
        tag.putDouble("OriginX", this.originX);
        tag.putDouble("OriginY", this.originY);
        tag.putDouble("OriginZ", this.originZ);
        tag.putFloat("Damage", this.damage);
        tag.putBoolean("SpawnedDropLightning", this.spawnedDropLightning);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
