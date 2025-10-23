package io.github.meatwo310.meatwo310.datagen;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.meatwo310.Meatwo310;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModModelGen {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final List<RegistryObject<Item>> basicItems = new ArrayList<>();
    private static final List<RegistryObject<Block>> basicBlocks = new ArrayList<>();
    private static final List<RegistryObject<Block>> horizontalDirectionalBlocks = new ArrayList<>();

    protected static void register(boolean run, DataGenerator generator, PackOutput packOutput, ExistingFileHelper efh) {
        generator.addProvider(run, new ItemModel(packOutput, Meatwo310.MODID, efh));
        generator.addProvider(run, new BlockState(packOutput, Meatwo310.MODID, efh));
    }

    public static void addBasicItem(RegistryObject<Item> item) {
        basicItems.add(item);
    }

    public static void addBasicBlock(RegistryObject<Block> block) {
        basicBlocks.add(block);
    }

    public static void addHorizontalDirectionalBlock(RegistryObject<Block> block) {
        horizontalDirectionalBlocks.add(block);
    }

    private static <T> void handleModelGeneration(RegistryObject<T> registryObject, Consumer<RegistryObject<T>> consumer, String type) {
        try {
            consumer.accept(registryObject);
        } catch (Exception e) {
            ResourceLocation loc = registryObject.getId();
            LOGGER.error("Failed to generate model for {}: {}", type, loc != null ? loc.getPath() : null);
            LOGGER.error(e.getMessage());
        }
    }

    private static class ItemModel extends ItemModelProvider {
        public ItemModel(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
            super(output, modid, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            basicItems.forEach(item -> handleModelGeneration(item, i -> this.basicItem(
                    i.get()
            ), "item"));
            basicBlocks.forEach(block -> handleModelGeneration(block, b -> this.withExistingParent(
                    b.getId().getPath(),
                    modLoc("block/" + b.getId().getPath())
            ), "block"));
            horizontalDirectionalBlocks.forEach(block -> handleModelGeneration(block, b -> this.withExistingParent(
                    b.getId().getPath(),
                    modLoc("block/" + b.getId().getPath())
            ), "horizontal directional block"));
        }
    }

    private static class BlockState extends BlockStateProvider {
        public BlockState(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
            super(output, modid, existingFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            basicBlocks.forEach(block -> handleModelGeneration(block, b -> this.simpleBlock(
                    b.get(),
                    this.models().getExistingFile(modLoc("block/" + b.getId().getPath()))
            ), "block"));
            horizontalDirectionalBlocks.forEach(block -> handleModelGeneration(block, b -> this.horizontalBlock(
                    b.get(),
                    this.models().getExistingFile(modLoc("block/" + b.getId().getPath()))
            ), "horizontal directional block"));
        }
    }
}
