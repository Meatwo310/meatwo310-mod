package com.dousiyo.meatwo310.compat.rei;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.registry.ModBlocks;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class Meatwo310CuttingReiCategory implements DisplayCategory<Meatwo310CuttingReiDisplay> {

    @Override
    public CategoryIdentifier<? extends Meatwo310CuttingReiDisplay> getCategoryIdentifier() {
        return Meatwo310ReiPlugin.CUTTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("rei." + Meatwo310.MODID + ".meatwo310_cutting");
    }

    @Override
    public EntryStack<?> getIcon() {
        return EntryStacks.of(ModBlocks.MEATWO310_CUTTER.get());
    }

    @Override
    public List<Widget> setupDisplay(Meatwo310CuttingReiDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(Widgets.createRecipeBase(bounds));

        int x = bounds.getX();
        int y = bounds.getY();

        widgets.add(Widgets.createSlot(new Point(x + 20, y + 18))
                .entries(display.getInputEntries().get(0))
                .markInput());

        widgets.add(Widgets.createArrow(new Point(x + 48, y + 19)));

        widgets.add(Widgets.createSlot(new Point(x + 86, y + 18))
                .entries(display.getOutputEntries().get(0))
                .markOutput());

        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }

    @Override
    public int getDisplayWidth(Meatwo310CuttingReiDisplay display) {
        return 116;
    }
}
