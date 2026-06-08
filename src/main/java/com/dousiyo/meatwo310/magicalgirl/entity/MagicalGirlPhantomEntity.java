package com.dousiyo.meatwo310.magicalgirl.entity;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlLines;
import com.dousiyo.meatwo310.magicalgirl.MagicalGirlPhase;
import com.dousiyo.meatwo310.magicalgirl.Phase3AttackState;
import com.dousiyo.meatwo310.magicalgirl.Phase3ProjectileUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MagicalGirlPhantomEntity extends Phantom {
    public MagicalGirlPhantomEntity(EntityType<? extends Phantom> type, Level level) {
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
            flyHighOrbit();
        }
    }

    @Override
    public boolean isSunBurnTick() {
        return false;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        super.setTarget(null);
    }

    private void flyHighOrbit() {
        Player centerTarget = this.level().getNearestPlayer(this.getX(), this.getY(), this.getZ(), 96.0D, false);
        Vec3 center = centerTarget != null
                ? centerTarget.position()
                : this.position().subtract(this.getLookAngle().scale(10.0D));
        double orbitTicks = this.tickCount * 0.035D;
        double radius = 16.0D;
        double targetY = center.y + 13.0D + Math.sin(this.tickCount * 0.04D) * 1.4D;
        Vec3 orbitPos = new Vec3(
                center.x + Math.cos(orbitTicks) * radius,
                targetY,
                center.z + Math.sin(orbitTicks) * radius
        );
        Vec3 toOrbit = orbitPos.subtract(this.position());
        double distance = toOrbit.length();
        Vec3 motion = distance > 0.05D ? toOrbit.normalize().scale(Math.min(0.5D, distance * 0.08D)) : Vec3.ZERO;
        this.setDeltaMovement(this.getDeltaMovement().scale(0.55D).add(motion));
        Vec3 facing = new Vec3(-Math.sin(orbitTicks), 0.0D, Math.cos(orbitTicks));
        this.setYRot((float) (Math.atan2(facing.z, facing.x) * (180.0D / Math.PI)) - 90.0F);
        this.hasImpulse = true;
    }
}
