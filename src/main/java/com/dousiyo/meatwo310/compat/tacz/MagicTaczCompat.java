package com.dousiyo.meatwo310.compat.tacz;

import com.dousiyo.meatwo310.magicalgirl.entity.MagicSafetyAnchorEntity;
import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.modifier.AttachmentCacheProperty;
import com.tacz.guns.resource.pojo.data.gun.BulletData;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2d;

import java.util.Optional;

public final class MagicTaczCompat {
    private MagicTaczCompat() {
    }

    public static Optional<EntityKineticBullet> fireMagicBullet(
            ServerLevel level,
            LivingEntity caster,
            ItemStack gunStack,
            float pitch,
            float yaw,
            float velocity,
            float inaccuracy,
            boolean tracer,
            @Nullable int[] tracerRgba,
            float tracerSize
    ) {
        if (!(gunStack.getItem() instanceof IGun iGun)) {
            return Optional.empty();
        }

        ResourceLocation gunId = iGun.getGunId(gunStack);
        Optional<CommonGunIndex> maybeGunIndex = TimelessAPI.getCommonGunIndex(gunId);
        if (maybeGunIndex.isEmpty()) {
            return Optional.empty();
        }

        CommonGunIndex gunIndex = maybeGunIndex.get();
        GunData gunData = gunIndex.getGunData();
        BulletData bulletData = gunIndex.getBulletData();
        ResourceLocation ammoId = gunData.getAmmoId();

        IGunOperator operator = IGunOperator.fromLivingEntity(caster);
        operator.initialData();

        AttachmentCacheProperty cache = new AttachmentCacheProperty();
        cache.eval(gunStack, gunData);
        operator.updateCacheProperty(cache);

        EntityKineticBullet bullet = new EntityKineticBullet(
                level,
                caster,
                gunStack,
                ammoId,
                gunId,
                tracer,
                gunData,
                bulletData
        );

        bullet.shootFromRotation(caster, pitch, yaw, 0.0F, velocity, new Vector2d(inaccuracy, inaccuracy));

        if (tracerRgba != null && tracerRgba.length == 4) {
            bullet.getPersistentData().putIntArray(EntityKineticBullet.TRACER_COLOR_OVERRIDER_KEY, tracerRgba);
        }
        bullet.getPersistentData().putFloat(EntityKineticBullet.TRACER_SIZE_OVERRIDER_KEY, tracerSize);
        bullet.getPersistentData().putBoolean(MagicSafetyAnchorEntity.MAGIC_BULLET_TAG, true);

        level.addFreshEntity(bullet);
        return Optional.of(bullet);
    }

    public static int fireBurstAt(
            ServerLevel level,
            LivingEntity caster,
            ItemStack gunStack,
            float pitch,
            float yaw,
            int shots,
            float velocity,
            float inaccuracy,
            float yawSpreadDeg,
            float pitchSpreadDeg,
            @Nullable int[] tracerRgba,
            float tracerSize
    ) {
        int fired = 0;
        for (int i = 0; i < shots; i++) {
            float shotYaw = yaw + randomCentered(caster, yawSpreadDeg);
            float shotPitch = pitch + randomCentered(caster, pitchSpreadDeg);
            if (fireMagicBullet(level, caster, gunStack, shotPitch, shotYaw, velocity, inaccuracy, true, tracerRgba, tracerSize).isPresent()) {
                fired++;
            }
        }
        return fired;
    }

    private static float randomCentered(LivingEntity caster, float spread) {
        if (spread <= 0.0F) {
            return 0.0F;
        }
        return (caster.getRandom().nextFloat() * 2.0F - 1.0F) * spread;
    }
}
