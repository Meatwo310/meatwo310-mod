package com.dousiyo.meatwo310.magicalgirl.entity;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlDamage;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlLines;
import com.dousiyo.meatwo310.magicalgirl.MagicalGirlPhase;
import com.dousiyo.meatwo310.magicalgirl.Phase3AttackState;
import com.dousiyo.meatwo310.magicalgirl.Phase3ProjectileUtil;
import com.dousiyo.meatwo310.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class ColoredLightningEntity extends Entity {
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(ColoredLightningEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RED = SynchedEntityData.defineId(ColoredLightningEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> GREEN = SynchedEntityData.defineId(ColoredLightningEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BLUE = SynchedEntityData.defineId(ColoredLightningEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> VISUAL_ONLY = SynchedEntityData.defineId(ColoredLightningEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Long> BOLT_SEED = SynchedEntityData.defineId(ColoredLightningEntity.class, EntityDataSerializers.LONG);
    private int life = 8;
    private float damage = MagicalGirlDamage.COLORED_LIGHTNING_ENTITY;
    private boolean hurtEntities;

    public ColoredLightningEntity(EntityType<? extends ColoredLightningEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.setNoGravity(true);
    }

    public ColoredLightningEntity(Level level, double x, double y, double z, float height, float red, float green, float blue, boolean visualOnly) {
        this(ModEntities.COLORED_LIGHTNING.get(), level);
        this.setPos(x, y, z);
        this.setHeight(height);
        this.setColor(red, green, blue);
        this.setVisualOnly(visualOnly);
        this.entityData.set(BOLT_SEED, this.random.nextLong());
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HEIGHT, 22.0F);
        this.entityData.define(RED, 0.45F);
        this.entityData.define(GREEN, 0.65F);
        this.entityData.define(BLUE, 1.0F);
        this.entityData.define(VISUAL_ONLY, false);
        this.entityData.define(BOLT_SEED, 0L);
    }

    @Override
    public void tick() {
        super.tick();
        this.setNoGravity(true);
        if (!this.level().isClientSide && this.entityData.get(BOLT_SEED) == 0L) {
            this.entityData.set(BOLT_SEED, this.random.nextLong());
        }
        if (!this.level().isClientSide && this.tickCount == 1) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 5.0F, 0.8F + this.random.nextFloat() * 0.2F);
            this.level().playSound(null, this.blockPosition(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
        }
        if (!this.level().isClientSide && !this.isVisualOnly() && !this.hurtEntities) {
            this.hurtEntities = true;
            hurtNearbyEntities();
        }
        if (!this.level().isClientSide && this.tickCount >= this.life) {
            this.discard();
        }
    }

    public float getLightningHeight() {
        return this.entityData.get(HEIGHT);
    }

    public float getRed() {
        return this.entityData.get(RED);
    }

    public float getGreen() {
        return this.entityData.get(GREEN);
    }

    public float getBlue() {
        return this.entityData.get(BLUE);
    }

    public long getBoltSeed() {
        return this.entityData.get(BOLT_SEED);
    }

    public boolean isVisualOnly() {
        return this.entityData.get(VISUAL_ONLY);
    }

    public void setHeight(float height) {
        this.entityData.set(HEIGHT, Mth.clamp(height, 0.6F, 64.0F));
    }

    public void setColor(float red, float green, float blue) {
        this.entityData.set(RED, Mth.clamp(red, 0.0F, 1.0F));
        this.entityData.set(GREEN, Mth.clamp(green, 0.0F, 1.0F));
        this.entityData.set(BLUE, Mth.clamp(blue, 0.0F, 1.0F));
    }

    public void setVisualOnly(boolean visualOnly) {
        this.entityData.set(VISUAL_ONLY, visualOnly);
    }

    private void hurtNearbyEntities() {
        if (!(this.level() instanceof ServerLevel)) {
            return;
        }
        AABB box = this.getBoundingBox().inflate(2.25D, 5.0D, 2.25D);
        for (LivingEntity target : this.level().getEntitiesOfClass(LivingEntity.class, box, ColoredLightningEntity::canHit)) {
            target.hurt(this.damageSources().magic(), this.damage);
        }
    }

    private static boolean canHit(LivingEntity entity) {
        return !(entity instanceof MagicalGirlBossEntity)
                && (!(entity instanceof Player player) || (!player.isCreative() && !player.isSpectator()));
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.life = tag.contains("Life", Tag.TAG_ANY_NUMERIC) ? Math.max(1, tag.getInt("Life")) : this.life;
        this.damage = tag.contains("Damage", Tag.TAG_ANY_NUMERIC) ? tag.getFloat("Damage") : this.damage;
        this.hurtEntities = tag.getBoolean("HurtEntities");
        if (tag.contains("Height", Tag.TAG_ANY_NUMERIC)) {
            setHeight(tag.getFloat("Height"));
        }
        if (tag.contains("VisualOnly")) {
            setVisualOnly(tag.getBoolean("VisualOnly"));
        }
        if (tag.contains("Seed", Tag.TAG_ANY_NUMERIC)) {
            this.entityData.set(BOLT_SEED, tag.getLong("Seed"));
        }
        if (tag.contains("Color", Tag.TAG_LIST)) {
            ListTag color = tag.getList("Color", Tag.TAG_FLOAT);
            if (color.size() >= 3) {
                setColor(color.getFloat(0), color.getFloat(1), color.getFloat(2));
            }
        } else if (tag.contains("Red", Tag.TAG_ANY_NUMERIC) || tag.contains("Green", Tag.TAG_ANY_NUMERIC) || tag.contains("Blue", Tag.TAG_ANY_NUMERIC)) {
            setColor(
                    tag.contains("Red", Tag.TAG_ANY_NUMERIC) ? tag.getFloat("Red") : getRed(),
                    tag.contains("Green", Tag.TAG_ANY_NUMERIC) ? tag.getFloat("Green") : getGreen(),
                    tag.contains("Blue", Tag.TAG_ANY_NUMERIC) ? tag.getFloat("Blue") : getBlue()
            );
        }
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("Life", this.life);
        tag.putFloat("Damage", this.damage);
        tag.putBoolean("HurtEntities", this.hurtEntities);
        tag.putFloat("Height", getLightningHeight());
        tag.putBoolean("VisualOnly", isVisualOnly());
        tag.putLong("Seed", getBoltSeed());
        ListTag color = new ListTag();
        color.add(net.minecraft.nbt.FloatTag.valueOf(getRed()));
        color.add(net.minecraft.nbt.FloatTag.valueOf(getGreen()));
        color.add(net.minecraft.nbt.FloatTag.valueOf(getBlue()));
        tag.put("Color", color);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
