package com.dousiyo.meatwo310.event;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.entity.ConversationNpcEntity;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ConversationNpcCommands {
    private static final double FIND_RADIUS = 64.0D;

    private ConversationNpcCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("meatwo310")
                        .then(Commands.literal("npc")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.literal("move")
                                        .then(Commands.argument("pos", Vec3Argument.vec3())
                                                .executes(ConversationNpcCommands::moveNearestNpc))
                                        .then(Commands.argument("target", EntityArgument.entity())
                                                .then(Commands.argument("pos", Vec3Argument.vec3())
                                                        .executes(ConversationNpcCommands::moveTargetNpc)))))
        );
    }

    private static int moveNearestNpc(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        ConversationNpcEntity npc = resolveNpc(source);
        if (npc == null) {
            source.sendFailure(Component.literal("[meatwo310] no nearby npc found"));
            return 0;
        }

        Vec3 pos = Vec3Argument.getVec3(ctx, "pos");
        npc.moveToTarget(pos.x, pos.y, pos.z);
        source.sendSuccess(() -> Component.literal("[meatwo310] npc moving to "
                + format(pos.x) + " " + format(pos.y) + " " + format(pos.z)), true);
        return 1;
    }

    private static int moveTargetNpc(CommandContext<CommandSourceStack> ctx)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        Entity entity = EntityArgument.getEntity(ctx, "target");
        if (!(entity instanceof ConversationNpcEntity npc) || !npc.isAlive()) {
            ctx.getSource().sendFailure(Component.literal("[meatwo310] target is not a living npc"));
            return 0;
        }

        Vec3 pos = Vec3Argument.getVec3(ctx, "pos");
        npc.moveToTarget(pos.x, pos.y, pos.z);
        ctx.getSource().sendSuccess(() -> Component.literal("[meatwo310] npc "
                + entity.getStringUUID() + " moving to "
                + format(pos.x) + " " + format(pos.y) + " " + format(pos.z)), true);
        return 1;
    }

    private static ConversationNpcEntity resolveNpc(CommandSourceStack source) {
        Entity entity = source.getEntity();
        if (entity instanceof ConversationNpcEntity npc && npc.isAlive()) {
            return npc;
        }

        Vec3 origin = source.getPosition();
        AABB area = new AABB(origin, origin).inflate(FIND_RADIUS);
        ConversationNpcEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (ConversationNpcEntity npc : source.getLevel().getEntitiesOfClass(ConversationNpcEntity.class, area, Entity::isAlive)) {
            double distance = npc.distanceToSqr(origin);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = npc;
            }
        }
        return nearest;
    }

    private static String format(double value) {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }
}
