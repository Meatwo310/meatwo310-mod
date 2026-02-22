package com.dousiyo.meatwo310.recipe;

import com.dousiyo.meatwo310.registry.ModRecipeSerializers;
import com.dousiyo.meatwo310.registry.ModRecipeTypes;
import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class Meatwo310CuttingRecipe extends StonecutterRecipe {
    public Meatwo310CuttingRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack result) {
        super(id, group, ingredient, result);
    }

    @Override
    public boolean matches(Container container, Level level) {
        return this.getIngredients().get(0).test(container.getItem(0));
    }

    @Override
    public ItemStack getToastSymbol() {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.MEATWO310_CUTTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.MEATWO310_CUTTING.get();
    }

    public static class Serializer implements RecipeSerializer<Meatwo310CuttingRecipe> {
        @Override
        public Meatwo310CuttingRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            Ingredient ingredient = Ingredient.fromJson(json.get("ingredient"));
            String resultId = GsonHelper.getAsString(json, "result");
            int count = GsonHelper.getAsInt(json, "count", 1);
            Item resultItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(resultId));
            if (resultItem == null) {
                throw new IllegalStateException("Unknown item in recipe result: " + resultId);
            }
            ItemStack result = new ItemStack(resultItem, count);
            return new Meatwo310CuttingRecipe(id, group, ingredient, result);
        }

        @Override
        public Meatwo310CuttingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(buf);
            ItemStack result = buf.readItem();
            return new Meatwo310CuttingRecipe(id, group, ingredient, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, Meatwo310CuttingRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            recipe.getIngredients().get(0).toNetwork(buf);
            buf.writeItem(recipe.getResultItem(RegistryAccess.EMPTY));
        }
    }
}
