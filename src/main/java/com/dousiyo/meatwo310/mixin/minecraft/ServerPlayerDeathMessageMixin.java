package com.dousiyo.meatwo310.mixin.minecraft;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Locale;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerDeathMessageMixin {

    @Redirect(
            method = "die",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/damagesource/CombatTracker;getDeathMessage()Lnet/minecraft/network/chat/Component;"
            )
    )
    private Component meatwo310$appendPvpKillDistance(CombatTracker tracker, DamageSource source) {
        // まずバニラの通常メッセージを取得
        Component base = tracker.getDeathMessage();

        ServerPlayer victim = (ServerPlayer) (Object) this;
        Entity attackerEntity = source.getEntity(); // 間接攻撃（矢など）の場合は射手が入ることが多い

        // プレイヤー同士のキルログだけ対象
        if (!(attackerEntity instanceof ServerPlayer killer)) {
            return base;
        }

        // 自殺/同一プレイヤーは除外したい場合
        if (killer == victim) {
            return base;
        }

        // 念のため（通常は同一次元）
        if (killer.level() != victim.level()) {
            return base;
        }

        double distance = killer.position().distanceTo(victim.position());

        // 表示形式: (xx.xm) / 整数にしたいなら %.0f に変える
        String suffix = String.format(Locale.ROOT, " (%.1fm)", distance);

        return base.copy().append(Component.literal(suffix));
    }
}
