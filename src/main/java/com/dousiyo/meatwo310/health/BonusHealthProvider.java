package com.dousiyo.meatwo310.health;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class BonusHealthProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final ResourceLocation KEY = ResourceLocation.fromNamespaceAndPath("meatwo310", "bonus_health");

    public static final Capability<IBonusHealth> CAP = CapabilityManager.get(new CapabilityToken<>(){});

    private final BonusHealthData backend = new BonusHealthData();
    private final LazyOptional<IBonusHealth> opt = LazyOptional.of(() -> backend);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        return cap == CAP ? opt.cast() : LazyOptional.empty();
    }

    @Override public CompoundTag serializeNBT() { return backend.serializeNBT(); }
    @Override public void deserializeNBT(CompoundTag nbt) { backend.deserializeNBT(nbt); }
}
