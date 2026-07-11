package com.dousiyo.meatwo310.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue EXPLOSION_TACZ_DECAY_DROP = BUILDER
            .push("explosion")
            .define("taczDecayDrop", true);
    public static final ForgeConfigSpec.BooleanValue EXPLOSION_NO_DROP = BUILDER
            .define("noDrop", true);
    public static final ForgeConfigSpec.DoubleValue EXPLOSION_MAX_GRENADE_RADIUS = BUILDER
            .comment("Maximum allowed blast radius for custom grenades")
            .defineInRange("maxGrenadeRadius", 8.0D, 0.5D, 32.0D);
    public static final ForgeConfigSpec.IntValue EXPLOSION_MAX_AFFECTED_ENTITIES = BUILDER
            .comment("Upper bound for entities processed by custom grenade explosions")
            .defineInRange("maxAffectedEntities", 64, 1, 512);

    static {
        BUILDER.pop();
    }

    public static final ForgeConfigSpec.BooleanValue HEALTH_ENABLE_CUSTOM_DEFAULT = BUILDER
            .push("health")
            .comment("Enable custom default max health (60 HP instead of vanilla 20 HP)")
            .define("enableCustomDefault", true);

    public static final ForgeConfigSpec.BooleanValue RESPAWN_RESISTANCE_EFFECT = BUILDER
            .comment("Give Resistance V for 5 seconds after respawn")
            .define("respawnResistanceEffect", true);

    static {
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}

