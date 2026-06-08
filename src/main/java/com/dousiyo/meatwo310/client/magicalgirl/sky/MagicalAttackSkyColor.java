package com.dousiyo.meatwo310.client.magicalgirl.sky;

import com.dousiyo.meatwo310.magicalgirl.entity.MagicalGirlBossEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public final class MagicalAttackSkyColor {
    private static final double EFFECT_RANGE_SQR = 192.0D * 192.0D;

    private MagicalAttackSkyColor() {}

    public static Vec3 apply(ClientLevel level, Vec3 cameraPos, Vec3 original, float partialTicks) {
        Vec3 fixedColor = fixedColor(level, cameraPos);
        return fixedColor == null ? original : fixedColor;
    }

    public static float blendRed(ClientLevel level, Vec3 cameraPos, float original, float partialTicks) {
        Vec3 fixedColor = fixedColor(level, cameraPos);
        return fixedColor == null ? original : (float) fixedColor.x;
    }

    public static float blendGreen(ClientLevel level, Vec3 cameraPos, float original, float partialTicks) {
        Vec3 fixedColor = fixedColor(level, cameraPos);
        return fixedColor == null ? original : (float) fixedColor.y;
    }

    public static float blendBlue(ClientLevel level, Vec3 cameraPos, float original, float partialTicks) {
        Vec3 fixedColor = fixedColor(level, cameraPos);
        return fixedColor == null ? original : (float) fixedColor.z;
    }

    private static Vec3 fixedColor(ClientLevel level, Vec3 cameraPos) {
        MagicalGirlBossEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (Entity entity : level.entitiesForRendering()) {
            if (!(entity instanceof MagicalGirlBossEntity boss) || !boss.isAlive()) {
                continue;
            }
            double distance = boss.position().distanceToSqr(cameraPos);
            if (distance > EFFECT_RANGE_SQR || !boss.hasFixedSkyColor()) {
                continue;
            }
            if (distance < nearestDistance) {
                nearest = boss;
                nearestDistance = distance;
            }
        }
        return nearest == null ? null : nearest.getFixedSkyColor();
    }
}
