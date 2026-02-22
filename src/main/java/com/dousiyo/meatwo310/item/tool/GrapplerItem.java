package com.dousiyo.meatwo310.item.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GrapplerItem extends Item {
    private final int cooldownTicks;
    private static final double MAX_DISTANCE = 20.0D;
    public static final String NO_FALL_DAMAGE_UNTIL_KEY = "meatwo310:no_fall_damage_until";
    public static final int NO_FALL_DAMAGE_DURATION_TICKS = 100;

    public GrapplerItem(Properties pProperties, int cooldownTicks) {
        super(pProperties);
        this.cooldownTicks = cooldownTicks;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        var stack = pPlayer.getItemInHand(pUsedHand);

        if (pPlayer.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        pLevel.playSound(null, pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                SoundEvents.FISHING_BOBBER_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));

        if (!pLevel.isClientSide) {
            Vec3 eyePos = pPlayer.getEyePosition(1.0F);
            Vec3 lookVec = pPlayer.getLookAngle();
            Vec3 endPos = eyePos.add(lookVec.scale(MAX_DISTANCE));

            BlockHitResult hitResult = pLevel.clip(new ClipContext(
                    eyePos,
                    endPos,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    pPlayer
            ));

            if (hitResult.getType() != HitResult.Type.MISS) {
                Vec3 hitPos = hitResult.getLocation();
                performGrapple(pPlayer, hitPos, pUsedHand, stack);
            }

            pPlayer.getCooldowns().addCooldown(this, this.cooldownTicks);
        }

        return InteractionResultHolder.success(stack);
    }

    private void performGrapple(Player player, Vec3 hitPos, InteractionHand hand, ItemStack stack) {
        Vec3 direction = hitPos.subtract(player.position());

        if (direction.lengthSqr() > 1.0D) {
            Vec3 targetPos = hitPos.add(0, 2.0D, 0);
            Vec3 adjustedDirection = targetPos.subtract(player.position());

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
                double heightDiff = targetPos.y - player.getY();
                if (heightDiff > 0) {
                    double heightFactor = Math.min(heightDiff / 10.0D, 1.0D);
                    upwardBoost = 0.8D + (heightFactor * 1.2D);
                } else {
                    double heightFactor = Math.min(Math.abs(heightDiff) / 10.0D, 1.0D);
                    upwardBoost = 0.8D - (heightFactor * 0.5D);
                    upwardBoost = Math.max(upwardBoost, 0.3D);
                }

                Vec3 motion = horizontalMotion.add(0, upwardBoost, 0);
                player.setDeltaMovement(motion);
            } else {
                double verticalDist = adjustedDirection.y;
                double verticalSpeed;

                if (verticalDist > 0) {
                    verticalSpeed = Math.max(1.5D, Math.min(verticalDist / 3.0D, 3.0D));
                } else {
                    verticalSpeed = 0.0D;
                }

                player.setDeltaMovement(0, verticalSpeed, 0);
            }

            player.fallDistance = 0;
            player.hurtMarked = true;

            long noFallDamageUntil = player.level().getGameTime() + NO_FALL_DAMAGE_DURATION_TICKS;
            player.getPersistentData().putLong(NO_FALL_DAMAGE_UNTIL_KEY, noFallDamageUntil);

            stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.meatwo310.grappler").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
