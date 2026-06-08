package com.dousiyo.meatwo310.event;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.entity.ConversationNpcEntity;
import com.dousiyo.meatwo310.registry.ModEntities;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ConversationNpcEvents {
    private ConversationNpcEvents() {
    }

    @SubscribeEvent
    public static void onEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MEATWO310_ENTITY.get(), ConversationNpcEntity.createAttributes().build());
        event.put(ModEntities.MEATWO310_NPC.get(), ConversationNpcEntity.createAttributes().build());
    }
}
