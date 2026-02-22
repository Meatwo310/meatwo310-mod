package com.dousiyo.meatwo310.registry;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.recipe.Meatwo310CuttingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, Meatwo310.MODID);

    public static final RegistryObject<RecipeType<Meatwo310CuttingRecipe>> MEATWO310_CUTTING =
            RECIPE_TYPES.register("meatwo310_cutting", () -> new RecipeType<>() {
                @Override
                public String toString() {
                    return Meatwo310.MODID + ":meatwo310_cutting";
                }
            });
}
