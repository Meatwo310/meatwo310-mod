package com.dousiyo.meatwo310.mixin.tacz;

import com.dousiyo.meatwo310.config.ServerConfig;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Pseudo
@Mixin(targets = "com.tacz.guns.util.ExplodeUtil", remap = false)
public class ExplodeUtilMixin {
    @ModifyVariable(method = "createExplosion", at = @At(value = "STORE", ordinal = 1), remap = false)
    private static Explosion.BlockInteraction destroyWithDecay(Explosion.BlockInteraction original) {
        return ServerConfig.EXPLOSION_TACZ_DECAY_DROP.get() ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : original;
    }
}
