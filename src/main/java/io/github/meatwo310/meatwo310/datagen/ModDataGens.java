package io.github.meatwo310.meatwo310.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGens {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        boolean server = event.includeServer();
        boolean client = event.includeClient();

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper efh = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ModLangGen.register(client, generator);
        ModModelGen.register(client, generator, output, efh);

//        TagGen.register(server, generator, output, lookupProvider, efh);
//        LootTableGen.register(server, generator, output);
//        RecipeGen.register(server, generator, output);
    }
}
