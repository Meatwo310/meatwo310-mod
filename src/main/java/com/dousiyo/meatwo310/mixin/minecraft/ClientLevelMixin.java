package com.dousiyo.meatwo310.mixin.minecraft;

import com.dousiyo.meatwo310.client.magicalgirl.sky.MagicalAttackSkyColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Inject(method = "getSkyColor", at = @At("RETURN"), cancellable = true)
    private void meatwo310$attackSkyColor(Vec3 cameraPos, float partialTicks, CallbackInfoReturnable<Vec3> cir) {
        ClientLevel level = (ClientLevel) (Object) this;
        cir.setReturnValue(MagicalAttackSkyColor.apply(level, cameraPos, cir.getReturnValue(), partialTicks));
    }
}
