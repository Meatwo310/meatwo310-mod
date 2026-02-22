package com.dousiyo.meatwo310.compat.rei;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.recipe.Meatwo310CuttingRecipe;
import com.dousiyo.meatwo310.registry.ModBlocks;
import com.dousiyo.meatwo310.registry.ModRecipeTypes;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public class Meatwo310ReiPlugin implements REIClientPlugin {

    public static final CategoryIdentifier<Meatwo310CuttingReiDisplay> CUTTING =
            CategoryIdentifier.of(Meatwo310.MODID, "meatwo310_cutting");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new Meatwo310CuttingReiCategory());
        registry.addWorkstations(CUTTING, EntryStacks.of(ModBlocks.MEATWO310_CUTTER.get()));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(
                Meatwo310CuttingRecipe.class,
                ModRecipeTypes.MEATWO310_CUTTING.get(),
                Meatwo310CuttingReiDisplay::new
        );
    }
}
