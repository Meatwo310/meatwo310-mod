package com.dousiyo.meatwo310.item.tool;

import com.dousiyo.meatwo310.magicalgirl.entity.MagicSafetyAnchorEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicSafetyAnchorItem extends Item {
    private final int cooldownTicks;

    public MagicSafetyAnchorItem(Properties properties, int cooldownTicks) {
        super(properties);
        this.cooldownTicks = cooldownTicks;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }
        Vec3 pos = Vec3.atBottomCenterOf(context.getClickedPos().relative(context.getClickedFace()));
        return placeAnchor(context.getLevel(), player, context.getItemInHand(), pos);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        InteractionResult result = placeAnchor(level, player, stack, player.position());
        return result.consumesAction()
                ? InteractionResultHolder.success(stack)
                : InteractionResultHolder.fail(stack);
    }

    private InteractionResult placeAnchor(Level level, Player player, ItemStack stack, Vec3 pos) {
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResult.FAIL;
        }
        level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.7F, 1.35F);
        if (!level.isClientSide) {
            MagicSafetyAnchorEntity anchor = new MagicSafetyAnchorEntity(level, pos.x, pos.y, pos.z);
            level.addFreshEntity(anchor);
            player.getCooldowns().addCooldown(this, this.cooldownTicks);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.meatwo310.magic_safety_anchor.1").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.meatwo310.magic_safety_anchor.2").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}
