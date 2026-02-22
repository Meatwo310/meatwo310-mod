package com.dousiyo.meatwo310.event;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.registry.ModEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FrozenFeetEffectEvents {

    private FrozenFeetEffectEvents() {}

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEffect() == null) return;
        if (!event.getEffect().equals(ModEffects.FROZEN_FEET.get())) return;

        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide) {
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 1.2F);
        }
    }

    @SubscribeEvent
    public static void onEffectExpired(MobEffectEvent.Expired event) {
        if (!event.getEffectInstance().getEffect().equals(ModEffects.FROZEN_FEET.get())) return;

        LivingEntity entity = event.getEntity();
        if (!entity.level().isClientSide) {
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.GLASS_BREAK, SoundSource.PLAYERS, 1.0F, 1.2F);
        }
    }
}

