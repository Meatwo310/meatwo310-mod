package io.github.meatwo310.meatwo310.datagen;

import io.github.meatwo310.meatwo310.Meatwo310;
import io.github.meatwo310.meatwo310.item.ModItems;
import io.github.meatwo310.meatwo310.item.ModTabs;
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
            add(ModTabs.TAB_ID, "Meatwo310");
            add(ModItems.MEATWO310.get(), "Meatwo310");
            add(ModItems.VALINE3G.get(), "Valine3g");
        }
    }

    private static class JaJp extends LanguageProvider {
        public JaJp(PackOutput output) {
            super(output, Meatwo310.MODID, "ja_jp");
        }

        @Override
        protected void addTranslations() {

        }
    }
}
