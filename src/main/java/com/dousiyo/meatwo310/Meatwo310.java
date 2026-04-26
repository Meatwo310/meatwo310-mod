package com.dousiyo.meatwo310;

import com.dousiyo.meatwo310.config.ServerConfig;
import com.dousiyo.meatwo310.registry.ModEffects;
import com.dousiyo.meatwo310.item.ModTabs;
import com.dousiyo.meatwo310.network.ModNetwork;
import com.dousiyo.meatwo310.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Meatwo310.MODID)
public class Meatwo310 {
    public static final String MODID = "meatwo310";

    public Meatwo310(FMLJavaModLoadingContext ctx) {
        IEventBus bus = ctx.getModEventBus();

        ModBlocks.BLOCKS.register(bus);
        ModItems.register(bus);
        ModTabs.register(bus);
        ModEntities.register(bus);
        ModMenus.MENUS.register(bus);
        ModRecipeTypes.RECIPE_TYPES.register(bus);
        ModRecipeSerializers.RECIPE_SERIALIZERS.register(bus);
        ModNetwork.register();
        ModEffects.register(bus);
        ctx.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);

    }


    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
