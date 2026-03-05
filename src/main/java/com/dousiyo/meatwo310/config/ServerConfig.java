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

    public static final ForgeConfigSpec.ConfigValue<String> CAPTURE_BLUE_TEAM_NAME = BUILDER
            .push("capturePoints")
            .comment("Scoreboard team name treated as BLUE")
            .define("blueTeamName", "blue");

    public static final ForgeConfigSpec.ConfigValue<String> CAPTURE_RED_TEAM_NAME = BUILDER
            .comment("Scoreboard team name treated as RED")
            .define("redTeamName", "red");

    public static final ForgeConfigSpec.IntValue CAPTURE_SECONDS = BUILDER
            .comment("Seconds required to move from neutral (0.5) to owned edge (0.0/1.0)")
            .defineInRange("captureSeconds", 10, 1, 600);

    public static final ForgeConfigSpec.IntValue CAPTURE_START_DELAY_SECONDS = BUILDER
            .comment("Delay before capture starts after enemies are removed")
            .defineInRange("startDelaySeconds", 1, 0, 30);

    public static final ForgeConfigSpec.IntValue CAPTURE_OCCUPANCY_UPDATE_INTERVAL_TICKS = BUILDER
            .comment("AABB occupancy check interval in ticks")
            .defineInRange("occupancyUpdateIntervalTicks", 5, 1, 40);

    static {
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}

