package io.github.meatwo310.meatwo310.item;

import io.github.meatwo310.meatwo310.Meatwo310;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Meatwo310.MODID);
    public static final String TAB_ID = "item_group.%s".formatted(Meatwo310.MODID);

    public static final RegistryObject<CreativeModeTab> TAB =
            TABS.register(TAB_ID, () -> CreativeModeTab.builder()
                    .title(Component.translatable(TAB_ID))
                    .icon(() -> ModItems.MEATWO310.get().getDefaultInstance())
                    .displayItems((params, output) -> ModItems.ITEM_MAP
                            .forEach((name, item) -> output.accept(item.get()))
                    ).build()
            );

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
