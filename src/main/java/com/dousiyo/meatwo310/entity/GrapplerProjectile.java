package com.dousiyo.meatwo310.entity;

import com.dousiyo.meatwo310.registry.ModEntities;
import com.dousiyo.meatwo310.registry.ModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class GrapplerProjectile extends ThrowableItemProjectile {
    private static final int MAX_LIFE_TIME = 7;
    public static final String NO_FALL_DAMAGE_UNTIL_KEY = "meatwo310:no_fall_damage_until";
    public static final int NO_FALL_DAMAGE_DURATION_TICKS = 100;
    public static final String GRAPPLER_LIFT_UNTIL_KEY = "meatwo310:grappler_lift_until";
    private static final int LIFT_DELAY_TICKS = 4;

    public GrapplerProjectile(EntityType<? extends GrapplerProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        scheduleDiscard(MAX_LIFE_TIME);
    }

    public GrapplerProjectile(double x, double y, double z, Level level) {
        super(ModEntities.GRAPPLER_PROJECTILE.get(), x, y, z, level);
        this.setNoGravity(true);
        scheduleDiscard(MAX_LIFE_TIME);
    }

    public GrapplerProjectile(LivingEntity shooter, Level level) {
        super(ModEntities.GRAPPLER_PROJECTILE.get(), shooter, level);
        this.setOwner(shooter);
        this.setNoGravity(true);
        scheduleDiscard(MAX_LIFE_TIME);

        Vec3 lookAngle = shooter.getLookAngle();
        double offset = 0.0D;
        this.moveTo(
                shooter.getX() + lookAngle.x * offset,
                shooter.getEyeY(),
                shooter.getZ() + lookAngle.z * offset,
                shooter.getYRot(),
                shooter.getXRot()
        );

        double speedMultiplier = 3.5D;
        Vec3 velocity = lookAngle.scale(speedMultiplier);
        this.setDeltaMovement(velocity);

        this.lerpMotion(velocity.x, velocity.y, velocity.z);
    }

    private void scheduleDiscard(int delayTicks) {
        if (this.level().isClientSide) return;
        var server = this.level().getServer();
        if (server == null) return;
        int executeAt = server.getTickCount() + delayTicks;
        server.tell(new net.minecraft.server.TickTask(executeAt, () -> {
            if (!this.isRemoved()) {
                this.discard();
            }
        }));
    }

    protected @NotNull Item getDefaultItem() {
        return ModItems.GRAPPLER.get();
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);

        if (!this.level().isClientSide) {
            if (this.getOwner() instanceof LivingEntity owner) {
                Vec3 hitPos = result.getLocation();

                Vec3 direction = hitPos.subtract(owner.position());

                if (direction.lengthSqr() > 1.0D) {
                    long liftUntil = owner.level().getGameTime() + LIFT_DELAY_TICKS + 20;
                    owner.getPersistentData().putLong(GRAPPLER_LIFT_UNTIL_KEY, liftUntil);
                    var server = owner.level().getServer();
                    if (server != null) {
                        server.tell(new net.minecraft.server.TickTask(server.getTickCount() + LIFT_DELAY_TICKS + 20, () -> {
                            if (owner.getPersistentData().getLong(GRAPPLER_LIFT_UNTIL_KEY) == liftUntil) {
                                owner.getPersistentData().remove(GRAPPLER_LIFT_UNTIL_KEY);
                            }
                        }));
                    }

                    Vec3 targetPos = hitPos.add(0, 2.0D, 0);
                    Vec3 adjustedDirection = targetPos.subtract(owner.position());

                    Vec3 horizontalDir = new Vec3(adjustedDirection.x, 0, adjustedDirection.z);
                    double horizontalDist = horizontalDir.length();

                    double totalDist = adjustedDirection.length();

                    if (horizontalDist > 1.0D) {
                        double horizontalSpeed;
                        if (totalDist <= 3.0D) {
                            horizontalSpeed = 0.0D;
                        } else if (totalDist < 10.0D) {
                            horizontalSpeed = ((totalDist - 3.0D) / 7.0D * 2.5D);
                        } else if (totalDist < 20.0D) {
                            horizontalSpeed = 2.5D + ((totalDist - 10.0D) / 10.0D * 2.0D);
                        } else if (totalDist < 35.0D) {
                            horizontalSpeed = 4.5D + ((totalDist - 20.0D) / 15.0D * 1.5D);
                        } else {
                            horizontalSpeed = 6.0D;
                        }

                        Vec3 horizontalMotion;
                        if (horizontalDist > 0.01D) {
                            horizontalMotion = horizontalDir.normalize().scale(horizontalSpeed);
                        } else {
                            horizontalMotion = Vec3.ZERO;
                        }

                        double upwardBoost;
                        double heightDiff = targetPos.y - owner.getY();
                        if (heightDiff > 0) {
                            double heightFactor = Math.min(heightDiff / 10.0D, 1.0D);
                            upwardBoost = 0.8D + (heightFactor * 1.2D);
                        } else {
                            double heightFactor = Math.min(Math.abs(heightDiff) / 10.0D, 1.0D);
                            upwardBoost = 0.8D - (heightFactor * 0.5D);
                            upwardBoost = Math.max(upwardBoost, 0.3D);
                        }

                        Vec3 motion = horizontalMotion.add(0, upwardBoost, 0);
                        owner.setDeltaMovement(motion);
                    } else {
                        double verticalDist = adjustedDirection.y;
                        double verticalSpeed;

                        if (verticalDist > 0) {
                            verticalSpeed = Math.max(1.5D, Math.min(verticalDist / 3.0D, 3.0D));
                        } else {
                            verticalSpeed = 0.0D;
                        }

                        owner.setDeltaMovement(0, verticalSpeed, 0);
                    }

                    owner.fallDistance = 0;
                    owner.hurtMarked = true;

                    long noFallDamageUntil = owner.level().getGameTime() + NO_FALL_DAMAGE_DURATION_TICKS;
                    owner.getPersistentData().putLong(NO_FALL_DAMAGE_UNTIL_KEY, noFallDamageUntil);
                    if (owner instanceof Player player) {
                        for (InteractionHand hand : InteractionHand.values()) {
                            ItemStack held = player.getItemInHand(hand);
                            if (held.is(ModItems.GRAPPLER.get())) {
                                held.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                                break;
                            }
                        }
                    }
                }
            }

            this.discard();
        }
    }
}
