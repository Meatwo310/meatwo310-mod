package com.dousiyo.meatwo310.magicalgirl.entity;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlLines;
import com.dousiyo.meatwo310.magicalgirl.MagicalGirlPhase;
import com.dousiyo.meatwo310.magicalgirl.Phase3AttackState;
import com.dousiyo.meatwo310.magicalgirl.Phase3ProjectileUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;

public class MagicalPhantomEntity extends Phantom {
    public MagicalPhantomEntity(EntityType<? extends Phantom> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide || !this.getPersistentData().contains(MagicalGirlBossEntity.MAGIC_PHANTOM_LIFE_TAG)) {
            return;
        }
        int life = this.getPersistentData().getInt(MagicalGirlBossEntity.MAGIC_PHANTOM_LIFE_TAG) - 1;
        if (life <= 0) {
            this.discard();
        } else {
            this.getPersistentData().putInt(MagicalGirlBossEntity.MAGIC_PHANTOM_LIFE_TAG, life);
        }
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hurt = super.doHurtTarget(target);
        if (hurt && !this.level().isClientSide) {
            explodeAndDiscard();
        }
        return hurt;
    }

    private void explodeAndDiscard() {
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.2D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.2F, Level.ExplosionInteraction.NONE);
        this.discard();
    }
}
