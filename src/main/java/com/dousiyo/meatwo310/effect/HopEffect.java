package com.dousiyo.meatwo310.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.ForgeMod;

public final class HopEffect extends MobEffect {

    private static final String GRAVITY_UUID = "7d1b0c6e-0c2f-4f05-93b6-1baf7e54c1b2";

    public HopEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xA7D8FF);

        this.addAttributeModifier(
                ForgeMod.ENTITY_GRAVITY.get(),
                GRAVITY_UUID,
                -0.70D,
                AttributeModifier.Operation.MULTIPLY_TOTAL
        );
    }
}
