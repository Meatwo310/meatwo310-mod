package io.github.meatwo310.meatwo310;

import io.github.meatwo310.meatwo310.config.ServerConfig;
import io.github.meatwo310.meatwo310.item.ModItems;
import io.github.meatwo310.meatwo310.item.ModTabs;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Meatwo310.MODID)
public class Meatwo310 {
    public static final String MODID = "meatwo310";

    public Meatwo310(FMLJavaModLoadingContext ctx) {
        IEventBus bus = ctx.getModEventBus();

        ModItems.register(bus);
        ModTabs.register(bus);

        ctx.registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC);
    }
}
