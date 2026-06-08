package com.dousiyo.meatwo310.registry;

import com.dousiyo.meatwo310.Meatwo310;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ModFluids {
    private static final ResourceLocation WATER_STILL = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_still");
    private static final ResourceLocation WATER_FLOW = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_flow");
    private static final ResourceLocation WATER_OVERLAY = ResourceLocation.fromNamespaceAndPath("minecraft", "block/water_overlay");
    private static final ResourceLocation UNDERWATER = ResourceLocation.fromNamespaceAndPath("minecraft", "textures/misc/underwater.png");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, Meatwo310.MODID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, Meatwo310.MODID);
    public static final DeferredRegister<Block> FLUID_BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Meatwo310.MODID);

    public static final RegistryObject<FluidType> GREEN_LIQUID_TYPE =
            registerType("green_liquid", 0xF0168B31);
    public static final RegistryObject<FluidType> PURPLE_LIQUID_TYPE =
            registerType("purple_liquid", 0xF05B22A7);
    public static final RegistryObject<FluidType> LIGHT_GRAY_LIQUID_TYPE =
            registerType("light_gray_liquid", 0xCCD7DCE2);
    public static final RegistryObject<FluidType> LIGHT_BLUE_LIQUID_TYPE =
            registerType("light_blue_liquid", 0x8055CCFF);
    public static final RegistryObject<FluidType> RED_LIQUID_TYPE =
            registerType("red_liquid", 0xF0D92525);

    public static final RegistryObject<ForgeFlowingFluid.Source> GREEN_LIQUID =
            FLUIDS.register("green_liquid", () -> new ForgeFlowingFluid.Source(greenProperties()));
    public static final RegistryObject<ForgeFlowingFluid.Flowing> GREEN_LIQUID_FLOWING =
            FLUIDS.register("flowing_green_liquid", () -> new ForgeFlowingFluid.Flowing(greenProperties()));
    public static final RegistryObject<LiquidBlock> GREEN_LIQUID_BLOCK =
            FLUID_BLOCKS.register("green_liquid", () -> liquidBlock(GREEN_LIQUID));

    public static final RegistryObject<ForgeFlowingFluid.Source> PURPLE_LIQUID =
            FLUIDS.register("purple_liquid", () -> new ForgeFlowingFluid.Source(purpleProperties()));
    public static final RegistryObject<ForgeFlowingFluid.Flowing> PURPLE_LIQUID_FLOWING =
            FLUIDS.register("flowing_purple_liquid", () -> new ForgeFlowingFluid.Flowing(purpleProperties()));
    public static final RegistryObject<LiquidBlock> PURPLE_LIQUID_BLOCK =
            FLUID_BLOCKS.register("purple_liquid", () -> liquidBlock(PURPLE_LIQUID));

    public static final RegistryObject<ForgeFlowingFluid.Source> LIGHT_GRAY_LIQUID =
            FLUIDS.register("light_gray_liquid", () -> new ForgeFlowingFluid.Source(lightGrayProperties()));
    public static final RegistryObject<ForgeFlowingFluid.Flowing> LIGHT_GRAY_LIQUID_FLOWING =
            FLUIDS.register("flowing_light_gray_liquid", () -> new ForgeFlowingFluid.Flowing(lightGrayProperties()));
    public static final RegistryObject<LiquidBlock> LIGHT_GRAY_LIQUID_BLOCK =
            FLUID_BLOCKS.register("light_gray_liquid", () -> liquidBlock(LIGHT_GRAY_LIQUID));

    public static final RegistryObject<ForgeFlowingFluid.Source> LIGHT_BLUE_LIQUID =
            FLUIDS.register("light_blue_liquid", () -> new ForgeFlowingFluid.Source(lightBlueProperties()));
    public static final RegistryObject<ForgeFlowingFluid.Flowing> LIGHT_BLUE_LIQUID_FLOWING =
            FLUIDS.register("flowing_light_blue_liquid", () -> new ForgeFlowingFluid.Flowing(lightBlueProperties()));
    public static final RegistryObject<LiquidBlock> LIGHT_BLUE_LIQUID_BLOCK =
            FLUID_BLOCKS.register("light_blue_liquid", () -> liquidBlock(LIGHT_BLUE_LIQUID));

    public static final RegistryObject<ForgeFlowingFluid.Source> RED_LIQUID =
            FLUIDS.register("red_liquid", () -> new ForgeFlowingFluid.Source(redProperties()));
    public static final RegistryObject<ForgeFlowingFluid.Flowing> RED_LIQUID_FLOWING =
            FLUIDS.register("flowing_red_liquid", () -> new ForgeFlowingFluid.Flowing(redProperties()));
    public static final RegistryObject<LiquidBlock> RED_LIQUID_BLOCK =
            FLUID_BLOCKS.register("red_liquid", () -> liquidBlock(RED_LIQUID));

    private static RegistryObject<FluidType> registerType(String name, int tintColor) {
        return FLUID_TYPES.register(name, () -> new TintedWaterFluidType(
                FluidType.Properties.create()
                        .canSwim(true)
                        .canDrown(true)
                        .canExtinguish(true)
                        .canHydrate(true)
                        .supportsBoating(true)
                        .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                        .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY),
                tintColor
        ));
    }

    private static ForgeFlowingFluid.Properties greenProperties() {
        return properties(
                GREEN_LIQUID_TYPE, GREEN_LIQUID, GREEN_LIQUID_FLOWING,
                () -> ModItems.GREEN_LIQUID_BUCKET.get(), GREEN_LIQUID_BLOCK
        );
    }

    private static ForgeFlowingFluid.Properties purpleProperties() {
        return properties(
                PURPLE_LIQUID_TYPE, PURPLE_LIQUID, PURPLE_LIQUID_FLOWING,
                () -> ModItems.PURPLE_LIQUID_BUCKET.get(), PURPLE_LIQUID_BLOCK
        );
    }

    private static ForgeFlowingFluid.Properties lightGrayProperties() {
        return properties(
                LIGHT_GRAY_LIQUID_TYPE, LIGHT_GRAY_LIQUID, LIGHT_GRAY_LIQUID_FLOWING,
                () -> ModItems.LIGHT_GRAY_LIQUID_BUCKET.get(), LIGHT_GRAY_LIQUID_BLOCK
        );
    }

    private static ForgeFlowingFluid.Properties lightBlueProperties() {
        return properties(
                LIGHT_BLUE_LIQUID_TYPE, LIGHT_BLUE_LIQUID, LIGHT_BLUE_LIQUID_FLOWING,
                () -> ModItems.LIGHT_BLUE_LIQUID_BUCKET.get(), LIGHT_BLUE_LIQUID_BLOCK
        );
    }

    private static ForgeFlowingFluid.Properties redProperties() {
        return properties(
                RED_LIQUID_TYPE, RED_LIQUID, RED_LIQUID_FLOWING,
                () -> ModItems.RED_LIQUID_BUCKET.get(), RED_LIQUID_BLOCK
        );
    }

    private static ForgeFlowingFluid.Properties properties(
            Supplier<? extends FluidType> type,
            Supplier<? extends Fluid> still,
            Supplier<? extends Fluid> flowing,
            Supplier<? extends Item> bucket,
            Supplier<? extends LiquidBlock> block
    ) {
        return new ForgeFlowingFluid.Properties(type, still, flowing)
                .bucket(bucket)
                .block(block)
                .slopeFindDistance(4)
                .levelDecreasePerBlock(1)
                .tickRate(5)
                .explosionResistance(100.0F);
    }

    private static LiquidBlock liquidBlock(Supplier<? extends ForgeFlowingFluid> fluid) {
        return new LiquidBlock(fluid, BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable());
    }

    private static class TintedWaterFluidType extends FluidType {
        private final int tintColor;

        private TintedWaterFluidType(Properties properties, int tintColor) {
            super(properties);
            this.tintColor = tintColor;
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                @Override
                public ResourceLocation getStillTexture() {
                    return WATER_STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return WATER_FLOW;
                }

                @Override
                public ResourceLocation getOverlayTexture() {
                    return WATER_OVERLAY;
                }

                @Override
                public ResourceLocation getRenderOverlayTexture(net.minecraft.client.Minecraft mc) {
                    return UNDERWATER;
                }

                @Override
                public int getTintColor() {
                    return tintColor;
                }
            });
        }
    }
}
