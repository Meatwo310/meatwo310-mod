package com.dousiyo.meatwo310.registry;

import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.content.block.Meatwo310CutterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Meatwo310.MODID);

    public static final RegistryObject<Block> MEATWO310_CUTTER = BLOCKS.register(
            "meatwo310_cutter",
            () -> new Meatwo310CutterBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.5F)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
            )
    );
}
