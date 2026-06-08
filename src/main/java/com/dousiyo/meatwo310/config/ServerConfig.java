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

    public static final ForgeConfigSpec.ConfigValue<String> MAGICAL_GIRL_END_FUNCTION = BUILDER
            .push("magicalGirl")
            .comment("Function executed once after Amino Rin's flashback ends. Leave empty to disable.")
            .define("endFunction", "meatwo310:magical_girl/end");
    public static final ForgeConfigSpec.IntValue MAGICAL_GIRL_PARTICLE_INTERVAL = BUILDER
            .comment("Particle emission interval for heavy magical girl area effects")
            .defineInRange("particleInterval", 2, 1, 10);
    public static final ForgeConfigSpec.IntValue MAGICAL_GIRL_HAZARD_PARTICLE_INTERVAL = BUILDER
            .comment("Particle emission interval for large magical girl hazard fields")
            .defineInRange("hazardParticleInterval", 3, 1, 10);
    public static final ForgeConfigSpec.IntValue MAGICAL_GIRL_ANCHOR_CHECK_INTERVAL = BUILDER
            .comment("Interval for safety anchor player protection checks")
            .defineInRange("anchorCheckInterval", 5, 1, 20);
    public static final ForgeConfigSpec.IntValue MAGICAL_GIRL_MAX_PHASE3_PROJECTILES = BUILDER
            .comment("Maximum queued phase 3 superb projectiles per boss")
            .defineInRange("maxPhase3Projectiles", 64, 8, 256);
    public static final ForgeConfigSpec.IntValue MAGICAL_GIRL_MAX_FLOATING_GUNS = BUILDER
            .comment("Maximum floating guns summoned by one magical girl attack")
            .defineInRange("maxFloatingGuns", 12, 1, 32);
    public static final ForgeConfigSpec.IntValue MAGICAL_GIRL_LIGHTNING_RENDER_LAYERS = BUILDER
            .comment("Client lightning render layers for magical girl colored lightning")
            .defineInRange("lightningRenderLayers", 3, 1, 4);
    public static final ForgeConfigSpec.IntValue MAGICAL_GIRL_LIGHTNING_RENDER_BRANCHES = BUILDER
            .comment("Client lightning render branches for magical girl colored lightning")
            .defineInRange("lightningRenderBranches", 2, 1, 3);

    static {
        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}

