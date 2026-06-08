package com.dousiyo.meatwo310.health;

import com.dousiyo.meatwo310.config.ServerConfig;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicalGirlBossEntity;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "meatwo310", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HpCommands {
    private static final String[] MAGICAL_GIRL_DEBUG_ATTACKS = {
            "phase1",
            "phase2",
            "final",
            "final_bullets",
            "lockon_burst",
            "chemical_spray",
            "reaction_bullet",
            "injection_needle",
            "suppression_rain",
            "summoned_arsenal",
            "mine_scatter",
            "emergency_execution",
            "phantoms",
            "hand",
            "hazard_magic",
            "ground_lightning",
            "colored_lightning",
            "stella_burst",
            "diamond_spinner",
            "ribbon_judgement",
            "rune_cage",
            "magic_mirror",
            "grand_spell_nova",
            "root_lance",
            "bloom_circle",
            "ivy_bind",
            "petal_gale",
            "twister716",
            "sakura_claw",
            "sakura_rain",
            "grand_blossom",
            "hazard_gas",
            "hazard_final",
            "yellow_ignition",
            "radiation",
            "blink"
    };

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
                                .then(Commands.literal("magicalGirl.endFunction")
                                        .then(Commands.argument("function", StringArgumentType.word())
                                                .executes(ctx -> setConfig(ctx, "magicalGirl.endFunction",
                                                        ServerConfig.MAGICAL_GIRL_END_FUNCTION,
                                                        StringArgumentType.getString(ctx, "function")))))
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
                        .then(Commands.literal("magical_girl")
                                .requires(src -> src.hasPermission(2))
                                .then(Commands.literal("start")
                                        .executes(HpCommands::startMagicalGirl))
                                .then(Commands.literal("remove")
                                        .executes(HpCommands::removeMagicalGirl))
                                .then(Commands.literal("debug_attack")
                                        .then(Commands.argument("attack", StringArgumentType.word())
                                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(MAGICAL_GIRL_DEBUG_ATTACKS, builder))
                                                .executes(ctx -> debugMagicalGirlAttack(ctx, null))
                                                .then(Commands.argument("target", EntityArgument.player())
                                                        .executes(ctx -> debugMagicalGirlAttack(ctx, EntityArgument.getPlayer(ctx, "target"))))
                                        )
                                )
                        )
        );
    }

    private static int debugMagicalGirlAttack(CommandContext<CommandSourceStack> ctx, ServerPlayer forcedTarget)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String attack = StringArgumentType.getString(ctx, "attack");
        MagicalGirlBossEntity boss = findNearestMagicalGirl(ctx.getSource());
        if (boss == null) {
            ctx.getSource().sendFailure(Component.literal("[meatwo310] no nearby meatwo310:magical_girl found"));
            return 0;
        }
        boolean ok = boss.debugCastAttack(attack, forcedTarget);
        if (!ok) {
            ctx.getSource().sendFailure(Component.literal("[meatwo310] unknown or unusable magical girl attack: " + attack));
            return 0;
        }
        ctx.getSource().sendSuccess(() -> Component.literal("[meatwo310] magical girl debug attack fired: " + attack), true);
        return 1;
    }

    private static int startMagicalGirl(CommandContext<CommandSourceStack> ctx) {
        MagicalGirlBossEntity boss = findNearestMagicalGirl(ctx.getSource());
        if (boss == null) {
            ctx.getSource().sendFailure(Component.literal("[meatwo310] no nearby meatwo310:magical_girl found"));
            return 0;
        }
        if (!boss.startBattleFromSeal()) {
            ctx.getSource().sendFailure(Component.literal("[meatwo310] nearest magical girl is not sealed or waiting"));
            return 0;
        }
        ctx.getSource().sendSuccess(() -> Component.literal("[meatwo310] magical girl battle started"), true);
        return 1;
    }

    private static int removeMagicalGirl(CommandContext<CommandSourceStack> ctx) {
        MagicalGirlBossEntity boss = findNearestMagicalGirl(ctx.getSource());
        if (boss == null) {
            ctx.getSource().sendFailure(Component.literal("[meatwo310] no nearby meatwo310:magical_girl found"));
            return 0;
        }
        boss.removeByCommand();
        ctx.getSource().sendSuccess(() -> Component.literal("[meatwo310] nearest magical girl removed"), true);
        return 1;
    }

    private static MagicalGirlBossEntity findNearestMagicalGirl(CommandSourceStack source) {
        Vec3 origin = source.getPosition();
        double radius = 128.0D;
        AABB area = new AABB(origin, origin).inflate(radius);
        MagicalGirlBossEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (MagicalGirlBossEntity boss : source.getLevel().getEntitiesOfClass(MagicalGirlBossEntity.class, area, Entity::isAlive)) {
            double distance = boss.distanceToSqr(origin);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = boss;
            }
        }
        return nearest;
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

    private static int setConfig(CommandContext<CommandSourceStack> ctx, String key,
                                 net.minecraftforge.common.ForgeConfigSpec.ConfigValue<String> configValue,
                                 String value) {
        configValue.set(value);
        ctx.getSource().sendSuccess(() ->
                Component.literal("[meatwo310] " + key + " = " + value), true);
        return 1;
    }
}
