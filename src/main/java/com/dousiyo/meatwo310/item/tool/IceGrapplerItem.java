package com.dousiyo.meatwo310.item.tool;

import com.dousiyo.meatwo310.entity.GrapplerProjectile;
import com.dousiyo.meatwo310.registry.ModEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IceGrapplerItem extends Item {
    private final int cooldownTicks;

    public IceGrapplerItem(Properties pProperties, int cooldownTicks) {
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
            pPlayer.addEffect(new MobEffectInstance(ModEffects.FROZEN_FEET.get(), 20 * 5, 0));

            pLevel.addFreshEntity(new GrapplerProjectile(pPlayer, pLevel));
            pPlayer.getCooldowns().addCooldown(this, this.cooldownTicks);

            stack.hurtAndBreak(1, pPlayer, player -> player.broadcastBreakEvent(pUsedHand));
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.meatwo310.ice_grappler").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
