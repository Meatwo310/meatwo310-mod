package com.dousiyo.meatwo310.event;

import com.dousiyo.meatwo310.item.tool.EnhancedGrapplerItem;
import com.dousiyo.meatwo310.item.tool.GrapplerItem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "meatwo310", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class GrapplerEvents {

    private GrapplerEvents() {}

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        if (entity.getPersistentData().contains(GrapplerItem.NO_FALL_DAMAGE_UNTIL_KEY)) {
            long noFallDamageUntil = entity.getPersistentData().getLong(GrapplerItem.NO_FALL_DAMAGE_UNTIL_KEY);
            long currentTime = entity.level().getGameTime();

            if (currentTime <= noFallDamageUntil) {
                event.setCanceled(true);
                entity.fallDistance = 0;
            } else {
                entity.getPersistentData().remove(GrapplerItem.NO_FALL_DAMAGE_UNTIL_KEY);
            }
        }

        if (entity.getPersistentData().contains(EnhancedGrapplerItem.NO_FALL_DAMAGE_UNTIL_KEY)) {
            long noFallDamageUntil = entity.getPersistentData().getLong(EnhancedGrapplerItem.NO_FALL_DAMAGE_UNTIL_KEY);
            long currentTime = entity.level().getGameTime();

            if (currentTime <= noFallDamageUntil) {
                event.setCanceled(true);
                entity.fallDistance = 0;
            } else {
                entity.getPersistentData().remove(EnhancedGrapplerItem.NO_FALL_DAMAGE_UNTIL_KEY);
            }
        }
    }
}
