package com.dousiyo.meatwo310.timer.core;

import net.minecraft.server.level.ServerPlayer;

public final class PlaceholderResolver {
    private PlaceholderResolver() {}

    public static String resolve(String raw, ServerPlayer player, String timerId) {
        return raw
                .replace("{player}", player.getGameProfile().getName())
                .replace("{uuid}", player.getUUID().toString())
                .replace("{id}", timerId);
    }
}
