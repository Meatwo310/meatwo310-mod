package com.dousiyo.meatwo310.magicalgirl.entity;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlDamage;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlLines;
import com.dousiyo.meatwo310.magicalgirl.MagicalGirlPhase;
import com.dousiyo.meatwo310.magicalgirl.Phase3AttackState;
import com.dousiyo.meatwo310.magicalgirl.Phase3ProjectileUtil;
import com.dousiyo.meatwo310.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class StellaBurstProjectileEntity extends Entity {
    private int life;
    private float damage = MagicalGirlDamage.STELLA_BURST_DEFAULT;

    public StellaBurstProjectileEntity(EntityType<? extends StellaBurstProjectileEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public StellaBurstProjectileEntity(Level level, Vec3 pos, Vec3 velocity, float damage) {
        this(ModEntities.STELLA_BURST_PROJECTILE.get(), level);
        this.setPos(pos);
        this.setDeltaMovement(velocity);
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 next = this.position().add(this.getDeltaMovement());
        this.setPos(next);
        if (this.level() instanceof ServerLevel serverLevel) {
            hurtFirstTarget(serverLevel);
        }
        if (++this.life >= 100) {
            this.discard();
        }
    }

    private void hurtFirstTarget(ServerLevel serverLevel) {
        AABB box = this.getBoundingBox().inflate(0.35D);
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, box, StellaBurstProjectileEntity::canHit)) {
            target.hurt(this.damageSources().magic(), this.damage);
            Vec3 knockback = this.getDeltaMovement();
            if (knockback.lengthSqr() > 0.01D) {
                knockback = knockback.normalize().scale(0.28D);
                target.push(knockback.x, 0.12D, knockback.z);
            }
            this.discard();
            return;
        }
    }

    private static boolean canHit(LivingEntity entity) {
        return !(entity instanceof MagicalGirlBossEntity)
                && (!(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator()));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.life = tag.getInt("Life");
        this.damage = tag.getFloat("Damage");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("Life", this.life);
        tag.putFloat("Damage", this.damage);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
