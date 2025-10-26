package io.github.meatwo310.meatwo310.mixin.minecraft;

import io.github.meatwo310.meatwo310.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Explosion.class)
public class ExplosionMixin {
    @Redirect(method = "finalizeExplosion", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;canDropFromExplosion(" +
                    "Lnet/minecraft/world/level/BlockGetter;" +
                    "Lnet/minecraft/core/BlockPos;" +
                    "Lnet/minecraft/world/level/Explosion;" +
                    ")Z")
    )
    private boolean noDrop(BlockState instance, BlockGetter blockGetter, BlockPos pos, Explosion explosion) {
        return !ServerConfig.EXPLOSION_NO_DROP.get() && instance.canDropFromExplosion(blockGetter, pos, explosion);
    }
}
