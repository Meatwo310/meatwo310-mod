package com.dousiyo.meatwo310.compat.rei;

import com.dousiyo.meatwo310.recipe.Meatwo310CuttingRecipe;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;

import java.util.Collections;

public class Meatwo310CuttingReiDisplay extends BasicDisplay {

    public Meatwo310CuttingReiDisplay(Meatwo310CuttingRecipe recipe) {
        super(
                EntryIngredients.ofIngredients(recipe.getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess())))
        );
    }

    @Override
    public me.shedaniel.rei.api.common.category.CategoryIdentifier<?> getCategoryIdentifier() {
        return Meatwo310ReiPlugin.CUTTING;
    }
}
