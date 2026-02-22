package com.dousiyo.meatwo310.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.BooleanValue EXPLOSION_TACZ_DECAY_DROP = BUILDER
            .push("explosion")
            .define("taczDecayDrop", true);
    public static final ForgeConfigSpec.BooleanValue EXPLOSION_NO_DROP = BUILDER
            .define("noDrop", true);

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

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
