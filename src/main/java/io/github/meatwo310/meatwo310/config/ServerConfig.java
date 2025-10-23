package io.github.meatwo310.meatwo310.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue CAMPING_CHECK_INTERVAL = BUILDER
            .push("campingCheck")
            .comment("Interval in ticks to check for camping players")
            .defineInRange("interval", 20, 1, Integer.MAX_VALUE);
    public static final ForgeConfigSpec.IntValue CAMPING_CHECK_HEIGHT = BUILDER
            .comment("Height above which players are considered camping")
            .defineInRange("height", 310, Integer.MIN_VALUE, Integer.MAX_VALUE);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
