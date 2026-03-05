package com.dousiyo.meatwo310.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class TimerClientConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public enum HudPosition {
        TOP_CENTER,
        TOP_RIGHT
    }

    public static final ForgeConfigSpec.EnumValue<HudPosition> HUD_POSITION = BUILDER
            .comment("Timer HUD position")
            .defineEnum("hudPosition", HudPosition.TOP_CENTER);

    public static final ForgeConfigSpec.BooleanValue CAPTURE_SHOW_OVERVIEW_HUD = BUILDER
            .comment("Show capture points overview HUD")
            .define("captureShowOverviewHud", true);

    public static final ForgeConfigSpec.BooleanValue CAPTURE_SHOW_FOCUS_HUD = BUILDER
            .comment("Show capture point focus HUD while standing in a point")
            .define("captureShowFocusHud", true);

    public static final ForgeConfigSpec.IntValue CAPTURE_OVERVIEW_Y_OFFSET = BUILDER
            .comment("Vertical offset for capture overview HUD")
            .defineInRange("captureOverviewYOffset", 24, -120, 200);

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    private TimerClientConfig() {}
}

