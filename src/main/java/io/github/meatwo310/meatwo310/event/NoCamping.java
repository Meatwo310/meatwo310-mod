package io.github.meatwo310.meatwo310.event;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.meatwo310.config.ServerConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class NoCamping {
    private static final MobEffectInstance[] CAMPING_EFFECTS = {
            // TODO: 調整する
        new MobEffectInstance(MobEffects.BLINDNESS, -1),
        new MobEffectInstance(MobEffects.DARKNESS, -1),
        new MobEffectInstance(MobEffects.DIG_SLOWDOWN, -1, 255),
        new MobEffectInstance(MobEffects.GLOWING, -1),
        new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, -1, 255),
        new MobEffectInstance(MobEffects.SLOW_FALLING, -1),
        new MobEffectInstance(MobEffects.WEAKNESS, -1, 255),
        new MobEffectInstance(MobEffects.JUMP, -1, -100),
    };

    @SubscribeEvent
    public static void onTickServerTick(TickEvent.ServerTickEvent event) {
        MinecraftServer server = event.getServer();
        if (server.getTickCount() % ServerConfig.CAMPING_CHECK_INTERVAL.get() != 0) {
            return;
        }
        LogUtils.getLogger().info("FIRE");

        server.getPlayerList().getPlayers().stream()
                .filter(player -> player.getY() > ServerConfig.CAMPING_CHECK_HEIGHT.get())
                .forEach(player -> {
                    for (MobEffectInstance effect : CAMPING_EFFECTS) {
                        player.addEffect(new MobEffectInstance(effect));
                    }
                });
    }
}
