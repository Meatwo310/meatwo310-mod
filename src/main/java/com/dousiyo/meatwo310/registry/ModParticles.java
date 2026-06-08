package com.dousiyo.meatwo310.registry;

import com.dousiyo.meatwo310.Meatwo310;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Meatwo310.MODID);

    public static final RegistryObject<SimpleParticleType> NATURE_LEAF =
            PARTICLE_TYPES.register("nature_leaf", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> NATURE_ROOT =
            PARTICLE_TYPES.register("nature_root", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> NATURE_IVY =
            PARTICLE_TYPES.register("nature_ivy", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> NATURE_LIGHT =
            PARTICLE_TYPES.register("nature_light", () -> new SimpleParticleType(false));

    private ModParticles() {
    }

    public static void register(IEventBus bus) {
        PARTICLE_TYPES.register(bus);
    }
}
