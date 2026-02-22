package com.dousiyo.meatwo310.registry;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.recipe.Meatwo310CuttingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Meatwo310.MODID);

    public static final RegistryObject<RecipeSerializer<Meatwo310CuttingRecipe>> MEATWO310_CUTTING =
            RECIPE_SERIALIZERS.register("meatwo310_cutting", Meatwo310CuttingRecipe.Serializer::new);
}
