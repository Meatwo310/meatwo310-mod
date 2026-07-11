package com.dousiyo.meatwo310.registry;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.entity.ConversationNpcEntity;
import com.dousiyo.meatwo310.entity.EnhancedGrapplerProjectile;
import com.dousiyo.meatwo310.entity.GrapplerProjectile;
import com.dousiyo.meatwo310.entity.HopGrenade;
import com.dousiyo.meatwo310.entity.IceGrenade;
import com.dousiyo.meatwo310.entity.ImpulseGrenade;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Meatwo310.MODID);

    public static final RegistryObject<EntityType<GrapplerProjectile>> GRAPPLER_PROJECTILE = ENTITIES.register(
            "grappler_hook",
            () -> EntityType.Builder
                    .<GrapplerProjectile>of(GrapplerProjectile::new, MobCategory.MISC)
                    .sized(0.4f, 0.4f)
                    .build(Meatwo310.loc("grappler_hook").toString())
    );

    public static final RegistryObject<EntityType<EnhancedGrapplerProjectile>> ENHANCED_GRAPPLER_PROJECTILE = ENTITIES.register(
            "enhanced_grappler_hook",
            () -> EntityType.Builder
                    .<EnhancedGrapplerProjectile>of(EnhancedGrapplerProjectile::new, MobCategory.MISC)
                    .sized(0.4f, 0.4f)
                    .build(Meatwo310.loc("enhanced_grappler_hook").toString())
    );

    public static final RegistryObject<EntityType<ImpulseGrenade>> IMPULSE_GRENADE = ENTITIES.register(
            "impulse_grenade",
            () -> EntityType.Builder
                    .<ImpulseGrenade>of(ImpulseGrenade::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(Meatwo310.loc("impulse_grenade").toString())
    );

    public static final RegistryObject<EntityType<IceGrenade>> ICE_GRENADE = ENTITIES.register(
            "ice_grenade",
            () -> EntityType.Builder
                    .<IceGrenade>of(IceGrenade::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(Meatwo310.loc("ice_grenade").toString())
    );

    public static final RegistryObject<EntityType<HopGrenade>> HOP_GRENADE = ENTITIES.register(
            "hop_grenade",
            () -> EntityType.Builder
                    .<HopGrenade>of(HopGrenade::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .build(Meatwo310.loc("hop_grenade").toString())
    );

    public static final RegistryObject<EntityType<ConversationNpcEntity>> MEATWO310_ENTITY = ENTITIES.register(
            "meatwo310",
            () -> EntityType.Builder
                    .of(ConversationNpcEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .clientTrackingRange(10)
                    .build(Meatwo310.loc("meatwo310").toString())
    );

    public static final RegistryObject<EntityType<ConversationNpcEntity>> MEATWO310_NPC = ENTITIES.register(
            "npc",
            () -> EntityType.Builder
                    .of(ConversationNpcEntity::new, MobCategory.CREATURE)
                    .sized(0.6f, 1.8f)
                    .clientTrackingRange(10)
                    .build(Meatwo310.loc("npc").toString())
    );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
