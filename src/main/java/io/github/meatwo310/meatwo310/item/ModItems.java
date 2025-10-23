package io.github.meatwo310.meatwo310.item;

import io.github.meatwo310.meatwo310.Meatwo310;
import io.github.meatwo310.meatwo310.datagen.ModModelGen;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Meatwo310.MODID);
    public static final Map<String, RegistryObject<Item>> ITEM_MAP = new LinkedHashMap<>();

    public static final RegistryObject<Item> MEATWO310 = add("meatwo310", () -> new Item(new Item.Properties()));

    public static RegistryObject<Item> add(String name, Supplier<Item> sup) {
        return add(name, sup, ModModelGen::addBasicItem);
    }
    public static RegistryObject<Item> add(String name, Supplier<Item> sup, Consumer<RegistryObject<Item>> modelGenMethod) {
        RegistryObject<Item> item = ITEMS.register(name, sup);
        ITEM_MAP.put(name, item);
        modelGenMethod.accept(item);
        return item;
    }

    public static RegistryObject<Item> addBlockItem(String name, Supplier<BlockItem> blockItemSupplier) {
        RegistryObject<Item> item = ITEMS.register(name, blockItemSupplier);
        ITEM_MAP.put(name, item);
        return item;
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
