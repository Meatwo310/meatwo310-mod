package com.dousiyo.meatwo310.item.food;

import com.dousiyo.meatwo310.Meatwo310;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class Valine3gItem extends Item {
    private static final int EXPERIENCE_REWARD = 1_000_000;

    public Valine3gItem(Properties props) {
        super(props);
    }

    public static boolean hasScreenEffect(LivingEntity entity) {
        MobEffectInstance speed = entity.getEffect(MobEffects.MOVEMENT_SPEED);
        MobEffectInstance jump = entity.getEffect(MobEffects.JUMP);
        return speed != null && speed.getAmplifier() >= 4
                && jump != null && jump.getAmplifier() >= 1;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.giveExperiencePoints(EXPERIENCE_REWARD);

            ResourceLocation advId = Meatwo310.loc("eat_valine3g");
            Advancement advancement = serverPlayer.server.getAdvancements().getAdvancement(advId);
            if (advancement != null) {
                for (String criterion : advancement.getCriteria().keySet()) {
                    serverPlayer.getAdvancements().award(advancement, criterion);
                }
            }
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.meatwo310.valine3g.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.meatwo310.valine3g.2").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}


