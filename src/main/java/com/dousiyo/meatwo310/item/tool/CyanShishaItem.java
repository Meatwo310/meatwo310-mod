package com.dousiyo.meatwo310.item.tool;

import com.dousiyo.meatwo310.Meatwo310;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class CyanShishaItem extends ShishaItem {
    public CyanShishaItem(Properties properties, List<MobEffectInstance> effects, int cooldownTicks) {
        super(properties, effects, cooldownTicks);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer serverPlayer) {
            ResourceLocation advId = Meatwo310.loc("eat_twister716");
            Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(advId);
            if (advancement != null) {
                for (String criterion : advancement.getCriteria().keySet()) {
                    serverPlayer.getAdvancements().award(advancement, criterion);
                }
            }
        }
        return super.finishUsingItem(stack, level, entity);
    }
}

