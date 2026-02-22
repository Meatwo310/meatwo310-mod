package com.dousiyo.meatwo310.health;

import com.dousiyo.meatwo310.config.ServerConfig;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "meatwo310", bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class HpEvents {

    @SubscribeEvent
    public static void onAttachCaps(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof Player) {
            e.addCapability(BonusHealthProvider.KEY, new BonusHealthProvider());
        }
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone e) {
        e.getOriginal().reviveCaps();
        e.getOriginal().getCapability(BonusHealthProvider.CAP).ifPresent(oldCap -> {
            e.getEntity().getCapability(BonusHealthProvider.CAP).ifPresent(newCap -> {
                newCap.setBonusHealth(oldCap.getBonusHealth());
                newCap.setInitialized(oldCap.isInitialized());
            });
        });
        e.getOriginal().invalidateCaps();
    }

    private static void applyOnly(Player p) {
        if (p.level().isClientSide) return;
        p.getCapability(BonusHealthProvider.CAP).ifPresent(cap ->
                HpApplier.apply(p, cap.getBonusHealth())
        );
    }

    private static void applyAndHealToMax(Player p) {
        if (p.level().isClientSide) return;
        p.getCapability(BonusHealthProvider.CAP).ifPresent(cap -> {
            HpApplier.apply(p, cap.getBonusHealth());
            p.setHealth((float) p.getMaxHealth());
        });
    }

    @SubscribeEvent
    public static void onLogin(PlayerLoggedInEvent e) {
        Player p = e.getEntity();
        if (p.level().isClientSide) return;

        p.getCapability(BonusHealthProvider.CAP).ifPresent(cap -> {
            HpApplier.apply(p, cap.getBonusHealth());

            if (!cap.isInitialized()) {
                p.setHealth((float) p.getMaxHealth());
                cap.setInitialized(true);
            }
        });
    }

    @SubscribeEvent
    public static void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getEntity();
        applyAndHealToMax(p);
        if (!p.level().isClientSide && ServerConfig.RESPAWN_RESISTANCE_EFFECT.get()) {
            p.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 4, false, true));
        }
    }

    @SubscribeEvent
    public static void onDimChange(PlayerChangedDimensionEvent e) {
        applyOnly(e.getEntity());
    }
}
