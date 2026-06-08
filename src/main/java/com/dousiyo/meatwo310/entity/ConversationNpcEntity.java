package com.dousiyo.meatwo310.entity;

import com.dousiyo.meatwo310.registry.ModEntities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class ConversationNpcEntity extends PathfinderMob {
    private static final double MOVE_SPEED = 1.0D;
    private static final double TARGET_REACHED_DISTANCE_SQR = 0.8D;
    private static final String DEFAULT_NAME = "npc";
    private boolean hasMoveTarget;
    private double moveTargetX;
    private double moveTargetY;
    private double moveTargetZ;

    public ConversationNpcEntity(EntityType<? extends ConversationNpcEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(
            ServerLevelAccessor level,
            DifficultyInstance difficulty,
            MobSpawnType reason,
            @Nullable net.minecraft.world.entity.SpawnGroupData spawnData,
            @Nullable CompoundTag dataTag
    ) {
        net.minecraft.world.entity.SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag);
        applySpawnDefaults();
        return result;
    }

    public void moveToTarget(double x, double y, double z) {
        this.hasMoveTarget = true;
        this.moveTargetX = x;
        this.moveTargetY = y;
        this.moveTargetZ = z;
        this.getNavigation().moveTo(x, y, z, MOVE_SPEED);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.hasMoveTarget) {
            if (this.distanceToSqr(this.moveTargetX, this.moveTargetY, this.moveTargetZ) <= TARGET_REACHED_DISTANCE_SQR) {
                this.hasMoveTarget = false;
                this.getNavigation().stop();
            } else if (this.getNavigation().isDone()) {
                this.getNavigation().moveTo(this.moveTargetX, this.moveTargetY, this.moveTargetZ, MOVE_SPEED);
            }
        }
    }

    private void applySpawnDefaults() {
        if (getType() != ModEntities.MEATWO310_NPC.get()) {
            return;
        }
        if (!hasCustomName()) {
            setCustomName(Component.literal(DEFAULT_NAME));
            setCustomNameVisible(true);
        }
        equipIfPresent(EquipmentSlot.HEAD, "attacker_helmet");
        equipIfPresent(EquipmentSlot.CHEST, "attacker_chestplate");
        equipIfPresent(EquipmentSlot.LEGS, "attacker_leggings");
        equipIfPresent(EquipmentSlot.FEET, "attacker_boots");
        setPersistenceRequired();
    }

    private void equipIfPresent(EquipmentSlot slot, String itemId) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("lrarmor", itemId));
        if (item == net.minecraft.world.item.Items.AIR) {
            return;
        }
        setItemSlot(slot, new ItemStack(item));
        setDropChance(slot, 0.0F);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Meatwo310NpcHasMoveTarget", this.hasMoveTarget);
        if (this.hasMoveTarget) {
            tag.putDouble("Meatwo310NpcMoveTargetX", this.moveTargetX);
            tag.putDouble("Meatwo310NpcMoveTargetY", this.moveTargetY);
            tag.putDouble("Meatwo310NpcMoveTargetZ", this.moveTargetZ);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.hasMoveTarget = tag.getBoolean("Meatwo310NpcHasMoveTarget");
        if (this.hasMoveTarget) {
            this.moveTargetX = tag.getDouble("Meatwo310NpcMoveTargetX");
            this.moveTargetY = tag.getDouble("Meatwo310NpcMoveTargetY");
            this.moveTargetZ = tag.getDouble("Meatwo310NpcMoveTargetZ");
        }
    }
}
