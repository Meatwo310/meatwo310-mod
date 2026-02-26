package com.dousiyo.meatwo310.mixin.minecraft;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {

    @Shadow
    int cookingProgress;

    @Shadow
    int cookingTotalTime;

    @Inject(method = "getTotalCookTime", at = @At("RETURN"), cancellable = true)
    private static void meatwo310$setInstantCookTime(Level level, AbstractFurnaceBlockEntity furnace, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(1);
    }
}
