package com.dousiyo.meatwo310.event;

import com.dousiyo.meatwo310.Meatwo310;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

@Mod.EventBusSubscriber(modid = Meatwo310.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class WoolFoodEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger(WoolFoodEvents.class);

    private static final FoodProperties WOOL_FOOD = new FoodProperties.Builder()
            .nutrition(5)
            .saturationMod(0.1f)
            .build();

    private static final Field ITEM_FOOD_PROPERTIES_FIELD =
            ObfuscationReflectionHelper.findField(Item.class, "f_41380_");

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            makeWoolEdible(Items.WHITE_WOOL);
            makeWoolEdible(Items.ORANGE_WOOL);
            makeWoolEdible(Items.MAGENTA_WOOL);
            makeWoolEdible(Items.LIGHT_BLUE_WOOL);
            makeWoolEdible(Items.YELLOW_WOOL);
            makeWoolEdible(Items.LIME_WOOL);
            makeWoolEdible(Items.PINK_WOOL);
            makeWoolEdible(Items.GRAY_WOOL);
            makeWoolEdible(Items.LIGHT_GRAY_WOOL);
            makeWoolEdible(Items.CYAN_WOOL);
            makeWoolEdible(Items.PURPLE_WOOL);
            makeWoolEdible(Items.BLUE_WOOL);
            makeWoolEdible(Items.BROWN_WOOL);
            makeWoolEdible(Items.GREEN_WOOL);
            makeWoolEdible(Items.RED_WOOL);
            makeWoolEdible(Items.BLACK_WOOL);
        });
    }

    private static void makeWoolEdible(Item wool) {
        try {
            ITEM_FOOD_PROPERTIES_FIELD.set(wool, WOOL_FOOD);
        } catch (IllegalAccessException e) {
            LOGGER.error("Failed to make wool edible: {}", wool, e);
        }
    }
}
