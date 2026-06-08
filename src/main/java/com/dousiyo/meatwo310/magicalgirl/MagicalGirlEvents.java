package com.dousiyo.meatwo310.magicalgirl;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlDamage;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.magicalgirl.entity.MagicalGirlBossEntity;
import com.dousiyo.meatwo310.registry.ModEntities;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class MagicalGirlEvents {
    private MagicalGirlEvents() {
    }

    @SubscribeEvent
    public static void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MAGICAL_GIRL_BOSS.get(), MagicalGirlBossEntity.createAttributes().build());
        event.put(ModEntities.MAGIC_RIGHT_HAND.get(), magicRightHandAttributes().build());
        event.put(ModEntities.MAGICAL_PHANTOM.get(), magicalPhantomAttributes().build());
        event.put(ModEntities.MAGICAL_GIRL_PHANTOM.get(), magicalPhantomAttributes().build());
    }

    private static AttributeSupplier.Builder magicRightHandAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 80.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    private static AttributeSupplier.Builder magicalPhantomAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, MagicalGirlDamage.PHANTOM_ATTACK_ATTRIBUTE)
                .add(Attributes.FLYING_SPEED, 0.2D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D)
                .add(Attributes.FOLLOW_RANGE, 64.0D);
    }
}
