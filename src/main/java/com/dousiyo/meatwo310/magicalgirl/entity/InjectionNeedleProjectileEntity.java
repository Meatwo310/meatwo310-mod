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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

public class InjectionNeedleProjectileEntity extends Entity {
    private UUID ownerUuid;
    private float damage = MagicalGirlDamage.INJECTION_NEEDLE_DEFAULT;

    public InjectionNeedleProjectileEntity(EntityType<? extends InjectionNeedleProjectileEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public InjectionNeedleProjectileEntity(Level level, Vec3 pos, Vec3 velocity, UUID ownerUuid, float damage) {
        this(ModEntities.INJECTION_NEEDLE.get(), level);
        this.setPos(pos);
        this.setDeltaMovement(velocity);
        this.ownerUuid = ownerUuid;
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 velocity = this.getDeltaMovement();
        this.setPos(this.position().add(velocity));
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 2, 0.015D, 0.015D, 0.015D, 0.0D);
            hitFirstTarget(serverLevel);
        }
        if (this.tickCount > 70 || this.onGround()) {
            this.discard();
        }
    }

    private void hitFirstTarget(ServerLevel level) {
        AABB box = this.getBoundingBox().inflate(0.28D);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box, this::canHit)) {
            target.hurt(this.damageSources().magic(), this.damage);
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 1));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
            target.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0));
            this.discard();
            return;
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

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.ownerUuid = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        this.damage = tag.getFloat("Damage");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        if (this.ownerUuid != null) {
            tag.putUUID("Owner", this.ownerUuid);
        }
        tag.putFloat("Damage", this.damage);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
