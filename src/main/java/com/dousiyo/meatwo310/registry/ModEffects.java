package com.dousiyo.meatwo310.registry;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.effect.BlinkEffect;
import com.dousiyo.meatwo310.effect.FrozenFeetEffect;
import com.dousiyo.meatwo310.effect.HopEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {
    private ModEffects() {}

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Meatwo310.MODID);

    public static final RegistryObject<MobEffect> FROZEN_FEET =
            EFFECTS.register("frozen_feet", FrozenFeetEffect::new);

    public static final RegistryObject<MobEffect> BLINK =
            EFFECTS.register("blink", BlinkEffect::new);

    public static final RegistryObject<MobEffect> HOP =
            EFFECTS.register("hop", HopEffect::new);


    public static void register(IEventBus bus) {
        EFFECTS.register(bus);
    }
}
