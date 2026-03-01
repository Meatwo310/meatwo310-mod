package com.dousiyo.meatwo310.registry;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.datagen.ModModelGen;
import com.dousiyo.meatwo310.item.armor.UnbreakableDiamondArmorItem;
import com.dousiyo.meatwo310.item.food.*;
import com.dousiyo.meatwo310.item.tool.*;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Meatwo310.MODID);
    public static final Map<String, RegistryObject<Item>> ITEM_MAP = new LinkedHashMap<>();
    private static final int SHISHA_MAX_USES = 8;
    private static final int SHISHA_COOLDOWN_TICKS = 20 * 120;
    private static final int GRAPPLER_MAX_USES = 10;
    private static final int GRAPPLER_COOLDOWN_TICKS = 60;
    private static final int IMPULSE_GRENADE_COOLDOWN_TICKS = 20;
    private static final int ICE_GRENADE_COOLDOWN_TICKS = 20;
    private static final int HOP_GRENADE_COOLDOWN_TICKS = 20;

    public static final RegistryObject<Item> MEATWO310 = add("meatwo310", () -> new Meatwo310Item(new Item.Properties()));

    private static final FoodProperties VALINE3G_FOOD = new FoodProperties.Builder()
            .nutrition(Integer.MAX_VALUE)
            .saturationMod(Float.MAX_VALUE)
            .alwaysEat()
            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20 * 60, 0), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.JUMP, 20 * 120, 1), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 120, 4), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 20 * 60, 1), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.HEAL, 20, 3), 1.0f)
            .effect(() -> new MobEffectInstance(MobEffects.ABSORPTION, 20 * 60, 4), 1.0f)
            .build();

    private static final FoodProperties TWISTER716_FOOD = new FoodProperties.Builder()
            .nutrition(20)
            .saturationMod(0.9f)
            .build();

    private static final FoodProperties HOP_FISH_FOOD = new FoodProperties.Builder()
            .nutrition(8)
            .saturationMod(0.8f)
            .alwaysEat()
            .effect(() -> new MobEffectInstance(ModEffects.HOP.get(), 20 * 60, 0), 1.0f)
            .build();

    private static final FoodProperties BLINK_FISH_FOOD = new FoodProperties.Builder()
            .nutrition(8)
            .saturationMod(0.8f)
            .alwaysEat()
            .effect(() -> new MobEffectInstance(ModEffects.BLINK.get(), 20 * 60, 0), 1.0f)
            .build();

    private static final FoodProperties ICE_FISH_FOOD = new FoodProperties.Builder()
            .nutrition(8)
            .saturationMod(0.8f)
            .alwaysEat()
            .effect(() -> new MobEffectInstance(ModEffects.FROZEN_FEET.get(), 20 * 60, 0), 1.0f)
            .build();

    public static final RegistryObject<Item> VALINE3G =
            add("valine3g", () -> new Valine3gItem(new Item.Properties().food(VALINE3G_FOOD)));

    public static RegistryObject<Item> add(String name, Supplier<Item> sup) {
        return add(name, sup, ModModelGen::addBasicItem);
    }

    public static RegistryObject<Item> add(String name, Supplier<Item> sup, Consumer<RegistryObject<Item>> modelGenMethod) {
        RegistryObject<Item> item = ITEMS.register(name, sup);
        ITEM_MAP.put(name, item);
        modelGenMethod.accept(item);
        return item;
    }

    public static final RegistryObject<Item> MEATWO310_CUTTER =
            add("meatwo310_cutter",
                    () -> new BlockItem(ModBlocks.MEATWO310_CUTTER.get(), new Item.Properties()),
                    item -> {
                    });
    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
    public static final RegistryObject<Item> LOW_MEATWO310 =
            add("low_meatwo310", () -> new LowMeatwo310Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> ARUMISIA =
            add("arumisia", () -> new ArumisiaItem(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> TWISTER716 =
            add("twister716", () -> new Twister716Item(
                    new Item.Properties().food(TWISTER716_FOOD)
            ));

    public static final RegistryObject<Item> HOP_FISH =
            add("hop_fish", () -> new HopFishItem(
                    new Item.Properties().food(HOP_FISH_FOOD)
            ));

    public static final RegistryObject<Item> BLINK_FISH =
            add("blink_fish", () -> new BlinkFishItem(
                    new Item.Properties().food(BLINK_FISH_FOOD)
            ));

    public static final RegistryObject<Item> ICE_FISH =
            add("ice_fish", () -> new IceFishItem(
                    new Item.Properties().food(ICE_FISH_FOOD)
            ));

    public static final RegistryObject<Item> CYAN_SHISHA =
            add("cyan_shisha", () -> new CyanShishaItem(
                    new Item.Properties().durability(SHISHA_MAX_USES),
                    java.util.List.of(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 120, 4)),
                    SHISHA_COOLDOWN_TICKS
            ));

    public static final RegistryObject<Item> PINK_SHISHA =
            add("pink_shisha", () -> new ShishaItem(
                    new Item.Properties().durability(SHISHA_MAX_USES),
                    java.util.List.of(new MobEffectInstance(MobEffects.REGENERATION, 20 * 120, 0)),
                    SHISHA_COOLDOWN_TICKS
            ));

    public static final RegistryObject<Item> GRAPPLER =
            add("grappler", () -> new GrapplerItem(
                    new Item.Properties().durability(GRAPPLER_MAX_USES),
                    GRAPPLER_COOLDOWN_TICKS
            ));

    public static final RegistryObject<Item> ICE_GRAPPLER =
            add("ice_grappler", () -> new IceGrapplerItem(
                    new Item.Properties().durability(1),
                    GRAPPLER_COOLDOWN_TICKS
            ));

    public static final RegistryObject<Item> ENHANCED_GRAPPLER =
            add("enhanced_grappler", () -> new EnhancedGrapplerItem(
                    new Item.Properties().durability(GRAPPLER_MAX_USES),
                    GRAPPLER_COOLDOWN_TICKS
            ));

    public static final RegistryObject<Item> IMPULSE_GRENADE =
            add("impulse_grenade", () -> new ImpulseGrenadeItem(
                    new Item.Properties().stacksTo(16),
                    IMPULSE_GRENADE_COOLDOWN_TICKS
            ));

    public static final RegistryObject<Item> ICE_GRENADE =
            add("ice_grenade", () -> new IceGrenadeItem(
                    new Item.Properties().stacksTo(16),
                    ICE_GRENADE_COOLDOWN_TICKS
            ));

    public static final RegistryObject<Item> HOP_GRENADE =
            add("hop_grenade", () -> new HopGrenadeItem(
                    new Item.Properties().stacksTo(16),
                    HOP_GRENADE_COOLDOWN_TICKS
            ));

    public static final RegistryObject<Item> UNBREAKABLE_DIAMOND_HELMET =
            add("unbreakable_diamond_helmet", () -> new UnbreakableDiamondArmorItem(
                    ArmorMaterials.DIAMOND, ArmorItem.Type.HELMET, new Item.Properties()
            ));

    public static final RegistryObject<Item> UNBREAKABLE_DIAMOND_CHESTPLATE =
            add("unbreakable_diamond_chestplate", () -> new UnbreakableDiamondArmorItem(
                    ArmorMaterials.DIAMOND, ArmorItem.Type.CHESTPLATE, new Item.Properties()
            ));

    public static final RegistryObject<Item> UNBREAKABLE_DIAMOND_LEGGINGS =
            add("unbreakable_diamond_leggings", () -> new UnbreakableDiamondArmorItem(
                    ArmorMaterials.DIAMOND, ArmorItem.Type.LEGGINGS, new Item.Properties()
            ));

    public static final RegistryObject<Item> UNBREAKABLE_DIAMOND_BOOTS =
            add("unbreakable_diamond_boots", () -> new UnbreakableDiamondArmorItem(
                    ArmorMaterials.DIAMOND, ArmorItem.Type.BOOTS, new Item.Properties()
            ));
}
