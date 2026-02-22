package com.dousiyo.meatwo310.health;

import com.dousiyo.meatwo310.config.ServerConfig;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public final class HpApplier {
    private HpApplier() {}

    public static void apply(Player player, double bonusHealth) {
        AttributeInstance inst = player.getAttribute(Attributes.MAX_HEALTH);
        if (inst == null) return;

        inst.removeModifier(HpConsts.START_UUID);
        inst.removeModifier(HpConsts.BONUS_UUID);

        double base = inst.getBaseValue();

        if (ServerConfig.HEALTH_ENABLE_CUSTOM_DEFAULT.get()) {
            double startDelta = HpConsts.STARTING_MAX_HEALTH - base;

            inst.addPermanentModifier(new AttributeModifier(
                    HpConsts.START_UUID, "yourmod.starting_health",
                    startDelta, AttributeModifier.Operation.ADDITION
            ));
        }

        inst.addPermanentModifier(new AttributeModifier(
                HpConsts.BONUS_UUID, "yourmod.bonus_health",
                bonusHealth, AttributeModifier.Operation.ADDITION
        ));

        player.setHealth((float) Math.min(player.getHealth(), player.getMaxHealth()));
    }
}
