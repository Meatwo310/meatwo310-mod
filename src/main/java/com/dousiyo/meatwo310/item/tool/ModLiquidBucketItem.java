package com.dousiyo.meatwo310.item.tool;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidUtil;

import java.util.Optional;
import java.util.function.Supplier;

public class ModLiquidBucketItem extends BucketItem {
    private final Supplier<? extends FlowingFluid> fluidSupplier;

    public ModLiquidBucketItem(Supplier<? extends FlowingFluid> fluidSupplier, Item.Properties properties) {
        super(fluidSupplier, properties);
        this.fluidSupplier = fluidSupplier;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        Fluid fluid = getFluid();
        BlockHitResult hit = getPlayerPOVHitResult(level, player, fluid == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        InteractionResultHolder<ItemStack> ret = ForgeEventFactory.onBucketUse(player, level, stack, hit);
        if (ret != null) {
            return ret;
        }
        if (hit.getType() == HitResult.Type.MISS || hit.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(stack);
        }

        BlockPos clickedPos = hit.getBlockPos();
        Direction direction = hit.getDirection();
        BlockPos placePos = clickedPos.relative(direction);
        if (!level.mayInteract(player, clickedPos) || !player.mayUseItemAt(placePos, direction, stack)) {
            return InteractionResultHolder.fail(stack);
        }

        BlockState clickedState = level.getBlockState(clickedPos);
        BlockPos targetPos = canBlockContainFluid(level, clickedPos, clickedState) ? clickedPos : placePos;
        if (!emptyContents(player, level, targetPos, hit, stack)) {
            return InteractionResultHolder.fail(stack);
        }

        checkExtraContent(player, level, stack, targetPos);
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, targetPos, stack);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(getEmptySuccessItem(stack, player), level.isClientSide());
    }

    @Override
    public boolean emptyContents(@Nullable Player player, Level level, BlockPos pos, @Nullable BlockHitResult result, @Nullable ItemStack container) {
        FlowingFluid fluid = getFluid();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        boolean canReplace = state.canBeReplaced(fluid);
        boolean canPlace = state.isAir() || canReplace || canBlockContainFluid(level, pos, state);
        Optional<net.minecraftforge.fluids.FluidStack> containedFluid = Optional.ofNullable(container).flatMap(FluidUtil::getFluidContained);

        if (!canPlace) {
            return result != null && emptyContents(player, level, result.getBlockPos().relative(result.getDirection()), null, container);
        } else if (containedFluid.isPresent() && fluid.getFluidType().isVaporizedOnPlacement(level, pos, containedFluid.get())) {
            fluid.getFluidType().onVaporize(player, level, pos, containedFluid.get());
            return true;
        } else if (level.dimensionType().ultraWarm() && fluid.is(FluidTags.WATER)) {
            playVaporizeEffects(player, level, pos);
            return true;
        } else if (placeInLiquidContainer(level, pos, state, fluid)) {
            playEmptySound(player, level, pos);
            return true;
        }

        if (!level.isClientSide && canReplace && !state.liquid()) {
            level.destroyBlock(pos, true);
        }

        if (!level.setBlock(pos, fluid.defaultFluidState().createLegacyBlock(), 11) && !state.getFluidState().isSource()) {
            return false;
        }

        playEmptySound(player, level, pos);
        return true;
    }

    @Override
    public FlowingFluid getFluid() {
        return fluidSupplier.get();
    }

    @Override
    protected boolean canBlockContainFluid(Level level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();
        if (block instanceof LiquidBlockContainer container && container.canPlaceLiquid(level, pos, state, getFluid())) {
            return true;
        }

        return canWaterlogWithModLiquid(level, pos, state);
    }

    @Override
    protected void playEmptySound(@Nullable Player player, LevelAccessor level, BlockPos pos) {
        Fluid fluid = getFluid();
        SoundEvent sound = fluid.getFluidType().getSound(player, level, pos, SoundActions.BUCKET_EMPTY);
        if (sound == null) {
            sound = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        }
        level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
    }

    private boolean placeInLiquidContainer(Level level, BlockPos pos, BlockState state, FlowingFluid fluid) {
        Block block = state.getBlock();
        if (block instanceof LiquidBlockContainer container && container.canPlaceLiquid(level, pos, state, fluid)) {
            container.placeLiquid(level, pos, state, fluid.getSource(false));
            return true;
        }

        if (canWaterlogWithModLiquid(level, pos, state)) {
            if (!level.isClientSide) {
                level.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, true), 3);
                level.scheduleTick(pos, fluid, fluid.getTickDelay(level));
            }
            return true;
        }

        return false;
    }

    private boolean canWaterlogWithModLiquid(Level level, BlockPos pos, BlockState state) {
        if (!state.hasProperty(BlockStateProperties.WATERLOGGED) || state.getValue(BlockStateProperties.WATERLOGGED)) {
            return false;
        }
        Block block = state.getBlock();
        return block instanceof LiquidBlockContainer container && container.canPlaceLiquid(level, pos, state, Fluids.WATER);
    }

    private void playVaporizeEffects(@Nullable Player player, Level level, BlockPos pos) {
        level.playSound(player, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);
        for (int i = 0; i < 8; ++i) {
            level.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + Math.random(), pos.getY() + Math.random(), pos.getZ() + Math.random(), 0.0D, 0.0D, 0.0D);
        }
    }
}
