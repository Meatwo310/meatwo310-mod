package com.dousiyo.meatwo310.health;

import net.minecraft.nbt.CompoundTag;

public interface IBonusHealth {
    double getBonusHealth();
    void setBonusHealth(double v);
    default void addBonus(double d) { setBonusHealth(getBonusHealth() + d); }

    boolean isInitialized();
    void setInitialized(boolean v);

    CompoundTag serializeNBT();
    void deserializeNBT(CompoundTag tag);
}
