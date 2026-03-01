package com.dousiyo.meatwo310.network;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class BlinkTeleportC2SPacket {
    private static final int COOLDOWN_TICKS = 20;
    private static final double DASH_SPEED = 1.85D;
    private static final double MAX_UPWARD_SPEED_PENALTY = 0.45D;
    private static final int MOMENTUM_RESET_AFTER_TICKS = 4;
    private static final double HORIZONTAL_MOMENTUM_KEEP_RATIO = 0.5D;
    private static final String CD_KEY = Meatwo310.MODID + ":blink_cd";
    private static final String MOMENTUM_RESET_AT_KEY = Meatwo310.MODID + ":blink_momentum_reset_at";

    public BlinkTeleportC2SPacket() {}

    public static void encode(BlinkTeleportC2SPacket msg, FriendlyByteBuf buf) {
    }

    public static BlinkTeleportC2SPacket decode(FriendlyByteBuf buf) {
        return new BlinkTeleportC2SPacket();
    }

    public static void handle(BlinkTeleportC2SPacket msg, java.util.function.Supplier<net.minecraftforge.network.NetworkEvent.Context> ctxSup) {
        var ctx = ctxSup.get();
        ctx.enqueueWork(() -> {
            ServerPlayer sp = ctx.getSender();
            if (sp == null) return;

            MobEffectInstance inst = sp.getEffect(ModEffects.BLINK.get());
            if (inst == null) return;

            if (sp.isInWaterOrBubble()) return;

            CompoundTag data = sp.getPersistentData();
            long now = sp.level().getGameTime();
            long readyAt = data.getLong(CD_KEY);
            if (now < readyAt) return;

            boolean success = tryBlinkDash(sp);
            if (success) {
                data.putLong(CD_KEY, now + COOLDOWN_TICKS);
            }
        });
        ctx.setPacketHandled(true);
    }

    private static boolean tryBlinkDash(ServerPlayer sp) {
        var level = sp.serverLevel();
        Vec3 look = sp.getLookAngle();
        Vec3 dir = look;
        if (dir.lengthSqr() < 1.0E-6) return false;
        dir = dir.normalize();

        double upwardFactor = Math.max(0.0D, dir.y);
        double speedScale = 1.0D - (MAX_UPWARD_SPEED_PENALTY * upwardFactor);
        Vec3 dash = dir.scale(DASH_SPEED * speedScale);

        if (!level.getWorldBorder().isWithinBounds(BlockPos.containing(sp.position().add(dash)))) return false;
        AABB moved = sp.getBoundingBox().move(dash);
        if (!level.noCollision(sp, moved)) return false;

        sp.setDeltaMovement(dash.x, dash.y, dash.z);
        sp.hurtMarked = true;
        sp.fallDistance = 0.0F;
        sp.getPersistentData().putLong(MOMENTUM_RESET_AT_KEY, sp.level().getGameTime() + MOMENTUM_RESET_AFTER_TICKS);
        return true;
    }

    public static void applyMomentumResetIfDue(ServerPlayer sp) {
        if (sp.isRemoved()) return;
        CompoundTag data = sp.getPersistentData();
        if (!data.contains(MOMENTUM_RESET_AT_KEY)) return;

        long now = sp.level().getGameTime();
        long resetAt = data.getLong(MOMENTUM_RESET_AT_KEY);
        if (now < resetAt) return;

        Vec3 current = sp.getDeltaMovement();
        sp.setDeltaMovement(
                current.x * HORIZONTAL_MOMENTUM_KEEP_RATIO,
                0.0D,
                current.z * HORIZONTAL_MOMENTUM_KEEP_RATIO
        );
        sp.hurtMarked = true;
        data.remove(MOMENTUM_RESET_AT_KEY);
    }
}
