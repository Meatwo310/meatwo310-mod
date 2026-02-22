package com.dousiyo.meatwo310.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGens {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        boolean client = event.includeClient();

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper efh = event.getExistingFileHelper();

        ModLangGen.register(client, generator);
        ModModelGen.register(client, generator, output, efh);

    }
}
