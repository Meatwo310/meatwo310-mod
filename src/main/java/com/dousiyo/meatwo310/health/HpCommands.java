package com.dousiyo.meatwo310.health;

import com.dousiyo.meatwo310.config.ServerConfig;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "meatwo310", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HpCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("meatwo310")
                        .then(Commands.literal("config")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.literal("explosion.taczDecayDrop")
                                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                                .executes(ctx -> setConfig(ctx, "explosion.taczDecayDrop",
                                                        ServerConfig.EXPLOSION_TACZ_DECAY_DROP,
                                                        BoolArgumentType.getBool(ctx, "enabled")))))
                                .then(Commands.literal("explosion.noDrop")
                                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                                .executes(ctx -> setConfig(ctx, "explosion.noDrop",
                                                        ServerConfig.EXPLOSION_NO_DROP,
                                                        BoolArgumentType.getBool(ctx, "enabled")))))
                                .then(Commands.literal("explosion.maxGrenadeRadius")
                                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.5D, 32.0D))
                                                .executes(ctx -> setConfig(ctx, "explosion.maxGrenadeRadius",
                                                        ServerConfig.EXPLOSION_MAX_GRENADE_RADIUS,
                                                        DoubleArgumentType.getDouble(ctx, "value")))))
                                .then(Commands.literal("explosion.maxAffectedEntities")
                                        .then(Commands.argument("value", IntegerArgumentType.integer(1, 512))
                                                .executes(ctx -> setConfig(ctx, "explosion.maxAffectedEntities",
                                                        ServerConfig.EXPLOSION_MAX_AFFECTED_ENTITIES,
                                                        IntegerArgumentType.getInteger(ctx, "value")))))
                                .then(Commands.literal("health.enableCustomDefault")
                                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                                .executes(ctx -> setConfig(ctx, "health.enableCustomDefault",
                                                        ServerConfig.HEALTH_ENABLE_CUSTOM_DEFAULT,
                                                        BoolArgumentType.getBool(ctx, "enabled")))))
                                .then(Commands.literal("health.respawnResistanceEffect")
                                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                                                .executes(ctx -> setConfig(ctx, "health.respawnResistanceEffect",
                                                        ServerConfig.RESPAWN_RESISTANCE_EFFECT,
                                                        BoolArgumentType.getBool(ctx, "enabled")))))
                        )
                        .then(Commands.literal("hp")
                                .then(Commands.literal("give")
                                        .requires(src -> src.hasPermission(2))
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                        .executes(ctx -> {
                                                            int useCount = IntegerArgumentType.getInteger(ctx, "amount");
                                                            double bonusToGive = HpConsts.BONUS_PER_USE * useCount;
                                                            int n = 0;
                                                            for (ServerPlayer p : EntityArgument.getPlayers(ctx, "targets")) {
                                                                p.getCapability(BonusHealthProvider.CAP).ifPresent(cap -> {
                                                                    cap.addBonus(bonusToGive);
                                                                    HpApplier.apply(p, cap.getBonusHealth());
                                                                });
                                                                n++;
                                                            }
                                                            int hpHearts = (int) (bonusToGive / 2.0D);
                                                            final int affected = n;
                                                            ctx.getSource().sendSuccess(() -> Component.literal(
                                                                    "[meatwo310] arumisia x" + useCount + " applied to " + affected
                                                                            + " player(s) (max hp +" + bonusToGive
                                                                            + ", hearts +" + hpHearts + ")"), true);
                                                            return n;
                                                        })
                                                )
                                        )
                                )
                                .then(Commands.literal("reset")
                                        .requires(src -> src.hasPermission(2))
                                        .then(Commands.argument("targets", EntityArgument.players())
                                                .executes(ctx -> {
                                                    int n = 0;
                                                    for (ServerPlayer p : EntityArgument.getPlayers(ctx, "targets")) {
                                                        p.getCapability(BonusHealthProvider.CAP).ifPresent(cap -> {
                                                            cap.setBonusHealth(0.0);
                                                            HpApplier.apply(p, 0.0);
                                                        });
                                                        n++;
                                                    }
                                                    final int affected = n;
                                                    ctx.getSource().sendSuccess(() ->
                                                            Component.literal("[meatwo310] hp reset for " + affected + " player(s)"), true);
                                                    return n;
                                                })
                                        )
                                )
                        )
        );
    }

    private static int setConfig(CommandContext<CommandSourceStack> ctx, String key,
                                 net.minecraftforge.common.ForgeConfigSpec.BooleanValue configValue,
                                 boolean enabled) {
        configValue.set(enabled);
        ctx.getSource().sendSuccess(() ->
                Component.literal("[meatwo310] " + key + " = " + enabled), true);
        return 1;
    }

    private static int setConfig(CommandContext<CommandSourceStack> ctx, String key,
                                 net.minecraftforge.common.ForgeConfigSpec.DoubleValue configValue,
                                 double value) {
        configValue.set(value);
        ctx.getSource().sendSuccess(() ->
                Component.literal("[meatwo310] " + key + " = " + value), true);
        return 1;
    }

    private static int setConfig(CommandContext<CommandSourceStack> ctx, String key,
                                 net.minecraftforge.common.ForgeConfigSpec.IntValue configValue,
                                 int value) {
        configValue.set(value);
        ctx.getSource().sendSuccess(() ->
                Component.literal("[meatwo310] " + key + " = " + value), true);
        return 1;
    }
}
