package io.github.meatwo310.meatwo310.event;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.meatwo310.config.ServerConfig;
import me.xjqsh.lrtactical.init.ModEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;

@Mod.EventBusSubscriber
public class NoCamping {
    private static final Lazy<Set<MobEffectInstance>> CAMPING_EFFECTS = Lazy.of(() -> Set.of(
            new MobEffectInstance(MobEffects.BLINDNESS, -1),
            new MobEffectInstance(MobEffects.DARKNESS, -1),
            new MobEffectInstance(MobEffects.DIG_SLOWDOWN, -1, 255),
            new MobEffectInstance(MobEffects.GLOWING, -1),
            new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 10),
            new MobEffectInstance(MobEffects.SLOW_FALLING, -1),
            new MobEffectInstance(MobEffects.WEAKNESS, -1, 255),
            new MobEffectInstance(MobEffects.JUMP, -1, -100),
            new MobEffectInstance(ModEffects.BLIND.get(), -1),
            new MobEffectInstance(ModEffects.DEAFENED.get(), -1)
    ));

    @SubscribeEvent
    public static void onTickServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            return;
        }

        MinecraftServer server = event.getServer();
        if (server.getTickCount() % ServerConfig.CAMPING_CHECK_INTERVAL.get() != 0) {
            return;
        }

        LogUtils.getLogger().info("FIRE");

        var players = server.getPlayerList().getPlayers().stream()
                .filter(player -> player.getY() > ServerConfig.CAMPING_CHECK_HEIGHT.get())
                .toList();
        if (players.isEmpty()) return;

        for (ServerPlayer player : players) {
            for (MobEffectInstance effect : CAMPING_EFFECTS.get()) {
                player.addEffect(new MobEffectInstance(effect));
            }
        }
    }
}
