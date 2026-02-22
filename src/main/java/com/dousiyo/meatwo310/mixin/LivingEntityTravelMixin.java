package com.dousiyo.meatwo310.mixin;

import com.dousiyo.meatwo310.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityTravelMixin {

    private static final float FROZEN_FEET_FRICTION = 0.99F / 0.91F;
    private static final double LOW_SPEED_THRESHOLD = 0.06D;
    private static final double HIGH_SPEED_THRESHOLD = 0.24D;
    private static final double LOW_MIN_KEEP_RATIO = 0.84D;
    private static final double HIGH_MIN_KEEP_RATIO = 0.96D;
    private static final double LOW_REVERSE_KEEP_RATIO = 0.86D;
    private static final double HIGH_REVERSE_KEEP_RATIO = 0.97D;
    private static final double AIR_KEEP_RATIO = 0.992D;
    private static final double AIR_DIR_KEEP = 0.96D;
    private static final double MIN_SPEED_EPSILON_SQR = 1.0E-6D;

    @Unique
    private Vec3 meatwo310$prevHorizontal = Vec3.ZERO;

    private static boolean activeAny(LivingEntity e) {
        Entity base = (Entity) e;
        if (!(e instanceof Player)) return false;
        if (base.isInWaterOrBubble()) return false;
        if (e.hasEffect(ModEffects.FROZEN_FEET.get())) return true;
        return false;
    }

    private static boolean activeGround(LivingEntity e) {
        return activeAny(e) && ((Entity) e).onGround();
    }

    private static double clamp01(double x) {
        if (x < 0.0D) return 0.0D;
        if (x > 1.0D) return 1.0D;
        return x;
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    @Inject(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"))
    private void meatwo310$capturePreTravelVelocity(Vec3 travelVector, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!activeAny(self)) {
            meatwo310$prevHorizontal = Vec3.ZERO;
            return;
        }
        Vec3 v = self.getDeltaMovement();
        meatwo310$prevHorizontal = new Vec3(v.x, 0.0D, v.z);
    }

    @Redirect(
            method = "travel(Lnet/minecraft/world/phys/Vec3;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"
            )
    )
    private float meatwo310$boostGroundFriction(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (entity == self && activeGround(self)) return FROZEN_FEET_FRICTION;
        return state.getFriction(level, pos, entity);
    }

    @Inject(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At("TAIL"))
    private void meatwo310$limitHardBraking(Vec3 travelVector, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!activeAny(self)) return;

        Vec3 oldH = meatwo310$prevHorizontal;
        if (oldH.lengthSqr() < MIN_SPEED_EPSILON_SQR) return;

        Vec3 now = self.getDeltaMovement();
        Vec3 newH = new Vec3(now.x, 0.0D, now.z);

        double oldSpeed = oldH.length();
        double newSpeed = newH.length();
        if (oldSpeed <= 0.0D) return;

        if (activeGround(self)) {
            double speedT = clamp01((oldSpeed - LOW_SPEED_THRESHOLD) / (HIGH_SPEED_THRESHOLD - LOW_SPEED_THRESHOLD));
            double minKeepRatio = lerp(LOW_MIN_KEEP_RATIO, HIGH_MIN_KEEP_RATIO, speedT);
            double reverseKeepRatio = lerp(LOW_REVERSE_KEEP_RATIO, HIGH_REVERSE_KEEP_RATIO, speedT);

            if (newSpeed < oldSpeed * minKeepRatio) {
                newH = oldH.normalize().scale(oldSpeed * minKeepRatio);
                newSpeed = newH.length();
            }

            if (newH.dot(oldH) < 0.0D) {
                double keepSpeed = Math.max(newSpeed, oldSpeed * reverseKeepRatio);
                newH = oldH.normalize().scale(keepSpeed);
            }
        } else {
            Vec3 oldDir = oldH.normalize();
            if (newSpeed < oldSpeed * AIR_KEEP_RATIO) {
                newSpeed = oldSpeed * AIR_KEEP_RATIO;
            }

            Vec3 targetDir;
            if (newSpeed < MIN_SPEED_EPSILON_SQR) {
                targetDir = oldDir;
            } else {
                Vec3 newDir = newH.normalize();
                targetDir = oldDir.scale(AIR_DIR_KEEP).add(newDir.scale(1.0D - AIR_DIR_KEEP));
                if (targetDir.lengthSqr() < MIN_SPEED_EPSILON_SQR) {
                    targetDir = oldDir;
                } else {
                    targetDir = targetDir.normalize();
                }
            }
            newH = targetDir.scale(newSpeed);
        }

        self.setDeltaMovement(newH.x, now.y, newH.z);
    }
}
