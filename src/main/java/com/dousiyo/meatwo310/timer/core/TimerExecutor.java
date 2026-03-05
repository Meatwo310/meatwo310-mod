package com.dousiyo.meatwo310.timer.core;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class TimerExecutor {
    private TimerExecutor() {}

    public static void execute(ServerLevel level, ServerPlayer owner, String timerId, List<String> commands) {
        MinecraftServer server = level.getServer();
        CommandSourceStack source = server.createCommandSourceStack();
        for (String command : commands) {
            if (command == null || command.isBlank()) {
                continue;
            }
            String resolved = PlaceholderResolver.resolve(command, owner, timerId);
            server.getCommands().performPrefixedCommand(source, resolved);
        }
    }
}
