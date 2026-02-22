package com.dousiyo.meatwo310.health;

import net.minecraft.nbt.CompoundTag;

public class BonusHealthData implements IBonusHealth {
    private double bonusHealth = 0.0;
    private boolean initialized = false;

    @Override public double getBonusHealth() { return bonusHealth; }
    @Override public void setBonusHealth(double v) { bonusHealth = v; }

    @Override public boolean isInitialized() { return initialized; }
    @Override public void setInitialized(boolean v) { initialized = v; }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag t = new CompoundTag();
        t.putDouble("bonusHealth", bonusHealth);
        t.putBoolean("initialized", initialized);
        return t;
    }

    @Override
    public void deserializeNBT(CompoundTag t) {
        bonusHealth = t.getDouble("bonusHealth");
        initialized = t.getBoolean("initialized");
    }
}
