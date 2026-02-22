package com.dousiyo.meatwo310.datagen;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.item.ModTabs;
import com.dousiyo.meatwo310.registry.ModItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class ModLangGen {
    protected static void register(boolean run, DataGenerator generator) {
        generator.addProvider(run, (DataProvider.Factory<EnUs>) EnUs::new);
        generator.addProvider(run, (DataProvider.Factory<JaJp>) JaJp::new);
    }

    private static class EnUs extends LanguageProvider {
        public EnUs(PackOutput output) {
            super(output, Meatwo310.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            addAll(this);
        }
    }

    private static class JaJp extends LanguageProvider {
        public JaJp(PackOutput output) {
            super(output, Meatwo310.MODID, "ja_jp");
        }

        @Override
        protected void addTranslations() {
            addAll(this);
        }
    }

    private static void addAll(LanguageProvider lp) {
        lp.add(ModTabs.TAB_ID, "Meatwo310");
        lp.add(ModItems.MEATWO310.get(), "Meatwo310");
        lp.add(ModItems.LOW_MEATWO310.get(), "Low Meatwo310");
        lp.add(ModItems.VALINE3G.get(), "Valine3g");
        lp.add(ModItems.ARUMISIA.get(), "Arumisia");
        lp.add(ModItems.TWISTER716.get(), "Twister716");
        lp.add(ModItems.CYAN_SHISHA.get(), "Cyan Shisha");
        lp.add(ModItems.PINK_SHISHA.get(), "Pink Shisha");
        lp.add(ModItems.GRAPPLER.get(), "Grappler");
        lp.add(ModItems.ICE_GRAPPLER.get(), "Ice Grappler");
        lp.add(ModItems.ENHANCED_GRAPPLER.get(), "Enhanced Grappler");
        lp.add(ModItems.IMPULSE_GRENADE.get(), "Impulse Grenade");
        lp.add(ModItems.ICE_GRENADE.get(), "Ice Grenade");
        lp.add(ModItems.HOP_GRENADE.get(), "Hop Grenade");
        lp.add(ModItems.HOP_FISH.get(), "Hop Fish");
        lp.add(ModItems.BLINK_FISH.get(), "Blink Fish");
        lp.add(ModItems.ICE_FISH.get(), "Ice Fish");

        lp.add("tooltip.meatwo310.meatwo310", "A mysterious item");
        lp.add("tooltip.meatwo310.low_meatwo310.1", "Lower grade Meatwo310");
        lp.add("tooltip.meatwo310.low_meatwo310.2", "Craft to obtain Meatwo310");
        lp.add("tooltip.meatwo310.valine3g.1", "An essential amino acid");
        lp.add("tooltip.meatwo310.valine3g.2", "Can also be eaten");
        lp.add("tooltip.meatwo310.arumisia", "Increases maximum health");
        lp.add("tooltip.meatwo310.twister716", "Edible");
        lp.add("tooltip.meatwo310.cyan_shisha", "Speed V for 2 minutes");
        lp.add("tooltip.meatwo310.pink_shisha", "Regeneration I for 2 minutes");
        lp.add("tooltip.meatwo310.grappler", "Right-click to throw and pull yourself to blocks");
        lp.add("tooltip.meatwo310.enhanced_grappler", "Grappler with no cooldown");
        lp.add("tooltip.meatwo310.ice_grappler", "Single-use grappler. Freezes you when used");
        lp.add("tooltip.meatwo310.impulse_grenade", "Explodes and launches entities in range");
        lp.add("tooltip.meatwo310.ice_grenade", "Explodes and freezes players in range");
        lp.add("tooltip.meatwo310.hop_grenade", "Explodes and gives hop effect to players in range");
        lp.add("tooltip.meatwo310.hop_fish", "Eat to gain hop effect");
        lp.add("tooltip.meatwo310.blink_fish", "Eat to gain blink effect");
        lp.add("tooltip.meatwo310.ice_fish", "Eat to gain frozen feet effect");
    }
}
