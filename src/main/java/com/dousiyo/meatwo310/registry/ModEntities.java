package com.dousiyo.meatwo310.registry;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.entity.ConversationNpcEntity;
import com.dousiyo.meatwo310.entity.EnhancedGrapplerProjectile;
import com.dousiyo.meatwo310.entity.GrapplerProjectile;
import com.dousiyo.meatwo310.entity.HopGrenade;
import com.dousiyo.meatwo310.entity.IceGrenade;
import com.dousiyo.meatwo310.entity.ImpulseGrenade;
import com.dousiyo.meatwo310.magicalgirl.entity.ColoredLightningEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.ChemicalAreaEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.DiamondSpinnerEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.FloatingTaCZGunEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.GrandSpellNovaEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.InjectionNeedleProjectileEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicRightHandEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicSafetyAnchorEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicalGirlBossEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicalGirlPhantomEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicalGroundLightningEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicalHazardEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicalPhantomEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicalTwisterEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.RibbonJudgementEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.RuneCageEntity;
import com.dousiyo.meatwo310.magicalgirl.entity.StellaBurstProjectileEntity;
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

    public static final RegistryObject<EntityType<MagicalGirlBossEntity>> MAGICAL_GIRL_BOSS = ENTITIES.register(
            "magical_girl",
            () -> EntityType.Builder
                    .of(MagicalGirlBossEntity::new, MobCategory.MONSTER)
                    .sized(0.6f, 1.8f)
                    .clientTrackingRange(10)
                    .build(Meatwo310.loc("magical_girl").toString())
    );

    public static final RegistryObject<EntityType<MagicRightHandEntity>> MAGIC_RIGHT_HAND = ENTITIES.register(
            "magic_right_hand",
            () -> EntityType.Builder
                    .<MagicRightHandEntity>of(MagicRightHandEntity::new, MobCategory.MONSTER)
                    .sized(2.8f, 2.4f)
                    .clientTrackingRange(10)
                    .build(Meatwo310.loc("magic_right_hand").toString())
    );

    public static final RegistryObject<EntityType<MagicalHazardEntity>> MAGICAL_HAZARD = ENTITIES.register(
            "magical_hazard",
            () -> EntityType.Builder
                    .<MagicalHazardEntity>of(MagicalHazardEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.1f)
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build(Meatwo310.loc("magical_hazard").toString())
    );

    public static final RegistryObject<EntityType<MagicalTwisterEntity>> MAGICAL_TWISTER = ENTITIES.register(
            "twister716",
            () -> EntityType.Builder
                    .<MagicalTwisterEntity>of(MagicalTwisterEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.1f)
                    .clientTrackingRange(18)
                    .updateInterval(1)
                    .build(Meatwo310.loc("twister716").toString())
    );

    public static final RegistryObject<EntityType<MagicSafetyAnchorEntity>> MAGIC_SAFETY_ANCHOR = ENTITIES.register(
            "magic_safety_anchor",
            () -> EntityType.Builder
                    .<MagicSafetyAnchorEntity>of(MagicSafetyAnchorEntity::new, MobCategory.MISC)
                    .sized(0.6f, 1.0f)
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build(Meatwo310.loc("magic_safety_anchor").toString())
    );

    public static final RegistryObject<EntityType<MagicalGroundLightningEntity>> MAGICAL_GROUND_LIGHTNING = ENTITIES.register(
            "magical_ground_lightning",
            () -> EntityType.Builder
                    .<MagicalGroundLightningEntity>of(MagicalGroundLightningEntity::new, MobCategory.MISC)
                    .sized(0.4f, 0.2f)
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build(Meatwo310.loc("magical_ground_lightning").toString())
    );

    public static final RegistryObject<EntityType<ColoredLightningEntity>> COLORED_LIGHTNING = ENTITIES.register(
            "colored_lightning",
            () -> EntityType.Builder
                    .<ColoredLightningEntity>of(ColoredLightningEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.1f)
                    .clientTrackingRange(16)
                    .updateInterval(2)
                    .build(Meatwo310.loc("colored_lightning").toString())
    );

    public static final RegistryObject<EntityType<MagicalPhantomEntity>> MAGICAL_PHANTOM = ENTITIES.register(
            "magical_phantom",
            () -> EntityType.Builder
                    .<MagicalPhantomEntity>of(MagicalPhantomEntity::new, MobCategory.MONSTER)
                    .sized(0.9f, 0.5f)
                    .clientTrackingRange(8)
                    .build(Meatwo310.loc("magical_phantom").toString())
    );

    public static final RegistryObject<EntityType<MagicalGirlPhantomEntity>> MAGICAL_GIRL_PHANTOM = ENTITIES.register(
            "magical_girl_phantom",
            () -> EntityType.Builder
                    .<MagicalGirlPhantomEntity>of(MagicalGirlPhantomEntity::new, MobCategory.MONSTER)
                    .sized(1.8f, 1.0f)
                    .clientTrackingRange(10)
                    .build(Meatwo310.loc("magical_girl_phantom").toString())
    );

    public static final RegistryObject<EntityType<StellaBurstProjectileEntity>> STELLA_BURST_PROJECTILE = ENTITIES.register(
            "stella_burst_projectile",
            () -> EntityType.Builder
                    .<StellaBurstProjectileEntity>of(StellaBurstProjectileEntity::new, MobCategory.MISC)
                    .sized(0.35f, 0.35f)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .build(Meatwo310.loc("stella_burst_projectile").toString())
    );

    public static final RegistryObject<EntityType<DiamondSpinnerEntity>> DIAMOND_SPINNER = ENTITIES.register(
            "diamond_spinner",
            () -> EntityType.Builder
                    .<DiamondSpinnerEntity>of(DiamondSpinnerEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.8f)
                    .clientTrackingRange(14)
                    .updateInterval(1)
                    .build(Meatwo310.loc("diamond_spinner").toString())
    );

    public static final RegistryObject<EntityType<RibbonJudgementEntity>> RIBBON_JUDGEMENT = ENTITIES.register(
            "ribbon_judgement",
            () -> EntityType.Builder
                    .<RibbonJudgementEntity>of(RibbonJudgementEntity::new, MobCategory.MISC)
                    .sized(0.2f, 0.2f)
                    .clientTrackingRange(14)
                    .updateInterval(2)
                    .build(Meatwo310.loc("ribbon_judgement").toString())
    );

    public static final RegistryObject<EntityType<RuneCageEntity>> RUNE_CAGE = ENTITIES.register(
            "rune_cage",
            () -> EntityType.Builder
                    .<RuneCageEntity>of(RuneCageEntity::new, MobCategory.MISC)
                    .sized(0.2f, 0.2f)
                    .clientTrackingRange(14)
                    .updateInterval(2)
                    .build(Meatwo310.loc("rune_cage").toString())
    );

    public static final RegistryObject<EntityType<GrandSpellNovaEntity>> GRAND_SPELL_NOVA = ENTITIES.register(
            "grand_spell_nova",
            () -> EntityType.Builder
                    .<GrandSpellNovaEntity>of(GrandSpellNovaEntity::new, MobCategory.MISC)
                    .sized(0.2f, 0.2f)
                    .clientTrackingRange(16)
                    .updateInterval(2)
                    .build(Meatwo310.loc("grand_spell_nova").toString())
    );

    public static final RegistryObject<EntityType<ChemicalAreaEntity>> CHEMICAL_AREA = ENTITIES.register(
            "chemical_area",
            () -> EntityType.Builder
                    .<ChemicalAreaEntity>of(ChemicalAreaEntity::new, MobCategory.MISC)
                    .sized(0.1f, 0.1f)
                    .clientTrackingRange(14)
                    .updateInterval(2)
                    .build(Meatwo310.loc("chemical_area").toString())
    );

    public static final RegistryObject<EntityType<FloatingTaCZGunEntity>> FLOATING_TACZ_GUN = ENTITIES.register(
            "floating_tacz_gun",
            () -> EntityType.Builder
                    .<FloatingTaCZGunEntity>of(FloatingTaCZGunEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.4f)
                    .clientTrackingRange(16)
                    .updateInterval(1)
                    .build(Meatwo310.loc("floating_tacz_gun").toString())
    );

    public static final RegistryObject<EntityType<InjectionNeedleProjectileEntity>> INJECTION_NEEDLE = ENTITIES.register(
            "injection_needle",
            () -> EntityType.Builder
                    .<InjectionNeedleProjectileEntity>of(InjectionNeedleProjectileEntity::new, MobCategory.MISC)
                    .sized(0.2f, 0.2f)
                    .clientTrackingRange(12)
                    .updateInterval(1)
                    .build(Meatwo310.loc("injection_needle").toString())
    );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }
}
