package com.dousiyo.meatwo310.entity;

import com.dousiyo.meatwo310.registry.ModEntities;
import com.dousiyo.meatwo310.registry.ModItems;
import com.dousiyo.meatwo310.util.IceKnockbackExplosion;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class IceGrenade extends ThrowableItemProjectile {
    public IceGrenade(EntityType<? extends IceGrenade> type, Level level) {
        super(type, level);
    }

    public IceGrenade(double x, double y, double z, Level level) {
        super(ModEntities.ICE_GRENADE.get(), x, y, z, level);
    }

    public IceGrenade(LivingEntity shooter, Level level) {
        super(ModEntities.ICE_GRENADE.get(), shooter, level);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            this.setPos(result.getLocation());
            IceKnockbackExplosion.create(this.level(), null, this.getX(), this.getY(), this.getZ(), 5.0F);
            this.discard();
        }
    }

    @Override
    public @NotNull ItemStack getItem() {
        return this.getDefaultItem().getDefaultInstance();
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        return ModItems.ICE_GRENADE.get();
    }
}
