package com.dousiyo.meatwo310.registry;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.content.menu.Meatwo310TerminalMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Meatwo310.MODID);

    public static final RegistryObject<MenuType<Meatwo310TerminalMenu>> MEATWO310_TERMINAL =
            MENUS.register("meatwo310_terminal", () -> IForgeMenuType.create(Meatwo310TerminalMenu::new));
}
