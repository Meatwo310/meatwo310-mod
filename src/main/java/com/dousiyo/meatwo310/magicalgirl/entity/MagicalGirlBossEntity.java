package com.dousiyo.meatwo310.magicalgirl.entity;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlDamage;

import com.dousiyo.meatwo310.magicalgirl.MagicalGirlLines;
import com.dousiyo.meatwo310.magicalgirl.MagicalGirlPhase;
import com.dousiyo.meatwo310.magicalgirl.Phase3AttackState;
import com.dousiyo.meatwo310.magicalgirl.Phase3ProjectileUtil;
import com.dousiyo.meatwo310.Meatwo310;
import com.dousiyo.meatwo310.compat.tacz.MagicTaczCompat;
import com.dousiyo.meatwo310.config.ServerConfig;
import com.dousiyo.meatwo310.registry.ModEntities;
import com.dousiyo.meatwo310.registry.ModItems;
import com.dousiyo.meatwo310.registry.ModParticles;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.init.ModAttributes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MagicalGirlBossEntity extends PathfinderMob implements GeoEntity {
    public static final String MAGIC_PHANTOM_LIFE_TAG = "Meatwo310MagicPhantomLife";
    public static final String MAGIC_PHANTOM_OWNER_TAG = "Meatwo310MagicPhantomOwner";
    public static final int MAGIC_PHANTOM_ATTACK_TICKS = 20 * 90;
    private static final int PHANTOM_REINFORCEMENT_INTERVAL = 20 * 8;
    private static final int PHASE_2_PHANTOM_CAP = 10;
    private static final int FINAL_PHASE_PHANTOM_CAP = 14;
    public static final String MAGIC_RAVAGER_OWNER_TAG = "Meatwo310MagicRavagerOwner";
    private static final int SPECIAL_ATTACK_CYCLE_TICKS = 20 * 180;
    private static final int MAGIC_RAVAGER_ATTACK_TICKS = SPECIAL_ATTACK_CYCLE_TICKS;
    private static final double RIDING_PHANTOM_HEALTH = 1024.0D;
    private static final double PHASE_1_BULLET_RESISTANCE = 0.5D;
    private static final double PHASE_2_BULLET_RESISTANCE = 0.75D;
    private static final double FINAL_PHASE_BULLET_RESISTANCE = 0.9D;
    private static final double PHANTOM_RIDING_BULLET_RESISTANCE = 1.0D;
    private static final int SEAL_ESCAPE_QUAKE_TICKS = 70;
    private static final int SEAL_ESCAPE_MOVE_TICKS = 100;
    private static final int SEAL_ESCAPE_TICKS = SEAL_ESCAPE_QUAKE_TICKS + SEAL_ESCAPE_MOVE_TICKS;
    private static final int INTRO_LINE_INTERVAL_TICKS = 40;
    private static final int BREAK_LINE_INTERVAL_TICKS = 50;
    private static final int COMBAT_LINE_INTERVAL_TICKS = 20 * 10;
    private static final int DEFEAT_LINE_INTERVAL_TICKS = 50;
    private static final int FLASHBACK_LINE_INTERVAL_TICKS = 70;
    private static final double SEAL_ESCAPE_WEST_DISTANCE = 40.0D;
    private static final double SEAL_ESCAPE_TARGET_Y = 100.0D;
    private static final double SEAL_ESCAPE_BREAK_RADIUS = 3.0D;
    private static final double SEAL_WAIT_TRIGGER_RANGE = 30.0D;
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(MagicalGirlBossEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> RED_EYES = SynchedEntityData.defineId(MagicalGirlBossEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CAST_ANIM_TICKS = SynchedEntityData.defineId(MagicalGirlBossEntity.class, EntityDataSerializers.INT);
    private static final TargetingConditions PLAYER_TARGETING = TargetingConditions.forCombat().range(80.0D).selector(MagicalGirlBossEntity::canTarget);
    private static final ResourceLocation FINAL_MAGIC_GUN = ResourceLocation.fromNamespaceAndPath("tacz", "aa12");
    private static final int[] FINAL_MAGIC_TRACER = new int[]{255, 45, 180, 255};
    private static final float[][] PHASE_2_LIGHTNING_COLORS = new float[][]{
            {1.00F, 0.22F, 0.36F},
            {1.00F, 0.68F, 0.16F},
            {1.00F, 0.95F, 0.24F},
            {0.25F, 1.00F, 0.42F},
            {0.28F, 0.84F, 1.00F},
            {0.62F, 0.42F, 1.00F},
            {1.00F, 0.38F, 0.92F}
    };
    private static final RawAnimation IDLE_FLOATING_ANIM = RawAnimation.begin().thenLoop("idle_floating");
    private static final RawAnimation MOVE_FLOATING_ANIM = RawAnimation.begin().thenLoop("move_floating");
    private static final RawAnimation RIDING_IDLE_ANIM = RawAnimation.begin().thenLoop("riding_idle");
    private static final RawAnimation DEFEAT_FALL_ANIM = RawAnimation.begin().thenPlayAndHold("defeat_fall");
    private static final RawAnimation DEFEAT_IDLE_ANIM = RawAnimation.begin().thenLoop("defeat_idle");
    private static final RawAnimation CAST_MAGIC_ANIM = RawAnimation.begin().thenPlay("cast_magic");
    private static final RawAnimation CAST_BIG_MAGIC_ANIM = RawAnimation.begin().thenPlay("cast_big_magic");
    private static final String CAST_CONTROLLER = "cast";
    private static final String CAST_MAGIC_TRIGGER = "cast_magic";
    private static final String CAST_BIG_MAGIC_TRIGGER = "cast_big_magic";
    private final ServerBossEvent bossEvent = new ServerBossEvent(Component.literal("魔法少女"), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS);
    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int stateTicks;
    private int attackCooldown = 80;
    private int lineIndex;
    private boolean endFunctionExecuted;
    private double anchorX = Double.NaN;
    private double anchorY = Double.NaN;
    private double anchorZ = Double.NaN;
    private ItemStack hiddenTaczGun = ItemStack.EMPTY;
    private UUID ridingPhantomUuid;
    private int phantomAttackTicks;
    private UUID ridingRavagerUuid;
    private int ravagerAttackTicks;
    private int endedSpecialCycle = -1;
    private Vec3 airMoveTarget = Vec3.ZERO;
    private int airMoveTargetTicks;
    private float sealedYaw = Float.NaN;
    private Vec3 sealEscapeStart = Vec3.ZERO;
    private Vec3 sealEscapeTarget = Vec3.ZERO;
    private final List<NatureAction> natureActions = new ArrayList<>();
    private final List<Phase3Action> phase3Actions = new ArrayList<>();
    private final List<UUID> phase3MineIds = new ArrayList<>();
    private final List<UUID> phantomSummonIds = new ArrayList<>();
    private final List<UUID> ravagerSummonIds = new ArrayList<>();
    private boolean usedGrandBlossom;
    private boolean usedGrandSpellNova;
    private boolean usedEmergencyExecution;

    public MagicalGirlBossEntity(EntityType<? extends MagicalGirlBossEntity> type, Level level) {
        super(type, level);
        this.xpReward = 80;
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 5000.0D)
                .add(Attributes.ATTACK_DAMAGE, MagicalGirlDamage.BOSS_MELEE_ATTRIBUTE)
                .add(Attributes.ARMOR, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.FOLLOW_RANGE, 48.0D)
                .add(Attributes.FLYING_SPEED, 0.34D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D)
                .add(ModAttributes.BULLET_RESISTANCE.get(), 0.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 16.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, MagicalGirlBossEntity::canTarget));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(PHASE, MagicalGirlPhase.SEALED.ordinal());
        this.entityData.define(RED_EYES, false);
        this.entityData.define(CAST_ANIM_TICKS, 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide) {
            return;
        }

        this.stateTicks++;
        int castAnimTicks = this.entityData.get(CAST_ANIM_TICKS);
        if (castAnimTicks > 0) {
            this.entityData.set(CAST_ANIM_TICKS, castAnimTicks - 1);
        }
        this.setNoGravity(true);
        initHiddenTaczGun();
        if (!canTarget(this.getTarget())) {
            this.setTarget(null);
        }
        initializeAnchor();
        maintainAirPosition();
        updateBossBar();

        MagicalGirlPhase phase = getPhase();
        if (phase == MagicalGirlPhase.SEALED) {
            tickSealed();
            return;
        } else if (phase == MagicalGirlPhase.SEAL_ESCAPE) {
            tickSealEscape();
            return;
        } else if (phase == MagicalGirlPhase.SEAL_WAIT) {
            tickSealWait();
            return;
        }
        tickNatureActions();
        tickPhase3Actions();
        cleanupPhase3Mines(false);
        tickPhantomAttack(phase);
        tickRavagerAttack(phase);
        if (phase == MagicalGirlPhase.INTRO) {
            tickIntro();
        } else if (phase == MagicalGirlPhase.BREAK_1 || phase == MagicalGirlPhase.BREAK_2) {
            tickBreak();
        } else if (phase == MagicalGirlPhase.DEFEAT) {
            tickDefeat();
        } else if (phase == MagicalGirlPhase.FLASHBACK) {
            tickFlashback();
        } else if (phase == MagicalGirlPhase.PHASE_1 || phase == MagicalGirlPhase.PHASE_2 || phase == MagicalGirlPhase.FINAL_PHASE) {
            tickCombatPhase(phase);
        } else if (phase == MagicalGirlPhase.END_HOOK) {
            executeEndFunctionOnce();
            this.discard();
        }
    }

    private void tickSealed() {
        this.setInvulnerable(true);
        freezeSealedRotation();
        this.setDeltaMovement(0.0D, 0.0D, 0.0D);
        this.hasImpulse = true;
        if (this.level() instanceof ServerLevel serverLevel && this.stateTicks % 24 == 0) {
            serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 1.0D, this.getZ(), 3, 0.45D, 0.8D, 0.45D, 0.01D);
            serverLevel.sendParticles(ParticleTypes.ENCHANT, this.getX(), this.getY() + 1.2D, this.getZ(), 12, 1.0D, 0.85D, 1.0D, 0.02D);
            spawnSealedAuraParticles(serverLevel);
        }
    }

    private void spawnSealedAuraParticles(ServerLevel serverLevel) {
        for (int i = 0; i < 24; i++) {
            double angle = this.random.nextDouble() * Math.PI * 2.0D;
            double radius = 5.0D + this.random.nextDouble() * 25.0D;
            double x = this.getX() + Math.cos(angle) * radius;
            double z = this.getZ() + Math.sin(angle) * radius;
            double y = this.getY() + 0.4D + this.random.nextDouble() * 7.0D;
            double inwardX = (this.getX() - x) * 0.0025D;
            double inwardZ = (this.getZ() - z) * 0.0025D;
            serverLevel.sendParticles(ParticleTypes.ENCHANT, x, y, z, 1, inwardX, 0.025D, inwardZ, 0.025D);
            if (i % 4 == 0) {
                serverLevel.sendParticles(ParticleTypes.END_ROD, x, y + 0.1D, z, 1, 0.04D, 0.06D, 0.04D, 0.004D);
            }
            if (i % 8 == 0) {
                serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, x, y, z, 1, 0.015D, 0.03D, 0.015D, 0.006D);
            }
        }
    }

    private void tickSealEscape() {
        this.setInvulnerable(true);
        this.setNoGravity(true);
        if (this.stateTicks == 1 || this.sealEscapeStart == Vec3.ZERO) {
            this.sealEscapeStart = this.position();
            this.sealEscapeTarget = !Double.isNaN(this.anchorX)
                    ? new Vec3(this.anchorX, this.anchorY, this.anchorZ)
                    : defaultSealEscapeTarget();
        }

        double moveProgress = sealEscapeMoveProgress(0.0F);
        double eased = 1.0D - Math.pow(1.0D - moveProgress, 3.0D);
        Vec3 nextPos = this.sealEscapeStart.lerp(this.sealEscapeTarget, eased);
        this.teleportTo(nextPos.x, nextPos.y, nextPos.z);
        this.setDeltaMovement(0.0D, 0.0D, 0.0D);
        this.hasImpulse = true;

        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.stateTicks > SEAL_ESCAPE_QUAKE_TICKS) {
                carveSealEscapePath(serverLevel, nextPos);
                spawnSealEscapeParticles(serverLevel, moveProgress);
            }
            if (this.stateTicks == 1) {
                serverLevel.playSound(null, this.blockPosition(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.HOSTILE, 2.0F, 0.55F);
            } else if (this.stateTicks == SEAL_ESCAPE_QUAKE_TICKS + 1) {
                serverLevel.playSound(null, this.blockPosition(), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 2.2F, 0.65F);
            }
        }

        if (this.stateTicks >= SEAL_ESCAPE_TICKS) {
            this.teleportTo(this.sealEscapeTarget.x, this.sealEscapeTarget.y, this.sealEscapeTarget.z);
            this.anchorX = this.sealEscapeTarget.x;
            this.anchorY = this.sealEscapeTarget.y;
            this.anchorZ = this.sealEscapeTarget.z;
            this.airMoveTarget = this.position();
            this.airMoveTargetTicks = 45;
            this.sealEscapeStart = Vec3.ZERO;
            this.sealEscapeTarget = Vec3.ZERO;
            enterPhase(MagicalGirlPhase.SEAL_WAIT);
        }
    }

    private void tickSealWait() {
        this.setInvulnerable(true);
        this.setNoGravity(true);
        if (!Double.isNaN(this.anchorX)) {
            this.teleportTo(this.anchorX, this.anchorY, this.anchorZ);
        }
        this.setDeltaMovement(0.0D, 0.0D, 0.0D);
        this.hasImpulse = true;

        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.stateTicks % 20 == 0) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT, this.getX(), this.getY() + 1.0D, this.getZ(), 12, 1.4D, 0.9D, 1.4D, 0.03D);
                serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 0.9D, this.getZ(), 4, 0.7D, 0.45D, 0.7D, 0.012D);
            }
            Player nearby = serverLevel.getNearestPlayer(
                    TargetingConditions.forCombat().range(SEAL_WAIT_TRIGGER_RANGE).selector(MagicalGirlBossEntity::canTarget),
                    this
            );
            if (nearby != null) {
                this.setInvulnerable(false);
                this.setTarget(nearby);
                enterPhase(MagicalGirlPhase.PHASE_1);
            }
        }
    }

    private double sealEscapeMoveProgress(float partialTicks) {
        return Mth.clamp((this.stateTicks + partialTicks - SEAL_ESCAPE_QUAKE_TICKS) / (double) SEAL_ESCAPE_MOVE_TICKS, 0.0D, 1.0D);
    }

    private void carveSealEscapePath(ServerLevel serverLevel, Vec3 center) {
        int radius = Mth.ceil(SEAL_ESCAPE_BREAK_RADIUS);
        int minY = Mth.floor(center.y) - radius;
        int maxY = Mth.floor(center.y) + radius;
        minY = Math.max(serverLevel.getMinBuildHeight(), minY);
        maxY = Math.min(serverLevel.getMaxBuildHeight() - 1, maxY);

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int x = Mth.floor(center.x) - radius; x <= Mth.floor(center.x) + radius; x++) {
            for (int z = Mth.floor(center.z) - radius; z <= Mth.floor(center.z) + radius; z++) {
                for (int y = minY; y <= maxY; y++) {
                    double dx = x + 0.5D - center.x;
                    double dy = y + 0.5D - center.y;
                    double dz = z + 0.5D - center.z;
                    if (dx * dx + dy * dy + dz * dz > SEAL_ESCAPE_BREAK_RADIUS * SEAL_ESCAPE_BREAK_RADIUS) {
                        continue;
                    }
                    mutable.set(x, y, z);
                    BlockState state = serverLevel.getBlockState(mutable);
                    if (state.isAir() || state.getDestroySpeed(serverLevel, mutable) < 0.0F) {
                        continue;
                    }
                    serverLevel.setBlock(mutable, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    private void spawnSealEscapeParticles(ServerLevel serverLevel, double progress) {
        serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 1.0D, this.getZ(), 28, 1.5D + progress * 2.0D, 1.0D, 1.5D + progress * 2.0D, 0.08D);
        serverLevel.sendParticles(ParticleTypes.ENCHANT, this.getX(), this.getY() + 1.2D, this.getZ(), 36, 2.4D, 1.2D, 2.4D, 0.05D);
    }

    private void freezeSealedRotation() {
        if (Float.isNaN(this.sealedYaw)) {
            this.sealedYaw = this.getYRot();
        }
        this.setYRot(this.sealedYaw);
        this.yBodyRot = this.sealedYaw;
        this.yHeadRot = this.sealedYaw;
        this.yRotO = this.sealedYaw;
        this.yBodyRotO = this.sealedYaw;
        this.yHeadRotO = this.sealedYaw;
    }


    private void tickIntro() {
        this.setInvulnerable(true);
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.stateTicks == 1) {
                serverLevel.playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.6F, 0.65F);
                serverLevel.playSound(null, this.blockPosition(), SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.HOSTILE, 1.2F, 1.45F);
                serverLevel.sendParticles(ParticleTypes.FLASH, this.getX(), this.getY() + 1.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 1.0D, this.getZ(), 24, 1.5D, 1.0D, 1.5D, 0.03D);
            serverLevel.sendParticles(ParticleTypes.WITCH, this.getX(), this.getY() + 0.2D, this.getZ(), 18, 2.0D, 0.2D, 2.0D, 0.01D);
        }
        if (this.stateTicks % INTRO_LINE_INTERVAL_TICKS == 1) {
            sayNextLine();
        }
        if (this.stateTicks >= 130) {
            this.setInvulnerable(false);
            enterPhase(MagicalGirlPhase.PHASE_1);
        }
    }

    private void tickBreak() {
        this.setInvulnerable(true);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.PORTAL, this.getX(), this.getY() + 1.0D, this.getZ(), 36, 1.2D, 1.0D, 1.2D, 0.08D);
        }
        if (this.stateTicks % BREAK_LINE_INTERVAL_TICKS == 1) {
            sayNextLine();
        }
        double targetHealth = phaseMaxHealth(nextCombatPhase(getPhase()));
        double healed = Math.min(targetHealth, this.getHealth() + targetHealth / 90.0D);
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(targetHealth);
        this.setHealth((float) healed);
        if (this.stateTicks >= 120) {
            this.setInvulnerable(false);
            enterPhase(nextCombatPhase(getPhase()));
        }
    }

    private void tickCombatPhase(MagicalGirlPhase phase) {
        if (this.tickCount % COMBAT_LINE_INTERVAL_TICKS == 20) {
            sayNextLine();
        }
        if (isSpecialAttackCycle()) {
            tickSpecialAttackCycle(phase);
            return;
        }
        endPhantomAttack(true);
        endRavagerAttack(true);
        if (--this.attackCooldown <= 0) {
            performAttack(phase);
            this.attackCooldown = nextAttackCooldown(phase);
        }
    }

    private void tickSpecialAttackCycle(MagicalGirlPhase phase) {
        Player target = this.level().getNearestPlayer(PLAYER_TARGETING, this);
        int cycle = currentSpecialCycle();
        if (target != null && this.endedSpecialCycle != cycle) {
            if (phase == MagicalGirlPhase.PHASE_1) {
                this.endedSpecialCycle = cycle;
            } else if (phase == MagicalGirlPhase.PHASE_2 && !hasActivePhantomAttack()) {
                spawnPhantomSwarm(target);
            } else if (phase == MagicalGirlPhase.FINAL_PHASE) {
                castEmergencyExecution(target);
                this.endedSpecialCycle = cycle;
            }
        }
        this.attackCooldown = nextAttackCooldown(phase);
    }

    private int nextAttackCooldown(MagicalGirlPhase phase) {
        int playerCount = Math.max(1, combatTargets(6).size());
        int base = phase == MagicalGirlPhase.FINAL_PHASE ? 58 : 78;
        int reduction = Math.min(32, (playerCount - 1) * (phase == MagicalGirlPhase.FINAL_PHASE ? 5 : 6));
        return Math.max(phase == MagicalGirlPhase.FINAL_PHASE ? 32 : 40, base - reduction);
    }

    private boolean isSpecialAttackCycle() {
        int cycle = currentSpecialCycle();
        return cycle % 2 == 0 && this.endedSpecialCycle != cycle;
    }

    private int currentSpecialCycle() {
        return this.stateTicks / SPECIAL_ATTACK_CYCLE_TICKS;
    }

    private void maintainAirPosition() {
        MagicalGirlPhase phase = getPhase();
        if (phase == MagicalGirlPhase.SEALED || phase == MagicalGirlPhase.SEAL_ESCAPE || phase == MagicalGirlPhase.SEAL_WAIT || phase == MagicalGirlPhase.DEFEAT || phase == MagicalGirlPhase.FLASHBACK || phase == MagicalGirlPhase.END_HOOK) {
            this.setDeltaMovement(0.0D, 0.0D, 0.0D);
            return;
        }

        if (this.isPassenger()) {
            LivingEntity target = this.getTarget();
            if (target != null && canTarget(target)) {
                faceTarget(target);
            }
            return;
        }

        LivingEntity target = findLookTarget();
        if (this.airMoveTargetTicks-- <= 0 || this.airMoveTarget == Vec3.ZERO || this.position().distanceToSqr(this.airMoveTarget) < 1.0D) {
            this.airMoveTarget = chooseAirMoveTarget(phase, target);
            this.airMoveTargetTicks = 20 + this.random.nextInt(18);
        }
        Vec3 toMoveTarget = this.airMoveTarget.subtract(this.position());
        double distance = toMoveTarget.length();
        if (distance > 0.05D) {
            double speed = phase == MagicalGirlPhase.FINAL_PHASE ? 0.34D : 0.26D;
            Vec3 desired = toMoveTarget.normalize().scale(Math.min(speed, distance * 0.12D));
            this.setDeltaMovement(this.getDeltaMovement().scale(0.72D).add(desired.scale(0.28D)));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().scale(0.72D));
        }
        if (target != null) {
            faceTarget(target);
        }
        this.hasImpulse = true;
    }

    private Vec3 chooseAirMoveTarget(MagicalGirlPhase phase, @Nullable LivingEntity target) {
        double angle = this.random.nextDouble() * Math.PI * 2.0D;
        double centerX = this.anchorX;
        double centerY = this.anchorY;
        double centerZ = this.anchorZ;
        double radius = phase == MagicalGirlPhase.INTRO || phase == MagicalGirlPhase.BREAK_1 || phase == MagicalGirlPhase.BREAK_2 ? 0.8D : 2.6D;
        if (target != null && (phase == MagicalGirlPhase.PHASE_1 || phase == MagicalGirlPhase.PHASE_2 || phase == MagicalGirlPhase.FINAL_PHASE)) {
            centerY = Math.max(this.anchorY, target.getY() + (phase == MagicalGirlPhase.FINAL_PHASE ? 12.0D : 10.0D));
            radius = phase == MagicalGirlPhase.FINAL_PHASE ? 16.0D : 12.0D;
        }
        return new Vec3(
                centerX + Math.cos(angle) * radius,
                centerY + (this.random.nextDouble() * 1.5D - 0.75D),
                centerZ + Math.sin(angle) * radius
        );
    }

    @Nullable
    private LivingEntity findLookTarget() {
        LivingEntity target = this.getTarget();
        if (target != null && canTarget(target)) {
            return target;
        }
        Player nearest = this.level().getNearestPlayer(PLAYER_TARGETING, this);
        if (nearest != null) {
            this.setTarget(nearest);
        }
        return nearest;
    }

    private void faceTarget(LivingEntity target) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        float yaw = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        this.setYRot(yaw);
        this.yBodyRot = yaw;
        this.yHeadRot = yaw;
        this.yRotO = yaw;
        this.yBodyRotO = yaw;
        this.yHeadRotO = yaw;
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
    }

    private void initializeAnchor() {
        if (!Double.isNaN(this.anchorX)) {
            return;
        }
        this.anchorX = this.getX();
        this.anchorY = this.getY() + 8.0D;
        this.anchorZ = this.getZ();
        if (getPhase() != MagicalGirlPhase.SEALED) {
            this.teleportTo(this.anchorX, this.anchorY, this.anchorZ);
        }
    }

    private void tickDefeat() {
        this.setInvulnerable(true);
        double groundY = defeatGroundY();
        descendAfterDefeat(groundY, 0.035D);
        if (this.stateTicks % DEFEAT_LINE_INTERVAL_TICKS == 1) {
            sayNextLine();
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.stateTicks == 1) {
                playDefeatBurst(serverLevel);
            }
            if (this.stateTicks % 45 == 0) {
                spawnVisualLightning(serverLevel, 4.5D);
                serverLevel.playSound(null, this.blockPosition(), SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.HOSTILE, 0.8F, 0.75F + this.random.nextFloat() * 0.25F);
            }
            if (this.stateTicks % 12 == 0) {
                serverLevel.sendParticles(ParticleTypes.SOUL, this.getX(), this.getY() + 1.0D, this.getZ(), 3, 0.45D, 0.55D, 0.45D, 0.018D);
                serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getY() + 1.2D, this.getZ(), 2, 0.55D, 0.35D, 0.55D, 0.025D);
            }
        }
        if (hasReachedDefeatGround(groundY)) {
            enterPhase(MagicalGirlPhase.FLASHBACK);
        }
    }

    private void tickFlashback() {
        descendAfterDefeat(defeatGroundY(), 0.025D);
        if (this.stateTicks % FLASHBACK_LINE_INTERVAL_TICKS == 1) {
            sayNextLine();
        }
        if (this.level() instanceof ServerLevel serverLevel) {
            if (this.stateTicks == 1) {
                serverLevel.playSound(null, this.blockPosition(), SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 1.5F, 0.55F);
            }
            if (this.stateTicks % 70 == 0) {
                spawnVisualLightning(serverLevel, 3.0D);
            }
            if (this.stateTicks % 16 == 0) {
                serverLevel.sendParticles(ParticleTypes.ENCHANT, this.getX(), this.getY() + 1.0D, this.getZ(), 4, 0.8D, 0.55D, 0.8D, 0.025D);
                serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, this.getX(), this.getY() + 1.0D, this.getZ(), 2, 0.55D, 0.35D, 0.55D, 0.018D);
            }
        }
    }

    private void descendAfterDefeat(double targetY, double maxStep) {
        if (Double.isNaN(this.anchorX)) {
            return;
        }
        double nextY = Math.max(targetY, this.getY() - maxStep);
        this.teleportTo(this.anchorX, nextY, this.anchorZ);
        this.setDeltaMovement(0.0D, 0.0D, 0.0D);
        this.hasImpulse = true;
    }

    private double defeatGroundY() {
        if (Double.isNaN(this.anchorX)) {
            return this.getY();
        }
        int groundY = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(this.anchorX), Mth.floor(this.anchorZ));
        return Math.min(this.anchorY - 8.0D, groundY);
    }

    private boolean hasReachedDefeatGround(double groundY) {
        return this.getY() <= groundY + 0.001D;
    }

    private void playDefeatBurst(ServerLevel serverLevel) {
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.WITHER_DEATH, SoundSource.HOSTILE, 1.6F, 1.35F);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.8F, 0.85F);
        serverLevel.sendParticles(ParticleTypes.FLASH, this.getX(), this.getY() + 1.0D, this.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.FIREWORK, this.getX(), this.getY() + 1.0D, this.getZ(), 12, 1.2D, 0.8D, 1.2D, 0.08D);
        serverLevel.sendParticles(ParticleTypes.WITCH, this.getX(), this.getY() + 1.0D, this.getZ(), 8, 1.4D, 0.8D, 1.4D, 0.06D);
        for (int i = 0; i < 1; i++) {
            spawnVisualLightning(serverLevel, 5.5D);
        }
    }

    private void spawnVisualLightning(ServerLevel serverLevel, double radius) {
        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
        if (lightning == null) {
            return;
        }
        double angle = this.random.nextDouble() * Math.PI * 2.0D;
        double distance = 1.0D + this.random.nextDouble() * radius;
        lightning.moveTo(this.getX() + Math.cos(angle) * distance, this.getY(), this.getZ() + Math.sin(angle) * distance);
        lightning.setVisualOnly(true);
        serverLevel.addFreshEntity(lightning);
    }

    private void performAttack(MagicalGirlPhase phase) {
        List<Player> targets = combatTargets(6);
        if (targets.isEmpty()) {
            return;
        }
        Player target = targets.get(this.random.nextInt(targets.size()));

        if (hasActivePhantomAttack() || hasActiveRavagerAttack()) {
            return;
        }
        if (phase == MagicalGirlPhase.PHASE_1) {
            if (!this.usedGrandBlossom && this.getHealth() / this.getMaxHealth() <= 0.72F) {
                castGrandBlossom(target);
                this.usedGrandBlossom = true;
            } else {
                float roll = this.random.nextFloat();
                if (roll < 0.20F) {
                    for (Player player : chooseTargets(targets, targets.size() >= 4 ? 2 : 1)) {
                        castTornado(player.position());
                    }
                } else {
                    castNatureAttack(target);
                    if (targets.size() >= 3) {
                        castNatureAttack(chooseDifferentTarget(targets, target));
                    }
                    if (targets.size() >= 5 && this.random.nextFloat() < 0.45F) {
                        castBloomCircle(chooseDifferentTarget(targets, target).position());
                    }
                }
            }
        } else if (phase == MagicalGirlPhase.PHASE_2) {
            if (!this.usedGrandSpellNova && this.getHealth() / this.getMaxHealth() <= 0.45F) {
                castGrandSpellNova(target);
                this.usedGrandSpellNova = true;
            } else {
                float roll = this.random.nextFloat();
                if (roll < 0.18F) {
                    castGroundLightningAtNearbyPlayers();
                } else if (roll < 0.38F) {
                    for (Player player : chooseTargets(targets, targets.size() >= 4 ? 2 : 1)) {
                        castColoredLightningStorm(player);
                    }
                } else {
                    castPhase2MagicAttack(targets);
                }
            }
        } else {
            castPhase3ModernAttack(targets);
        }
    }

    private boolean hasActivePhantomAttack() {
        return this.ridingPhantomUuid != null;
    }

    private boolean hasActiveRavagerAttack() {
        return this.ridingRavagerUuid != null;
    }

    public boolean debugCastAttack(String attack, @Nullable Player forcedTarget) {
        Player target = forcedTarget != null ? forcedTarget : this.level().getNearestPlayer(PLAYER_TARGETING, this);
        Vec3 targetPos = target != null ? target.position() : this.position().add(0.0D, -6.0D, 0.0D);
        String normalized = attack.toLowerCase(java.util.Locale.ROOT);

        switch (normalized) {
            case "phase1", "p1" -> {
                spawnHazard(targetPos, 70, 28, 2.8F, MagicalGirlDamage.BASIC_HAZARD, 0);
                if (target != null) {
                    blinkNear(target);
                }
                return true;
            }
            case "phase2", "p2" -> {
                if (target == null) {
                    return false;
                }
                castStellaBurst(target);
                castDiamondSpinner(target);
                return true;
            }
            case "final", "final_combo", "p3" -> {
                if (target != null) {
                    castEmergencyExecution(target);
                }
                return true;
            }
            case "phantoms", "phantom_swarm", "phantom" -> {
                if (target == null) {
                    return false;
                }
                spawnPhantomSwarm(target);
                return true;
            }
            case "ravager", "ravager_raid", "vindicator_raid" -> {
                return false;
            }
            case "final_bullets", "tacz_bullets", "bullets" -> {
                if (target == null) {
                    return false;
                }
                initHiddenTaczGun();
                castLockOnBurst(target);
                return true;
            }
            case "lockon_burst", "lockon", "burst" -> {
                if (target == null) {
                    return false;
                }
                castLockOnBurst(target);
                return true;
            }
            case "red_laser", "laser", "phase3_laser" -> {
                if (target == null) {
                    return false;
                }
                castPhase3RedLaser(target);
                return true;
            }
            case "chemical_spray", "chemical", "spray" -> {
                if (target == null) {
                    return false;
                }
                castChemicalSpray(target);
                return true;
            }
            case "reaction_bullet", "reaction" -> {
                if (target == null) {
                    return false;
                }
                castReactionBullet(target);
                return true;
            }
            case "injection_needle", "needle", "injection" -> {
                if (target == null) {
                    return false;
                }
                castInjectionNeedle(target);
                return true;
            }
            case "suppression_rain", "suppression" -> {
                if (target == null) {
                    return false;
                }
                castSuppressionRain(target);
                return true;
            }
            case "summoned_arsenal", "arsenal", "summon_arsenal" -> {
                if (target == null) {
                    return false;
                }
                castSummonedArsenal(target, 8, 0);
                return true;
            }
            case "mine_scatter", "mines", "mine" -> {
                if (target == null) {
                    return false;
                }
                castMineScatter(target, 8, 20 * 10);
                return true;
            }
            case "emergency_execution", "execution" -> {
                if (target == null) {
                    return false;
                }
                castEmergencyExecution(target);
                return true;
            }
            case "hand", "right_hand" -> {
                spawnHand(targetPos);
                return true;
            }
            case "hazard_magic", "hazard0" -> {
                spawnHazard(targetPos, 70, 28, 2.8F, MagicalGirlDamage.BASIC_HAZARD, 0);
                return true;
            }
            case "ground_lightning", "lightning_ground", "electric_ground" -> {
                if (target == null) {
                    return false;
                }
                spawnGroundLightning(target);
                return true;
            }
            case "colored_lightning", "rainbow_lightning", "color_lightning", "lightning_colors" -> {
                if (target == null) {
                    return false;
                }
                castColoredLightningStorm(target);
                return true;
            }
            case "stella_burst", "stella", "star_burst" -> {
                if (target == null) {
                    return false;
                }
                castStellaBurst(target);
                return true;
            }
            case "diamond_spinner", "diamond", "spinner" -> {
                if (target == null) {
                    return false;
                }
                castDiamondSpinner(target);
                return true;
            }
            case "ribbon_judgement", "ribbon_judgment", "ribbon" -> {
                if (target == null) {
                    return false;
                }
                castRibbonJudgement(target);
                return true;
            }
            case "rune_cage", "rune" -> {
                castRuneCage(targetPos);
                return true;
            }
            case "magic_mirror", "mirror" -> {
                if (target == null) {
                    return false;
                }
                castMagicMirror(target);
                return true;
            }
            case "grand_spell_nova", "spell_nova", "nova" -> {
                if (target == null) {
                    return false;
                }
                castGrandSpellNova(target);
                return true;
            }
            case "root_lance", "root" -> {
                if (target == null) {
                    return false;
                }
                castRootLance(target);
                return true;
            }
            case "bloom_circle", "bloom" -> {
                castBloomCircle(targetPos);
                return true;
            }
            case "ivy_bind", "ivy" -> {
                castIvyBind(targetPos);
                return true;
            }
            case "petal_gale", "petal" -> {
                if (target == null) {
                    return false;
                }
                castPetalGale(target);
                return true;
            }
            case "twister716" -> {
                castTornado(targetPos);
                return true;
            }
            case "sakura_claw", "cherry_claw", "claw" -> {
                if (target == null) {
                    return false;
                }
                castSakuraClaw(target);
                return true;
            }
            case "sakura_rain", "cherry_rain", "petal_rain" -> {
                castSakuraRain(targetPos);
                return true;
            }
            case "grand_blossom", "blossom" -> {
                if (target == null) {
                    return false;
                }
                castGrandBlossom(target);
                return true;
            }
            case "hazard_gas", "hazard1" -> {
                spawnHazard(targetPos, 95, 22, 3.4F, MagicalGirlDamage.GAS_HAZARD, 1);
                return true;
            }
            case "hazard_final", "hazard2" -> {
                spawnHazard(targetPos, 100, 18, 4.0F, MagicalGirlDamage.FINAL_HAZARD, 2);
                return true;
            }
            case "yellow_smoke", "yellow_ignition", "ignite_smoke", "hazard_yellow", "hazard3" -> {
                spawnYellowIgnition(targetPos);
                return true;
            }
            case "radiation", "radiation_gas", "hazard_radiation", "hazard4" -> {
                spawnRadiation(targetPos);
                return true;
            }
            case "blink" -> {
                if (target == null) {
                    return false;
                }
                blinkNear(target);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    public boolean startBattleFromSeal() {
        MagicalGirlPhase phase = getPhase();
        if (phase == MagicalGirlPhase.SEAL_WAIT) {
            Vec3 battleCenter = !Double.isNaN(this.anchorX)
                    ? new Vec3(this.anchorX, this.anchorY, this.anchorZ)
                    : defaultSealEscapeTarget();
            setBattleAnchor(battleCenter);
            this.teleportTo(this.anchorX, this.anchorY, this.anchorZ);
            this.setInvulnerable(false);
            if (this.level() instanceof ServerLevel serverLevel) {
                Player nearby = serverLevel.getNearestPlayer(
                        TargetingConditions.forCombat().range(80.0D).selector(MagicalGirlBossEntity::canTarget),
                        this
                );
                if (nearby != null) {
                    this.setTarget(nearby);
                }
            }
            enterPhase(MagicalGirlPhase.PHASE_1);
            return true;
        }
        if (phase != MagicalGirlPhase.SEALED) {
            return false;
        }
        setBattleAnchor(defaultSealEscapeTarget());
        enterPhase(MagicalGirlPhase.SEAL_ESCAPE);
        this.sealedYaw = Float.NaN;
        return true;
    }

    private Vec3 defaultSealEscapeTarget() {
        double targetY = Math.min(SEAL_ESCAPE_TARGET_Y, this.level().getMaxBuildHeight() - 1.0D);
        return new Vec3(this.getX() - SEAL_ESCAPE_WEST_DISTANCE, targetY, this.getZ());
    }

    private void setBattleAnchor(Vec3 center) {
        this.anchorX = center.x;
        this.anchorY = center.y;
        this.anchorZ = center.z;
    }

    public void removeByCommand() {
        endPhantomAttack(true);
        endRavagerAttack(true);
        cleanupPhase3Mines(true);
        this.natureActions.clear();
        this.phase3Actions.clear();
        this.discard();
    }

    private void initHiddenTaczGun() {
        if (!this.hiddenTaczGun.isEmpty()) {
            return;
        }
        ItemStack gunStack = new ItemStack(com.tacz.guns.init.ModItems.MODERN_KINETIC_GUN.get());
        if (!(gunStack.getItem() instanceof IGun iGun)) {
            return;
        }
        iGun.setGunId(gunStack, FINAL_MAGIC_GUN);
        this.hiddenTaczGun = gunStack;
        this.setItemSlot(EquipmentSlot.MAINHAND, ModItems.MAGICAL_GIRL_WAND.get().getDefaultInstance());
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    private void castFinalMagicBullets(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel) || this.hiddenTaczGun.isEmpty()) {
            return;
        }
        triggerCastAnimation();
        float[] aim = calcAim(this, target);
        int fired = MagicTaczCompat.fireBurstAt(
                serverLevel,
                this,
                this.hiddenTaczGun,
                aim[0],
                aim[1],
                9,
                4.2F,
                0.55F,
                9.0F,
                5.0F,
                FINAL_MAGIC_TRACER,
                1.65F
        );
        if (fired > 0) {
            serverLevel.sendParticles(ParticleTypes.END_ROD, this.getX(), this.getEyeY(), this.getZ(), 24, 0.6D, 0.4D, 0.6D, 0.04D);
            serverLevel.sendParticles(ParticleTypes.WITCH, target.getX(), target.getY() + 1.0D, target.getZ(), 18, 0.8D, 0.6D, 0.8D, 0.03D);
        }
    }

    private static float[] calcAim(LivingEntity shooter, LivingEntity target) {
        double dx = target.getX() - shooter.getX();
        double dy = target.getEyeY() - shooter.getEyeY();
        double dz = target.getZ() - shooter.getZ();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Mth.atan2(dz, dx) * (180.0D / Math.PI)) - 90.0F;
        float pitch = (float) (-(Mth.atan2(dy, horizontal) * (180.0D / Math.PI)));
        return new float[]{pitch, yaw};
    }

    private static boolean canTarget(LivingEntity entity) {
        return !(entity instanceof Player player) || canTargetPlayer(player);
    }

    private static boolean canTargetPlayer(Player player) {
        return !player.isCreative() && !player.isSpectator();
    }

    private List<Player> combatTargets(int maxTargets) {
        AABB area = this.getBoundingBox().inflate(80.0D, 48.0D, 80.0D);
        List<Player> targets = new ArrayList<>(this.level().getEntitiesOfClass(Player.class, area, MagicalGirlBossEntity::canTargetPlayer));
        targets.sort(java.util.Comparator.comparingDouble(player -> player.distanceToSqr(this)));
        if (targets.size() > maxTargets) {
            return new ArrayList<>(targets.subList(0, maxTargets));
        }
        return targets;
    }

    private List<Player> chooseTargets(List<Player> targets, int count) {
        List<Player> pool = new ArrayList<>(targets);
        List<Player> chosen = new ArrayList<>();
        int limit = Math.min(count, pool.size());
        for (int i = 0; i < limit; i++) {
            chosen.add(pool.remove(this.random.nextInt(pool.size())));
        }
        return chosen;
    }

    private Player chooseDifferentTarget(List<Player> targets, Player current) {
        if (targets.size() <= 1) {
            return current;
        }
        Player chosen = current;
        for (int attempts = 0; attempts < 8 && chosen == current; attempts++) {
            chosen = targets.get(this.random.nextInt(targets.size()));
        }
        return chosen == current ? targets.get(0) == current ? targets.get(1) : targets.get(0) : chosen;
    }

    private int scaledTargetCount(List<Player> targets, int soloCount, int maxCount) {
        return Mth.clamp(soloCount + Math.max(0, targets.size() - 1) / 2, soloCount, maxCount);
    }

    private void spawnHazard(Vec3 pos, int lifetime, int warmup, float radius, float damage, int variant) {
        if (!this.level().isClientSide) {
            triggerCastAnimation();
            Vec3 groundPos = groundMagicCirclePos(pos);
            this.level().addFreshEntity(new MagicalHazardEntity(this.level(), groundPos.x, groundPos.y, groundPos.z, lifetime, warmup, radius, damage, variant));
        }
    }

    private Vec3 groundMagicCirclePos(Vec3 pos) {
        int x = Mth.floor(pos.x);
        int z = Mth.floor(pos.z);
        int y = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        return new Vec3(pos.x, y, pos.z);
    }

    private void spawnYellowIgnition(Vec3 pos) {
        spawnHazard(pos, 20 * 12, 20 * 4, 30.0F, MagicalGirlDamage.YELLOW_IGNITION_HAZARD, MagicalHazardEntity.VARIANT_YELLOW_IGNITION);
    }

    private void spawnRadiation(Vec3 pos) {
        spawnHazard(pos, 20 * 12, 20 * 4, 30.0F, MagicalGirlDamage.RADIATION_HAZARD, MagicalHazardEntity.VARIANT_RADIATION);
    }

    private void castPhase3ModernAttack(Player target) {
        if (!this.usedEmergencyExecution && this.getHealth() / this.getMaxHealth() <= 0.55F) {
            castEmergencyExecution(target);
            this.usedEmergencyExecution = true;
            return;
        }
        double distance = this.distanceTo(target);
        float roll = this.random.nextFloat();
        if (distance < 6.0D) {
            if (roll < 0.22F) {
                castChemicalSpray(target);
            } else if (roll < 0.44F) {
                castInjectionNeedle(target);
            } else if (roll < 0.62F) {
                castPhase3RedLaser(target);
            } else if (roll < 0.80F) {
                spawnYellowIgnition(target.position());
            } else {
                castLockOnBurst(target);
            }
        } else if (distance < 16.0D) {
            if (roll < 0.20F) {
                castLockOnBurst(target);
            } else if (roll < 0.40F) {
                castReactionBullet(target);
            } else if (roll < 0.56F) {
                castPhase3RedLaser(target);
            } else if (roll < 0.59F) {
                spawnRadiation(target.position());
            } else if (roll < 0.80F) {
                castMineScatter(target, 6 + this.random.nextInt(3), 20 * 10);
            } else {
                castSuppressionRain(target);
            }
        } else {
            if (roll < 0.30F) {
                castSummonedArsenal(target, 6 + this.random.nextInt(5), 0);
            } else if (roll < 0.52F) {
                castSuppressionRain(target);
            } else if (roll < 0.68F) {
                castPhase3RedLaser(target);
            } else if (roll < 0.84F) {
                castMineScatter(target, 7 + this.random.nextInt(3), 20 * 11);
            } else {
                castLockOnBurst(target);
            }
        }
    }

    private void castPhase3ModernAttack(List<Player> targets) {
        Player primary = targets.get(this.random.nextInt(targets.size()));
        if (!this.usedEmergencyExecution && this.getHealth() / this.getMaxHealth() <= 0.55F) {
            castEmergencyExecution(primary);
            this.usedEmergencyExecution = true;
            return;
        }
        float roll = this.random.nextFloat();
        List<Player> selected = chooseTargets(targets, scaledTargetCount(targets, 2, 4));
        if (roll < 0.18F) {
            for (Player target : selected) {
                castLockOnBurst(target);
            }
        } else if (roll < 0.34F) {
            for (Player target : selected) {
                castSuppressionRain(target);
            }
        } else if (roll < 0.48F) {
            boolean cleanup = true;
            for (Player target : selected) {
                castMineScatter(target, targets.size() >= 5 ? 5 : 4, 20 * 10, cleanup);
                cleanup = false;
            }
        } else if (roll < 0.62F) {
            for (Player target : chooseTargets(targets, Math.min(2, targets.size()))) {
                castPhase3RedLaser(target);
            }
        } else if (roll < 0.76F) {
            castSummonedArsenal(primary, 8 + Math.min(8, targets.size() * 2), 0);
            for (Player target : chooseTargets(targets, Math.min(3, targets.size()))) {
                castInjectionNeedle(target);
            }
        } else if (roll < 0.88F) {
            for (Player target : selected) {
                castReactionBullet(target);
            }
        } else {
            for (Player target : selected) {
                if (this.distanceTo(target) < 12.0D || this.random.nextFloat() < 0.70F) {
                    castChemicalSpray(target);
                } else {
                    spawnRadiation(target.position());
                }
            }
        }
    }

    private void castLockOnBurst(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        faceTarget(target);
        Vec3 muzzle = this.getEyePosition().add(this.getLookAngle().scale(0.8D));
        Vec3 targetBody = FloatingTaCZGunEntity.targetBodyPosition(target);
        Vec3 dir = targetBody.subtract(muzzle).normalize();
        renderPhase3Line(serverLevel, muzzle, targetBody, ParticleTypes.ELECTRIC_SPARK, 18);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.HOSTILE, 1.0F, 1.85F);
        int bullets = 3 + this.random.nextInt(3);
        for (int i = 0; i < bullets; i++) {
            queuePhase3Action(new Phase3Action(Phase3ActionType.SUPERB_SHOT, 10 + i * 4, muzzle, dir, 6.5D, MagicalGirlDamage.SUPERB_LOCK_ON_SHOT, 0.35D));
        }
    }

    private void castPhase3RedLaser(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerBigCastAnimation();
        faceTarget(target);
        serverLevel.addFreshEntity(new RibbonJudgementEntity(serverLevel, this, target, true));
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.15F, 1.9F);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, this.getX(), this.getEyeY(), this.getZ(), 28, 0.55D, 0.35D, 0.55D, 0.05D);
    }

    private void castChemicalSpray(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        faceTarget(target);
        Vec3 forward = new Vec3(target.getX() - this.getX(), 0.0D, target.getZ() - this.getZ()).normalize();
        Vec3 right = new Vec3(-forward.z, 0.0D, forward.x);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.SLIME_BLOCK_PLACE, SoundSource.HOSTILE, 1.2F, 0.55F);
        for (int i = -2; i <= 2; i++) {
            Vec3 pos = this.position().add(forward.scale(3.5D + this.random.nextDouble() * 3.0D)).add(right.scale(i * 0.9D));
            Vec3 ground = Vec3.atBottomCenterOf(groundBlockPos(pos));
            queuePhase3Action(new Phase3Action(Phase3ActionType.CHEMICAL_AREA, 12 + Math.abs(i) * 3, ground, Vec3.ZERO, 2.0D, MagicalGirlDamage.CHEMICAL_SPRAY_AREA, 0.0D));
            renderWarningCircle(serverLevel, ground, 2.0D, ParticleTypes.ITEM_SLIME, 20);
        }
    }

    private void castReactionBullet(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        faceTarget(target);
        Vec3 center = Vec3.atBottomCenterOf(groundBlockPos(target.position()));
        Vec3 side = new Vec3(this.random.nextDouble() - 0.5D, 0.0D, this.random.nextDouble() - 0.5D).normalize();
        for (int i = 0; i < 3; i++) {
            Vec3 area = center.add(side.scale((i - 1) * 2.2D));
            queuePhase3Action(new Phase3Action(Phase3ActionType.CHEMICAL_AREA, 12 + i * 6, area, Vec3.ZERO, 2.1D, MagicalGirlDamage.REACTION_BULLET_AREA, 0.0D));
            renderWarningCircle(serverLevel, area, 2.1D, ParticleTypes.ITEM_SLIME, 18);
        }
        Vec3 muzzle = this.getEyePosition().add(this.getLookAngle().scale(0.8D));
        queuePhase3Action(new Phase3Action(Phase3ActionType.IGNITION_REACTION, 58, center, muzzle, 3.5D, MagicalGirlDamage.CHEMICAL_REACTION, 0.0D));
        renderWarningCircle(serverLevel, center, 3.5D, ParticleTypes.FLAME, 30);
        serverLevel.playSound(null, BlockPos.containing(center), SoundEvents.TNT_PRIMED, SoundSource.HOSTILE, 1.0F, 1.3F);
    }

    private void castInjectionNeedle(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        faceTarget(target);
        int count = 3 + this.random.nextInt(4);
        Vec3 origin = this.getEyePosition().add(0.0D, 0.25D, 0.0D);
        for (int i = 0; i < count; i++) {
            double angle = Math.PI * 2.0D * i / count;
            Vec3 spawn = origin.add(Math.cos(angle) * 1.1D, Math.sin(angle * 2.0D) * 0.25D, Math.sin(angle) * 1.1D);
            Vec3 targetBody = FloatingTaCZGunEntity.targetBodyPosition(target);
            Vec3 dir = targetBody.subtract(spawn).normalize();
            queuePhase3Action(new Phase3Action(Phase3ActionType.NEEDLE_SHOT, 18 + i * 3, spawn, dir, MagicalGirlDamage.INJECTION_NEEDLE, 1.35D, 0.0D));
            renderPhase3Line(serverLevel, spawn, targetBody, ParticleTypes.CRIT, 8);
        }
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.CROSSBOW_QUICK_CHARGE_2, SoundSource.HOSTILE, 1.0F, 1.55F);
    }

    private void castSuppressionRain(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        Vec3 center = Vec3.atBottomCenterOf(groundBlockPos(target.position()));
        int lanes = 4 + this.random.nextInt(3);
        serverLevel.playSound(null, BlockPos.containing(center), SoundEvents.BEACON_POWER_SELECT, SoundSource.HOSTILE, 1.0F, 0.65F);
        for (int lane = 0; lane < lanes; lane++) {
            double angle = Math.PI * 2.0D * lane / lanes + this.random.nextDouble() * 0.35D;
            Vec3 laneDir = new Vec3(Math.cos(angle), -0.75D, Math.sin(angle)).normalize();
            Vec3 horizontal = new Vec3(laneDir.x, 0.0D, laneDir.z).normalize();
            Vec3 start = center.subtract(horizontal.scale(9.0D)).add(0.0D, 9.0D, 0.0D);
            renderPhase3Line(serverLevel, start, center.add(horizontal.scale(9.0D)).add(0.0D, 0.2D, 0.0D), ParticleTypes.ELECTRIC_SPARK, 28);
            for (int i = 0; i < 8; i++) {
                Vec3 spawn = start.add(horizontal.scale(i * 2.2D)).add((this.random.nextDouble() - 0.5D) * 1.2D, 0.0D, (this.random.nextDouble() - 0.5D) * 1.2D);
                queuePhase3Action(new Phase3Action(Phase3ActionType.SUPERB_SHOT, 32 + i * 3 + lane, spawn, laneDir, 4.2D, MagicalGirlDamage.SUPERB_SUPPRESSION_RAIN, 0.55D));
            }
        }
    }

    private void castSummonedArsenal(Player target, int gunCount, int baseDelay) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerBigCastAnimation();
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.2F, 1.7F);
        ResourceLocation[] guns = new ResourceLocation[]{
                ResourceLocation.fromNamespaceAndPath("tacz", "m4a1"),
                ResourceLocation.fromNamespaceAndPath("tacz", "ak47"),
                ResourceLocation.fromNamespaceAndPath("tacz", "glock_17")
        };
        Vec3 bossCenter = this.position().add(0.0D, 0.9D, 0.0D);
        int cappedGunCount = Math.min(gunCount, ServerConfig.MAGICAL_GIRL_MAX_FLOATING_GUNS.get());
        for (int i = 0; i < cappedGunCount; i++) {
            double angle = Math.PI * 2.0D * i / cappedGunCount;
            double radius = 2.4D + (i % 2) * 0.55D;
            Vec3 offset = new Vec3(Math.cos(angle) * radius, 0.65D + (i % 3) * 0.35D, Math.sin(angle) * radius);
            FloatingTaCZGunEntity gun = new FloatingTaCZGunEntity(serverLevel, guns[i % guns.length], this.getUUID(), target.getUUID(), baseDelay + i * 3);
            gun.setPos(bossCenter.add(offset));
            gun.aimAt(FloatingTaCZGunEntity.targetBodyPosition(target));
            serverLevel.addFreshEntity(gun);
        }
    }

    private void castMineScatter(Player target, int mineCount, int mineLifeTicks) {
        castMineScatter(target, mineCount, mineLifeTicks, true);
    }

    private void castMineScatter(Player target, int mineCount, int mineLifeTicks, boolean cleanupExisting) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        if (cleanupExisting) {
            cleanupPhase3Mines(true);
        }
        Vec3 predicted = target.position().add(target.getDeltaMovement().scale(10.0D));
        for (int i = 0; i < mineCount; i++) {
            double angle = this.random.nextDouble() * Math.PI * 2.0D;
            double distance = 3.5D + this.random.nextDouble() * 5.5D;
            Vec3 pos = predicted.add(Math.cos(angle) * distance, 0.0D, Math.sin(angle) * distance);
            BlockPos ground = groundBlockPos(pos);
            Vec3 marker = Vec3.atBottomCenterOf(ground);
            renderWarningCircle(serverLevel, marker, 0.9D, ParticleTypes.ELECTRIC_SPARK, 12);
            queuePhase3Action(new Phase3Action(Phase3ActionType.PLACE_MINE, 32 + i * 2, marker, Vec3.ZERO, i % 2, mineLifeTicks, 0.0D));
        }
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.CHAIN_PLACE, SoundSource.HOSTILE, 1.2F, 0.75F);
    }

    private void castEmergencyExecution(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerBigCastAnimation();
        Vec3 center = new Vec3(this.anchorX, this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(this.anchorX), Mth.floor(this.anchorZ)), this.anchorZ);
        this.teleportTo(center.x, center.y + 9.0D, center.z);
        cleanupPhase3Mines(true);
        serverLevel.playSound(null, BlockPos.containing(center), SoundEvents.WITHER_SPAWN, SoundSource.HOSTILE, 1.0F, 1.35F);
        renderWarningCircle(serverLevel, center, 12.0D, ParticleTypes.ELECTRIC_SPARK, 96);
        renderWarningCircle(serverLevel, center.add(4.5D, 0.0D, 0.0D), 2.0D, ParticleTypes.END_ROD, 28);
        castSummonedArsenal(target, 12, 60);
        for (int i = 0; i < 4; i++) {
            double angle = Math.PI * 2.0D * i / 4.0D + 0.6D;
            Vec3 area = Vec3.atBottomCenterOf(groundBlockPos(center.add(Math.cos(angle) * 5.5D, 0.0D, Math.sin(angle) * 5.5D)));
            if (area.distanceToSqr(center.add(4.5D, 0.0D, 0.0D)) > 7.0D) {
                queuePhase3Action(new Phase3Action(Phase3ActionType.CHEMICAL_AREA, 26 + i * 5, area, Vec3.ZERO, 2.2D, MagicalGirlDamage.EMERGENCY_CHEMICAL_AREA, 0.0D));
            }
        }
        castMineScatter(target, 5, 20 * 8);
        for (int tick = 42; tick <= 110; tick += 8) {
            Vec3 from = center.add((this.random.nextDouble() - 0.5D) * 18.0D, 10.0D, (this.random.nextDouble() - 0.5D) * 18.0D);
            Vec3 to = center.add((this.random.nextDouble() - 0.5D) * 10.0D, 0.3D, (this.random.nextDouble() - 0.5D) * 10.0D);
            queuePhase3Action(new Phase3Action(Phase3ActionType.SUPERB_SHOT, tick, from, to.subtract(from).normalize(), 4.4D, MagicalGirlDamage.SUPERB_EMERGENCY_RAIN, 0.35D));
        }
        queuePhase3Action(new Phase3Action(Phase3ActionType.IGNITION_REACTION, 126, center, this.getEyePosition(), 5.5D, MagicalGirlDamage.EMERGENCY_CHEMICAL_REACTION, 0.0D));
        queuePhase3Action(new Phase3Action(Phase3ActionType.FINAL_EXPLOSION, 160, center, Vec3.ZERO, 12.0D, MagicalGirlDamage.EMERGENCY_FINAL_EXPLOSION, 0.0D));
        this.attackCooldown = 20 * 12;
    }

    private void castPhase2MagicAttack(Player target) {
        float roll = this.random.nextFloat();
        if (roll < 0.22F) {
            castStellaBurst(target);
        } else if (roll < 0.56F) {
            castDiamondSpinner(target);
        } else if (roll < 0.69F) {
            castRibbonJudgement(target);
        } else if (roll < 0.84F) {
            castRuneCage(target);
            if (this.random.nextBoolean()) {
                castStellaBurst(target);
            }
        } else {
            castMagicMirror(target);
        }
    }

    private void castPhase2MagicAttack(List<Player> targets) {
        Player primary = targets.get(this.random.nextInt(targets.size()));
        float roll = this.random.nextFloat();
        int targetCount = scaledTargetCount(targets, 2, 4);
        if (roll < 0.22F) {
            for (Player target : chooseTargets(targets, targetCount)) {
                castStellaBurst(target);
            }
        } else if (roll < 0.56F) {
            for (Player target : chooseTargets(targets, targetCount)) {
                castDiamondSpinner(target);
            }
        } else if (roll < 0.68F) {
            castRibbonJudgement(primary);
            if (targets.size() >= 4) {
                castRuneCage(chooseDifferentTarget(targets, primary));
            }
        } else if (roll < 0.84F) {
            for (Player target : chooseTargets(targets, Math.min(3, targets.size()))) {
                castRuneCage(target);
            }
            for (Player target : chooseTargets(targets, Math.min(2, targets.size()))) {
                castStellaBurst(target);
            }
        } else {
            castMagicMirror(primary);
            if (targets.size() >= 4) {
                castDiamondSpinner(chooseDifferentTarget(targets, primary));
            }
        }
    }

    private void castStellaBurst(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        faceTarget(target);
        int count = 5 + this.random.nextInt(3);
        Vec3 origin = this.getEyePosition().add(0.0D, 0.2D, 0.0D);
        float damage = getPhase() == MagicalGirlPhase.PHASE_2
                ? MagicalGirlDamage.PHASE_2_STELLA_BURST
                : MagicalGirlDamage.STELLA_BURST;
        for (int i = 0; i < count; i++) {
            double angle = Math.PI * 2.0D * i / count;
            Vec3 spawn = origin.add(Math.cos(angle) * 1.2D, Math.sin(angle * 2.0D) * 0.35D, Math.sin(angle) * 1.2D);
            Vec3 aim = target.getEyePosition().subtract(spawn).normalize().scale(0.58D + i * 0.02D);
            serverLevel.addFreshEntity(new StellaBurstProjectileEntity(serverLevel, spawn, aim, damage));
        }
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.HOSTILE, 1.1F, 1.55F);
    }

    private void castDiamondSpinner(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        faceTarget(target);
        int count = 8;
        for (int i = 0; i < count; i++) {
            serverLevel.addFreshEntity(new DiamondSpinnerEntity(serverLevel, this, target, i, count));
        }
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.HOSTILE, 1.2F, 0.9F);
    }

    private void castRibbonJudgement(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        faceTarget(target);
        serverLevel.addFreshEntity(new RibbonJudgementEntity(serverLevel, this, target));
        serverLevel.playSound(null, target.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.HOSTILE, 1.2F, 0.75F);
    }

    private void castRuneCage(Vec3 pos) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        Vec3 ground = groundMagicCirclePos(pos);
        serverLevel.addFreshEntity(new RuneCageEntity(serverLevel, ground.x, ground.y, ground.z));
        serverLevel.playSound(null, BlockPos.containing(ground), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.HOSTILE, 1.0F, 1.1F);
    }

    private void castRuneCage(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        Vec3 ground = groundMagicCirclePos(target.position());
        serverLevel.addFreshEntity(new RuneCageEntity(serverLevel, ground.x, ground.y, ground.z, this, target));
        serverLevel.playSound(null, BlockPos.containing(ground), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.HOSTILE, 1.0F, 1.1F);
    }

    private void castMagicMirror(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        serverLevel.sendParticles(ParticleTypes.PORTAL, this.getX(), this.getY() + 1.0D, this.getZ(), 42, 0.9D, 0.7D, 0.9D, 0.05D);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.GLASS_BREAK, SoundSource.HOSTILE, 1.1F, 1.4F);
        for (int i = 0; i < 2; i++) {
            spawnAttackPhantom(serverLevel, target, false, false);
        }
        castStellaBurst(target);
    }

    private void castGrandSpellNova(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerBigCastAnimation();
        Vec3 center = new Vec3(this.anchorX, this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(this.anchorX), Mth.floor(this.anchorZ)), this.anchorZ);
        this.teleportTo(center.x, center.y + 6.0D, center.z);
        serverLevel.addFreshEntity(new GrandSpellNovaEntity(serverLevel, center));
        serverLevel.playSound(null, BlockPos.containing(center), SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 1.5F, 0.6F);
    }

    private void spawnGroundLightning(Player target) {
        if (this.level().isClientSide) {
            return;
        }
        double dirX = target.getX() - this.getX();
        double dirZ = target.getZ() - this.getZ();
        Vec3 start = this.position();
        this.level().addFreshEntity(new MagicalGroundLightningEntity(this.level(), start.x, start.y, start.z, dirX, dirZ));
    }

    private void castGroundLightningAtNearbyPlayers() {
        List<Player> targets = combatTargets(Integer.MAX_VALUE);
        if (targets.isEmpty()) {
            return;
        }
        triggerCastAnimation();
        faceTarget(targets.get(0));
        for (Player target : targets) {
            spawnGroundLightning(target);
        }
    }

    private void castColoredLightningStorm(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        triggerCastAnimation();
        Vec3 center = groundMagicCirclePos(target.position());
        serverLevel.playSound(null, BlockPos.containing(center), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 1.45F, 1.2F);
        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, center.x, center.y + 0.2D, center.z, 48, 3.5D, 0.15D, 3.5D, 0.12D);

        for (int i = 0; i < PHASE_2_LIGHTNING_COLORS.length; i++) {
            double angle = i == 0 ? 0.0D : Math.PI * 2.0D * i / (PHASE_2_LIGHTNING_COLORS.length - 1);
            double radius = i == 0 ? 0.0D : 2.5D + this.random.nextDouble() * 3.5D;
            Vec3 strikePos = groundMagicCirclePos(center.add(Math.cos(angle) * radius, 0.0D, Math.sin(angle) * radius));
            spawnColoredLightning(serverLevel, strikePos, PHASE_2_LIGHTNING_COLORS[i]);
            damageColoredLightning(strikePos);
        }
    }

    private void spawnColoredLightning(ServerLevel serverLevel, Vec3 pos, float[] color) {
        serverLevel.addFreshEntity(new ColoredLightningEntity(serverLevel, pos.x, pos.y, pos.z, 22.0F, color[0], color[1], color[2], true));
        serverLevel.sendParticles(ParticleTypes.FLASH, pos.x, pos.y + 0.2D, pos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.END_ROD, pos.x, pos.y + 0.35D, pos.z, 8, 0.35D, 0.12D, 0.35D, 0.03D);
    }

    private void damageColoredLightning(Vec3 pos) {
        for (LivingEntity entity : entitiesInCylinder(pos, 2.25D, 5.0D)) {
            entity.hurt(this.damageSources().magic(), MagicalGirlDamage.COLORED_LIGHTNING_STRIKE);
            Vec3 away = entity.position().subtract(pos);
            if (away.lengthSqr() > 0.01D) {
                away = away.normalize();
                entity.push(away.x * 0.35D, 0.3D, away.z * 0.35D);
            }
        }
    }

    private void castNatureAttack(Player target) {
        double dist = this.distanceTo(target);
        float roll = this.random.nextFloat();
        if (dist < 4.0D) {
            if (roll < 0.45F) {
                castPetalGale(target);
            } else if (roll < 0.72F) {
                castIvyBind(target.position());
            } else {
                castBloomCircle(target.position());
            }
        } else if (dist < 10.0D) {
            if (roll < 0.42F) {
                castRootLance(target);
            } else if (roll < 0.62F) {
                castSakuraClaw(target);
            } else if (roll < 0.82F) {
                castBloomCircle(target.position());
            } else {
                castIvyBind(target.position());
            }
        } else if (roll < 0.40F) {
            castRootLance(target);
        } else if (roll < 0.62F) {
            castSakuraClaw(target);
        } else if (roll < 0.78F) {
            castSakuraRain(target.position());
        } else {
            castBloomCircle(target.position());
        }
    }

    private void castRootLance(Player target) {
        triggerCastAnimation();
        faceTarget(target);
        Vec3 start = this.position();
        Vec3 dir = new Vec3(target.getX() - this.getX(), 0.0D, target.getZ() - this.getZ());
        if (dir.lengthSqr() < 0.01D) {
            dir = this.getLookAngle();
        }
        dir = new Vec3(dir.x, 0.0D, dir.z).normalize();
        for (int i = 1; i <= 12; i++) {
            Vec3 pos = start.add(dir.scale(i));
            BlockPos ground = groundBlockPos(pos);
            natureActions.add(new NatureAction(NatureActionType.ROOT_LANCE_WARN, 1, Vec3.atCenterOf(ground), dir, 0.0D, 0.0D));
            natureActions.add(new NatureAction(NatureActionType.ROOT_LANCE_HIT, 14 + i / 2, Vec3.atCenterOf(ground), dir, 0.0D, 0.0D));
        }
    }

    private void castBloomCircle(Vec3 pos) {
        triggerCastAnimation();
        Vec3 center = Vec3.atBottomCenterOf(groundBlockPos(pos));
        for (int i = 1; i <= 24; i += 4) {
            natureActions.add(new NatureAction(NatureActionType.BLOOM_WARN, i, center, Vec3.ZERO, 2.5D * i / 24.0D, 0.0D));
        }
        natureActions.add(new NatureAction(NatureActionType.BLOOM_HIT, 24, center, Vec3.ZERO, 2.5D, MagicalGirlDamage.BLOOM_CIRCLE));
    }

    private void castIvyBind(Vec3 pos) {
        triggerCastAnimation();
        Vec3 center = Vec3.atBottomCenterOf(groundBlockPos(pos));
        natureActions.add(new NatureAction(NatureActionType.IVY_WARN, 1, center, Vec3.ZERO, 1.5D, 0.0D));
        natureActions.add(new NatureAction(NatureActionType.IVY_HIT, 10, center, Vec3.ZERO, 1.5D, MagicalGirlDamage.IVY_BIND));
    }

    private void castPetalGale(Player target) {
        triggerCastAnimation();
        faceTarget(target);
        Vec3 dir = new Vec3(target.getX() - this.getX(), 0.0D, target.getZ() - this.getZ()).normalize();
        natureActions.add(new NatureAction(NatureActionType.PETAL_WARN, 1, this.position(), dir, 8.0D, 0.0D));
        natureActions.add(new NatureAction(NatureActionType.PETAL_HIT, 16, this.position(), dir, 8.0D, MagicalGirlDamage.PETAL_GALE));
    }

    private void castTornado(Vec3 pos) {
        triggerBigCastAnimation();
        Vec3 center = Vec3.atBottomCenterOf(groundBlockPos(pos));
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.addFreshEntity(new MagicalTwisterEntity(serverLevel, center, 9.5F, 22.0F, 20 * 16, 18, MagicalGirlDamage.TWISTER_TICK, this.getUUID()));
        }
    }

    private void castSakuraClaw(Player target) {
        triggerCastAnimation();
        faceTarget(target);
        Vec3 circle = this.position().add(0.0D, -1.0D, 0.0D);
        Vec3 dir = new Vec3(target.getX() - this.getX(), 0.0D, target.getZ() - this.getZ());
        if (dir.lengthSqr() < 0.01D) {
            dir = this.getLookAngle();
        }
        dir = new Vec3(dir.x, 0.0D, dir.z).normalize();
        natureActions.add(new NatureAction(NatureActionType.SAKURA_CLAW_WARN, 1, circle, dir, 7.5D, 0.0D));
        natureActions.add(new NatureAction(NatureActionType.SAKURA_CLAW_HIT, 18, circle, dir, 7.5D, MagicalGirlDamage.SAKURA_CLAW));
    }

    private void castSakuraRain(Vec3 pos) {
        triggerCastAnimation();
        Vec3 center = Vec3.atBottomCenterOf(groundBlockPos(pos));
        natureActions.add(new NatureAction(NatureActionType.SAKURA_RAIN_WARN, 1, center, Vec3.ZERO, 30.0D, 0.0D));
        for (int tick = 12; tick <= 92; tick += 4) {
            natureActions.add(new NatureAction(NatureActionType.SAKURA_RAIN_TICK, tick, center, Vec3.ZERO, 30.0D, 0.0D));
        }
        for (int tick = 24; tick <= 92; tick += 20) {
            natureActions.add(new NatureAction(NatureActionType.SAKURA_RAIN_HIT, tick, center, Vec3.ZERO, 30.0D, MagicalGirlDamage.SAKURA_RAIN));
        }
    }

    private void castGrandBlossom(Player target) {
        triggerCastAnimation();
        Vec3 center = new Vec3(this.anchorX, this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(this.anchorX), Mth.floor(this.anchorZ)), this.anchorZ);
        this.teleportTo(center.x, center.y + 6.0D, center.z);
        natureActions.add(new NatureAction(NatureActionType.GRAND_WARN, 1, center, Vec3.ZERO, 12.0D, 0.0D));
        natureActions.add(new NatureAction(NatureActionType.GRAND_HIT, 80, center, Vec3.ZERO, 12.0D, MagicalGirlDamage.GRAND_BLOSSOM));
    }

    private void tickPhase3Actions() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Iterator<Phase3Action> it = this.phase3Actions.iterator();
        while (it.hasNext()) {
            Phase3Action action = it.next();
            if (--action.ticks <= 0) {
                executePhase3Action(serverLevel, action);
                it.remove();
            }
        }
    }

    private void queuePhase3Action(Phase3Action action) {
        if (action.type == Phase3ActionType.SUPERB_SHOT && queuedPhase3ProjectileCount() >= ServerConfig.MAGICAL_GIRL_MAX_PHASE3_PROJECTILES.get()) {
            return;
        }
        this.phase3Actions.add(action);
    }

    private int queuedPhase3ProjectileCount() {
        int count = 0;
        for (Phase3Action action : this.phase3Actions) {
            if (action.type == Phase3ActionType.SUPERB_SHOT) {
                count++;
            }
        }
        return count;
    }

    private void executePhase3Action(ServerLevel serverLevel, Phase3Action action) {
        switch (action.type) {
            case SUPERB_SHOT -> {
                Phase3ProjectileUtil.spawnSuperbProjectile(serverLevel, this, action.pos, action.dir, (float) action.radius, (float) action.damage, (float) action.extra, 0.0F);
                serverLevel.playSound(null, BlockPos.containing(action.pos), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.45F, 1.75F + this.random.nextFloat() * 0.25F);
                serverLevel.sendParticles(ParticleTypes.FLASH, action.pos.x, action.pos.y, action.pos.z, 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
            case CHEMICAL_AREA -> serverLevel.addFreshEntity(new ChemicalAreaEntity(serverLevel, action.pos, (float) action.radius, 20 * 8, 18, (float) action.damage, true, this.getUUID()));
            case IGNITION_REACTION -> reactChemicalAreas(serverLevel, action.pos, action.radius, (float) action.damage);
            case NEEDLE_SHOT -> {
                InjectionNeedleProjectileEntity needle = new InjectionNeedleProjectileEntity(serverLevel, action.pos, action.dir.normalize().scale(action.damage), this.getUUID(), (float) action.radius);
                serverLevel.addFreshEntity(needle);
                serverLevel.playSound(null, BlockPos.containing(action.pos), SoundEvents.CROSSBOW_SHOOT, SoundSource.HOSTILE, 0.8F, 1.65F);
            }
            case PLACE_MINE -> placePhase3Mine(serverLevel, action.pos, action.radius >= 0.5D);
            case FINAL_EXPLOSION -> damagePhase3FinalExplosion(serverLevel, action.pos, action.radius, (float) action.damage);
        }
    }

    private void reactChemicalAreas(ServerLevel serverLevel, Vec3 pos, double radius, float fallbackDamage) {
        boolean reacted = false;
        AABB box = new AABB(pos, pos).inflate(radius);
        for (ChemicalAreaEntity area : serverLevel.getEntitiesOfClass(ChemicalAreaEntity.class, box, ChemicalAreaEntity::isFlammable)) {
            area.reactAndExplode();
            reacted = true;
        }
        if (!reacted) {
            serverLevel.explode(this, pos.x, pos.y + 0.2D, pos.z, (float) Math.min(4.0D, radius), false, Level.ExplosionInteraction.NONE);
            for (LivingEntity entity : entitiesInCylinder(pos, radius, 3.0D)) {
                entity.hurt(this.damageSources().explosion(null), fallbackDamage);
            }
        }
    }

    private void placePhase3Mine(ServerLevel serverLevel, Vec3 pos, boolean blu43) {
        Entity mine = blu43
                ? com.atsuishio.superbwarfare.init.ModEntities.BLU_43.get().create(serverLevel)
                : com.atsuishio.superbwarfare.init.ModEntities.TM_62.get().create(serverLevel);
        if (mine == null) {
            return;
        }
        mine.setPos(pos.x, pos.y, pos.z);
        mine.setCustomName(Component.literal("魔法少女"));
        mine.setCustomNameVisible(false);
        setMineOwnerIfSupported(mine);
        serverLevel.addFreshEntity(mine);
        this.phase3MineIds.add(mine.getUUID());
        serverLevel.playSound(null, BlockPos.containing(pos), SoundEvents.CHAIN_PLACE, SoundSource.HOSTILE, 0.8F, 1.25F);
    }

    private void setMineOwnerIfSupported(Entity mine) {
        try {
            mine.getClass().getMethod("setOwnerUUID", UUID.class).invoke(mine, this.getUUID());
        } catch (ReflectiveOperationException ignored) {
        }
    }

    private void cleanupPhase3Mines(boolean force) {
        if (!(this.level() instanceof ServerLevel serverLevel) || this.phase3MineIds.isEmpty()) {
            return;
        }
        if (!force && this.tickCount % 40 != 0) {
            return;
        }
        Iterator<UUID> it = this.phase3MineIds.iterator();
        while (it.hasNext()) {
            UUID uuid = it.next();
            Entity entity = serverLevel.getEntity(uuid);
            if (entity == null || !entity.isAlive()) {
                it.remove();
            } else if (force || entity.tickCount > 20 * 12) {
                entity.discard();
                it.remove();
            }
        }
    }

    private void damagePhase3FinalExplosion(ServerLevel serverLevel, Vec3 center, double radius, float damage) {
        Vec3 safeZone = center.add(4.5D, 0.0D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.FLASH, center.x, center.y + 0.5D, center.z, 3, 0.0D, 0.0D, 0.0D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.EXPLOSION, center.x, center.y + 0.5D, center.z, 12, 4.0D, 0.5D, 4.0D, 0.0D);
        serverLevel.playSound(null, BlockPos.containing(center), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 2.0F, 0.72F);
        for (LivingEntity entity : entitiesInCylinder(center, radius, 5.0D)) {
            if (entity.position().distanceToSqr(safeZone) <= 4.0D) {
                continue;
            }
            Vec3 away = entity.position().subtract(center);
            if (away.lengthSqr() < 0.01D) {
                away = new Vec3(1.0D, 0.0D, 0.0D);
            }
            entity.hurt(this.damageSources().explosion(null), damage);
            Vec3 push = away.normalize();
            entity.push(push.x * 1.1D, 0.35D, push.z * 1.1D);
        }
        cleanupPhase3Mines(true);
    }

    private void renderPhase3Line(ServerLevel serverLevel, Vec3 start, Vec3 end, net.minecraft.core.particles.ParticleOptions particle, int points) {
        points = scaledParticlePoints(points);
        Vec3 diff = end.subtract(start);
        for (int i = 0; i <= points; i++) {
            Vec3 p = start.add(diff.scale(i / (double) points));
            serverLevel.sendParticles(particle, p.x, p.y, p.z, 1, 0.01D, 0.01D, 0.01D, 0.0D);
        }
    }

    private void renderWarningCircle(ServerLevel serverLevel, Vec3 center, double radius, net.minecraft.core.particles.ParticleOptions particle, int points) {
        points = scaledParticlePoints(points);
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0D * i / points;
            double x = center.x + Math.cos(angle) * radius;
            double z = center.z + Math.sin(angle) * radius;
            serverLevel.sendParticles(particle, x, center.y + 0.15D, z, 1, 0.02D, 0.02D, 0.02D, 0.0D);
        }
    }

    private int scaledParticlePoints(int points) {
        return Math.max(1, points / Math.max(1, ServerConfig.MAGICAL_GIRL_PARTICLE_INTERVAL.get()));
    }

    private void tickNatureActions() {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Iterator<NatureAction> it = this.natureActions.iterator();
        while (it.hasNext()) {
            NatureAction action = it.next();
            if (--action.ticks <= 0) {
                executeNatureAction(serverLevel, action);
                it.remove();
            }
        }
    }

    private void executeNatureAction(ServerLevel serverLevel, NatureAction action) {
        switch (action.type) {
            case ROOT_LANCE_WARN -> natureCircle(serverLevel, action.pos, 0.5D, ModParticles.NATURE_ROOT.get(), 8);
            case ROOT_LANCE_HIT -> damageCylinder(serverLevel, action.pos, 0.75D, 2.2D, MagicalGirlDamage.ROOT_LANCE, action.dir, 0.4D, true);
            case BLOOM_WARN -> natureCircle(serverLevel, action.pos, action.radius, ParticleTypes.CHERRY_LEAVES, 24);
            case BLOOM_HIT -> damageCylinder(serverLevel, action.pos, action.radius, 2.2D, (float) action.damage, Vec3.ZERO, 0.25D, false);
            case IVY_WARN -> natureCircle(serverLevel, action.pos, action.radius, ModParticles.NATURE_IVY.get(), 18);
            case IVY_HIT -> {
                damageCylinder(serverLevel, action.pos, action.radius, 2.0D, (float) action.damage, Vec3.ZERO, 0.0D, false);
                for (LivingEntity entity : entitiesInCylinder(action.pos, action.radius, 2.0D)) {
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
                    entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, -1));
                }
            }
            case PETAL_WARN -> {
                serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES, this.getX(), this.getY() + 1.0D, this.getZ(), 36, 1.0D, 0.6D, 1.0D, 0.03D);
                serverLevel.playSound(null, this.blockPosition(), SoundEvents.GRASS_BREAK, SoundSource.HOSTILE, 1.0F, 0.8F);
            }
            case PETAL_HIT -> damagePetalGale(serverLevel, action);
            case TORNADO_WARN -> renderTornadoWarning(serverLevel, action);
            case TORNADO_TICK -> tickTornado(serverLevel, action);
            case TORNADO_FINISH -> finishTornado(serverLevel, action);
            case SAKURA_CLAW_WARN -> renderSakuraClawWarning(serverLevel, action);
            case SAKURA_CLAW_HIT -> damageSakuraClaw(serverLevel, action);
            case SAKURA_RAIN_WARN -> renderSakuraRainWarning(serverLevel, action);
            case SAKURA_RAIN_TICK -> renderSakuraRain(serverLevel, action);
            case SAKURA_RAIN_HIT -> damageSakuraRain(action);
            case GRAND_WARN -> {
                natureCircle(serverLevel, action.pos, 12.0D, ParticleTypes.CHERRY_LEAVES, 96);
                natureCircle(serverLevel, action.pos.add(5.0D, 0.0D, 0.0D), 2.0D, ModParticles.NATURE_LIGHT.get(), 32);
                natureCircle(serverLevel, action.pos.add(-4.0D, 0.0D, 3.0D), 2.0D, ModParticles.NATURE_LIGHT.get(), 32);
            }
            case GRAND_HIT -> damageGrandBlossom(serverLevel, action.pos);
        }
    }

    private void renderTornadoWarning(ServerLevel serverLevel, NatureAction action) {
        natureCircle(serverLevel, action.pos, action.radius, ParticleTypes.CLOUD, 72);
        natureCircle(serverLevel, action.pos, action.radius * 0.62D, ParticleTypes.CAMPFIRE_COSY_SMOKE, 54);
        natureCircle(serverLevel, action.pos, action.radius * 0.28D, ParticleTypes.POOF, 28);
        serverLevel.sendParticles(ParticleTypes.CLOUD, action.pos.x, action.pos.y + 0.15D, action.pos.z, 42, 1.8D, 0.08D, 1.8D, 0.06D);
        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, action.pos.x, action.pos.y + 0.3D, action.pos.z, 18, 1.2D, 0.12D, 1.2D, 0.04D);
        serverLevel.playSound(null, BlockPos.containing(action.pos), SoundEvents.BEACON_AMBIENT, SoundSource.HOSTILE, 1.4F, 1.8F);
        serverLevel.playSound(null, BlockPos.containing(action.pos), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 0.55F, 1.85F);
    }

    private void tickTornado(ServerLevel serverLevel, NatureAction action) {
        double age = 124.0D - action.ticks;
        int rings = 11;
        int points = scaledParticlePoints(160);
        for (int i = 0; i < points; i++) {
            double layer = i % rings;
            double height = 0.35D + layer * 1.05D + this.random.nextDouble() * 0.45D;
            double taper = 1.0D - layer / (rings * 1.45D);
            double radius = action.radius * (0.16D + 0.84D * taper) * (0.82D + this.random.nextDouble() * 0.22D);
            double angle = age * 0.42D + layer * 0.92D + i * 1.618D;
            double x = action.pos.x + Math.cos(angle) * radius;
            double z = action.pos.z + Math.sin(angle) * radius;
            double y = action.pos.y + height;
            serverLevel.sendParticles(ParticleTypes.CLOUD, x, y, z, 1, 0.08D, 0.04D, 0.08D, 0.03D);
            if (i % 5 == 0) {
                serverLevel.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y, z, 1, 0.05D, 0.03D, 0.05D, 0.015D);
            }
            if (i % 11 == 0) {
                serverLevel.sendParticles(ParticleTypes.ASH, x, y + 0.08D, z, 1, 0.04D, 0.02D, 0.04D, 0.01D);
            }
        }
        if (this.random.nextInt(2) == 0) {
            serverLevel.playSound(null, BlockPos.containing(action.pos), SoundEvents.ELYTRA_FLYING, SoundSource.HOSTILE, 0.65F, 1.55F + this.random.nextFloat() * 0.2F);
        }
        pullTornadoTargets(action);
    }

    private void pullTornadoTargets(NatureAction action) {
        for (LivingEntity entity : entitiesInTornado(action.pos, action.radius, 12.0D)) {
            Vec3 toCenter = new Vec3(action.pos.x - entity.getX(), 0.0D, action.pos.z - entity.getZ());
            double distance = Math.max(0.35D, toCenter.length());
            Vec3 inward = toCenter.lengthSqr() > 0.01D ? toCenter.normalize() : Vec3.ZERO;
            Vec3 tangent = new Vec3(-inward.z, 0.0D, inward.x);
            double pull = Mth.clamp((action.radius - distance + 1.8D) / action.radius, 0.22D, 0.86D);
            double spin = Mth.clamp(0.46D - distance * 0.018D, 0.26D, 0.46D);
            entity.push(inward.x * 0.16D * pull + tangent.x * spin, 0.13D + pull * 0.05D, inward.z * 0.16D * pull + tangent.z * spin);
            entity.setYRot(entity.getYRot() + 28.0F);
            entity.yBodyRot += 28.0F;
            entity.yHeadRot += 28.0F;
            entity.hurtMarked = true;
            if (entity.tickCount % 8 == 0) {
                entity.hurt(this.damageSources().mobAttack(this), (float) action.damage);
            }
        }
    }

    private void finishTornado(ServerLevel serverLevel, NatureAction action) {
        serverLevel.sendParticles(ParticleTypes.POOF, action.pos.x, action.pos.y + 0.4D, action.pos.z, 92, 2.2D, 0.18D, 2.2D, 0.12D);
        serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, action.pos.x, action.pos.y + 1.2D, action.pos.z, 10, action.radius * 0.48D, 1.2D, action.radius * 0.48D, 0.0D);
        serverLevel.sendParticles(ParticleTypes.CLOUD, action.pos.x, action.pos.y + 4.0D, action.pos.z, scaledParticlePoints(240), action.radius * 0.8D, 4.2D, action.radius * 0.8D, 0.22D);
        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, action.pos.x, action.pos.y + 3.0D, action.pos.z, scaledParticlePoints(80), action.radius * 0.5D, 3.0D, action.radius * 0.5D, 0.12D);
        serverLevel.playSound(null, BlockPos.containing(action.pos), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.2F, 1.45F);
        for (LivingEntity entity : entitiesInTornado(action.pos, action.radius + 1.2D, 12.5D)) {
            Vec3 away = new Vec3(entity.getX() - action.pos.x, 0.0D, entity.getZ() - action.pos.z);
            if (away.lengthSqr() < 0.01D) {
                away = new Vec3(1.0D, 0.0D, 0.0D);
            }
            away = away.normalize();
            entity.hurt(this.damageSources().mobAttack(this), (float) action.damage);
            entity.push(away.x * 0.55D, 0.95D, away.z * 0.55D);
            entity.hurtMarked = true;
        }
    }

    private List<LivingEntity> entitiesInTornado(Vec3 center, double radius, double height) {
        AABB box = new AABB(center.x - radius, center.y - 0.5D, center.z - radius, center.x + radius, center.y + height, center.z + radius);
        return this.level().getEntitiesOfClass(LivingEntity.class, box, e -> e != this && e.isAlive() && horizontalDistanceSqr(e.position(), center) <= radius * radius);
    }

    private static double horizontalDistanceSqr(Vec3 a, Vec3 b) {
        double dx = a.x - b.x;
        double dz = a.z - b.z;
        return dx * dx + dz * dz;
    }

    private void damagePetalGale(ServerLevel serverLevel, NatureAction action) {
        serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES, this.getX(), this.getY() + 1.0D, this.getZ(), 80, 3.0D, 0.8D, 3.0D, 0.12D);
        serverLevel.playSound(null, this.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.0F, 0.7F);
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(action.radius, 3.0D, action.radius), e -> e != this && e.isAlive())) {
            Vec3 toEntity = new Vec3(entity.getX() - this.getX(), 0.0D, entity.getZ() - this.getZ());
            double distance = toEntity.length();
            if (distance <= 0.1D || distance > action.radius) {
                continue;
            }
            double dot = action.dir.normalize().dot(toEntity.normalize());
            if (dot >= Math.cos(Math.toRadians(35.0D))) {
                entity.hurt(this.damageSources().mobAttack(this), (float) action.damage);
                entity.push(toEntity.normalize().x * 0.8D, 0.15D, toEntity.normalize().z * 0.8D);
            }
        }
    }

    private void renderSakuraClawWarning(ServerLevel serverLevel, NatureAction action) {
        natureCircle(serverLevel, action.pos, 1.35D, ParticleTypes.CHERRY_LEAVES, 36);
        natureCircle(serverLevel, action.pos, 0.8D, ParticleTypes.END_ROD, 18);
        Vec3 right = new Vec3(-action.dir.z, 0.0D, action.dir.x).normalize();
        for (int claw = -1; claw <= 1; claw++) {
            Vec3 start = action.pos.add(right.scale(claw * 0.38D)).add(0.0D, 0.6D, 0.0D);
            for (int i = 0; i < 10; i++) {
                Vec3 p = start.add(action.dir.scale(i * 0.35D));
                serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES, p.x, p.y, p.z, 1, 0.03D, 0.03D, 0.03D, 0.0D);
                if (i % 3 == 0) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD, p.x, p.y + 0.05D, p.z, 1, 0.01D, 0.01D, 0.01D, 0.0D);
                }
            }
        }
        serverLevel.playSound(null, BlockPos.containing(action.pos), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.HOSTILE, 1.0F, 1.45F);
    }

    private void damageSakuraClaw(ServerLevel serverLevel, NatureAction action) {
        Vec3 right = new Vec3(-action.dir.z, 0.0D, action.dir.x).normalize();
        for (int claw = -1; claw <= 1; claw++) {
            Vec3 offset = right.scale(claw * 0.46D);
            for (int i = 1; i <= 18; i++) {
                Vec3 p = action.pos.add(offset).add(action.dir.scale(i * action.radius / 18.0D)).add(0.0D, 0.7D + i * 0.025D, 0.0D);
                serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES, p.x, p.y, p.z, 4, 0.12D, 0.08D, 0.12D, 0.03D);
                if (i % 2 == 0) {
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, p.x, p.y, p.z, 1, 0.03D, 0.02D, 0.03D, 0.01D);
                }
                if (i % 4 == 0) {
                    serverLevel.sendParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 1, 0.02D, 0.01D, 0.02D, 0.0D);
                }
            }
        }
        serverLevel.playSound(null, BlockPos.containing(action.pos), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.25F, 1.25F);
        AABB hitBox = new AABB(
                action.pos.x - action.radius, action.pos.y - 1.0D, action.pos.z - action.radius,
                action.pos.x + action.radius, action.pos.y + 3.0D, action.pos.z + action.radius
        );
        for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, hitBox, e -> e != this && e.isAlive())) {
            Vec3 toEntity = new Vec3(entity.getX() - action.pos.x, 0.0D, entity.getZ() - action.pos.z);
            double forward = toEntity.dot(action.dir);
            double side = Math.abs(toEntity.dot(right));
            if (forward >= 0.0D && forward <= action.radius && side <= 1.25D) {
                entity.hurt(this.damageSources().mobAttack(this), (float) action.damage);
                entity.push(action.dir.x * 0.45D, 0.15D, action.dir.z * 0.45D);
            }
        }
    }

    private void renderSakuraRainWarning(ServerLevel serverLevel, NatureAction action) {
        natureCircle(serverLevel, action.pos, action.radius, ParticleTypes.CHERRY_LEAVES, 128);
        natureCircle(serverLevel, action.pos, action.radius * 0.55D, ParticleTypes.END_ROD, 48);
        serverLevel.playSound(null, BlockPos.containing(action.pos), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.HOSTILE, 1.2F, 1.25F);
    }

    private void renderSakuraRain(ServerLevel serverLevel, NatureAction action) {
        int count = scaledParticlePoints(180);
        for (int i = 0; i < count; i++) {
            double angle = this.random.nextDouble() * Math.PI * 2.0D;
            double distance = Math.sqrt(this.random.nextDouble()) * action.radius;
            double x = action.pos.x + Math.cos(angle) * distance;
            double z = action.pos.z + Math.sin(angle) * distance;
            double y = action.pos.y + 7.0D + this.random.nextDouble() * 8.0D;
            serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES, x, y, z, 1, 0.4D, 0.1D, 0.4D, 0.02D);
            if (i % 24 == 0) {
                serverLevel.sendParticles(ParticleTypes.END_ROD, x, y, z, 1, 0.05D, 0.02D, 0.05D, 0.0D);
            }
        }
    }

    private void damageSakuraRain(NatureAction action) {
        for (LivingEntity entity : entitiesInCylinder(action.pos, action.radius, 12.0D)) {
            entity.hurt(this.damageSources().mobAttack(this), (float) action.damage);
        }
    }

    private void damageGrandBlossom(ServerLevel serverLevel, Vec3 center) {
        serverLevel.sendParticles(ModParticles.NATURE_LIGHT.get(), center.x, center.y + 1.0D, center.z, scaledParticlePoints(48), 4.0D, 0.8D, 4.0D, 0.08D);
        serverLevel.sendParticles(ParticleTypes.CHERRY_LEAVES, center.x, center.y + 1.0D, center.z, scaledParticlePoints(160), 8.0D, 1.2D, 8.0D, 0.15D);
        serverLevel.playSound(null, BlockPos.containing(center), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.5F, 1.25F);
        Vec3 safeA = center.add(5.0D, 0.0D, 0.0D);
        Vec3 safeB = center.add(-4.0D, 0.0D, 3.0D);
        for (LivingEntity entity : entitiesInCylinder(center, 12.0D, 4.0D)) {
            boolean safe = entity.position().distanceToSqr(safeA) <= 4.0D || entity.position().distanceToSqr(safeB) <= 4.0D;
            if (!safe) {
                Vec3 away = entity.position().subtract(center).normalize();
                entity.hurt(this.damageSources().mobAttack(this), MagicalGirlDamage.GRAND_BLOSSOM);
                entity.push(away.x, 0.35D, away.z);
            }
        }
    }

    private void damageCylinder(ServerLevel serverLevel, Vec3 center, double radius, double height, float damage, Vec3 knockDir, double knockback, boolean rootBurst) {
        serverLevel.sendParticles(rootBurst ? ModParticles.NATURE_ROOT.get() : ParticleTypes.CHERRY_LEAVES, center.x, center.y + 0.2D, center.z, rootBurst ? 18 : 42, radius * 0.5D, 0.25D, radius * 0.5D, 0.04D);
        serverLevel.playSound(null, BlockPos.containing(center), rootBurst ? SoundEvents.WOOD_BREAK : SoundEvents.AZALEA_BREAK, SoundSource.HOSTILE, 1.0F, 0.85F);
        for (LivingEntity entity : entitiesInCylinder(center, radius, height)) {
            entity.hurt(this.damageSources().mobAttack(this), damage);
            if (knockback > 0.0D) {
                Vec3 dir = knockDir.lengthSqr() > 0.01D ? knockDir.normalize() : entity.position().subtract(center).normalize();
                entity.push(dir.x * knockback, 0.15D, dir.z * knockback);
            }
        }
    }

    private List<LivingEntity> entitiesInCylinder(Vec3 center, double radius, double height) {
        AABB box = new AABB(center.x - radius, center.y - 1.0D, center.z - radius, center.x + radius, center.y + height, center.z + radius);
        return this.level().getEntitiesOfClass(LivingEntity.class, box, e -> e != this && e.isAlive() && e.position().distanceToSqr(center) <= radius * radius);
    }

    private void natureCircle(ServerLevel serverLevel, Vec3 center, double radius, net.minecraft.core.particles.ParticleOptions particle, int points) {
        points = scaledParticlePoints(points);
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0D * i / points;
            serverLevel.sendParticles(particle, center.x + Math.cos(angle) * radius, center.y + 0.08D, center.z + Math.sin(angle) * radius, 1, 0.02D, 0.01D, 0.02D, 0.0D);
        }
    }

    private BlockPos groundBlockPos(Vec3 pos) {
        int x = Mth.floor(pos.x);
        int z = Mth.floor(pos.z);
        int y = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        return new BlockPos(x, y, z);
    }

    private void spawnHand(Vec3 pos) {
        if (!this.level().isClientSide) {
            triggerCastAnimation();
            this.level().addFreshEntity(new MagicRightHandEntity(this.level(), pos.x, pos.y, pos.z));
        }
    }

    private void triggerCastAnimation() {
        this.entityData.set(CAST_ANIM_TICKS, 16);
        this.triggerAnim(CAST_CONTROLLER, getPhase() == MagicalGirlPhase.FINAL_PHASE ? CAST_BIG_MAGIC_TRIGGER : CAST_MAGIC_TRIGGER);
    }

    private void triggerBigCastAnimation() {
        this.entityData.set(CAST_ANIM_TICKS, 16);
        this.triggerAnim(CAST_CONTROLLER, CAST_BIG_MAGIC_TRIGGER);
    }

    private void spawnRavagerRaid(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        endPhantomAttack(true);
        endRavagerAttack(true);
        double angle = this.random.nextDouble() * Math.PI * 2.0D;
        double spawnRadius = 8.0D;
        double x = target.getX() + Math.cos(angle) * spawnRadius;
        double z = target.getZ() + Math.sin(angle) * spawnRadius;
        double y = target.getY();

        Ravager ravager = EntityType.RAVAGER.create(this.level());
        if (ravager == null) {
            return;
        }
        ravager.moveTo(x, y, z, this.random.nextFloat() * 360.0F, 0.0F);
        ravager.setTarget(target);
        ravager.getNavigation().moveTo(target, 1.25D);
        ravager.getPersistentData().putUUID(MAGIC_RAVAGER_OWNER_TAG, this.getUUID());
        serverLevel.addFreshEntity(ravager);
        trackSummon(this.ravagerSummonIds, ravager);
        this.ridingRavagerUuid = ravager.getUUID();
        this.ravagerAttackTicks = 0;
        this.startRiding(ravager, true);
        serverLevel.sendParticles(ParticleTypes.POOF, x, y + 1.0D, z, 36, 1.0D, 0.6D, 1.0D, 0.04D);

        int count = 4;
        for (int i = 0; i < count; i++) {
            Vindicator vindicator = EntityType.VINDICATOR.create(this.level());
            if (vindicator == null) {
                continue;
            }
            double vindicatorAngle = angle + Math.PI * 2.0D * i / count;
            double vindicatorRadius = 4.0D + this.random.nextDouble() * 3.0D;
            double vx = x + Math.cos(vindicatorAngle) * vindicatorRadius;
            double vz = z + Math.sin(vindicatorAngle) * vindicatorRadius;
            vindicator.moveTo(vx, y, vz, this.random.nextFloat() * 360.0F, 0.0F);
            vindicator.setTarget(target);
            vindicator.getNavigation().moveTo(target, 1.15D);
            vindicator.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
            vindicator.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
            vindicator.getPersistentData().putUUID(MAGIC_RAVAGER_OWNER_TAG, this.getUUID());
            serverLevel.addFreshEntity(vindicator);
            trackSummon(this.ravagerSummonIds, vindicator);
            serverLevel.sendParticles(ParticleTypes.SMOKE, vx, y + 1.0D, vz, 10, 0.4D, 0.4D, 0.4D, 0.02D);
        }
    }

    private void spawnPhantomSwarm(Player target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        endPhantomAttack(true);
        List<Player> targets = combatTargets(6);
        if (targets.isEmpty()) {
            targets = List.of(target);
        }
        int count = Math.min(phantomCap(getPhase()), 4 + targets.size());
        for (int i = 0; i < count; i++) {
            Player phantomTarget = targets.get(i % targets.size());
            Phantom phantom = spawnAttackPhantom(serverLevel, phantomTarget, i == 0, getPhase() == MagicalGirlPhase.FINAL_PHASE);
            if (phantom != null && i == 0) {
                AttributeInstance maxHealth = phantom.getAttribute(Attributes.MAX_HEALTH);
                if (maxHealth != null) {
                    maxHealth.setBaseValue(RIDING_PHANTOM_HEALTH);
                }
                phantom.setHealth((float) RIDING_PHANTOM_HEALTH);
                this.ridingPhantomUuid = phantom.getUUID();
                this.phantomAttackTicks = 0;
                this.startRiding(phantom, true);
                applyBulletResistanceForPhase(getPhase());
            }
        }
    }

    private Phantom spawnAttackPhantom(ServerLevel serverLevel, Player target, boolean riderMount, boolean vindicatorRider) {
        if (!riderMount && activeOwnedPhantomCount(serverLevel) >= phantomCap(getPhase())) {
            return null;
        }
        Phantom phantom = riderMount
                ? ModEntities.MAGICAL_GIRL_PHANTOM.get().create(this.level())
                : ModEntities.MAGICAL_PHANTOM.get().create(this.level());
        if (phantom == null) {
            return null;
        }
        double angle = this.random.nextDouble() * Math.PI * 2.0D;
        double radius = riderMount ? 0.0D : 5.0D + this.random.nextDouble() * 8.0D;
        double x = riderMount ? this.getX() : target.getX() + Math.cos(angle) * radius;
        double y = riderMount ? this.getY() - 0.8D : target.getY() + 8.0D + this.random.nextDouble() * 5.0D;
        double z = riderMount ? this.getZ() : target.getZ() + Math.sin(angle) * radius;
        phantom.moveTo(x, y, z, this.random.nextFloat() * 360.0F, 0.0F);
        if (!riderMount) {
            phantom.setTarget(target);
        }
        phantom.setPhantomSize(riderMount ? 6 : 2 + this.random.nextInt(2));
        phantom.getPersistentData().putInt(MAGIC_PHANTOM_LIFE_TAG, 20 * 90);
        phantom.getPersistentData().putUUID(MAGIC_PHANTOM_OWNER_TAG, this.getUUID());
        serverLevel.addFreshEntity(phantom);
        trackSummon(this.phantomSummonIds, phantom);
        serverLevel.sendParticles(ParticleTypes.WITCH, x, y, z, 24, 1.0D, 0.6D, 1.0D, 0.04D);
        if (vindicatorRider && !riderMount) {
            spawnGroundPhantomVindicator(serverLevel, target);
        }
        return phantom;
    }

    private void spawnGroundPhantomVindicator(ServerLevel serverLevel, Player target) {
        Vindicator vindicator = EntityType.VINDICATOR.create(this.level());
        if (vindicator == null) {
            return;
        }
        double angle = this.random.nextDouble() * Math.PI * 2.0D;
        double radius = 4.0D + this.random.nextDouble() * 6.0D;
        double x = target.getX() + Math.cos(angle) * radius;
        double z = target.getZ() + Math.sin(angle) * radius;
        double y = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mth.floor(x), Mth.floor(z));
        vindicator.moveTo(x, y, z, this.random.nextFloat() * 360.0F, 0.0F);
        vindicator.setTarget(target);
        vindicator.getNavigation().moveTo(target, 1.2D);
        vindicator.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
        vindicator.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        vindicator.getPersistentData().putUUID(MAGIC_PHANTOM_OWNER_TAG, this.getUUID());
        serverLevel.addFreshEntity(vindicator);
        trackSummon(this.phantomSummonIds, vindicator);
        serverLevel.sendParticles(ParticleTypes.SMOKE, x, y + 1.0D, z, 14, 0.5D, 0.5D, 0.5D, 0.03D);
    }

    private void tickPhantomAttack(MagicalGirlPhase phase) {
        if (this.ridingPhantomUuid == null) {
            return;
        }
        if (phase != MagicalGirlPhase.PHASE_2) {
            endPhantomAttack(true);
            return;
        }
        if (++this.phantomAttackTicks >= MAGIC_PHANTOM_ATTACK_TICKS) {
            this.endedSpecialCycle = currentSpecialCycle();
            endPhantomAttack(true);
            return;
        }
        Player target = this.level().getNearestPlayer(PLAYER_TARGETING, this);
        if (target != null && this.phantomAttackTicks % PHANTOM_REINFORCEMENT_INTERVAL == 0 && this.level() instanceof ServerLevel serverLevel) {
            int available = Math.max(0, phantomCap(phase) - activeOwnedPhantomCount(serverLevel));
            List<Player> targets = combatTargets(6);
            int count = Math.min(available, Math.min(targets.size(), phase == MagicalGirlPhase.FINAL_PHASE ? 4 : 3));
            for (int i = 0; i < count; i++) {
                spawnAttackPhantom(serverLevel, targets.get(i % targets.size()), false, phase == MagicalGirlPhase.FINAL_PHASE);
            }
        }
        Entity vehicle = this.getVehicle();
        if (!(vehicle instanceof Phantom phantom) || !phantom.getUUID().equals(this.ridingPhantomUuid) || !phantom.isAlive()) {
            this.endedSpecialCycle = currentSpecialCycle();
            endPhantomAttack(true);
        }
    }

    private int activeOwnedPhantomCount(ServerLevel serverLevel) {
        cleanupTrackedSummons(serverLevel, this.phantomSummonIds, false);
        int count = 0;
        for (UUID uuid : this.phantomSummonIds) {
            Entity entity = serverLevel.getEntity(uuid);
            if (entity instanceof Phantom && entity.isAlive()) {
                count++;
            }
        }
        return count;
    }

    private int phantomCap(MagicalGirlPhase phase) {
        return phase == MagicalGirlPhase.FINAL_PHASE ? FINAL_PHASE_PHANTOM_CAP : PHASE_2_PHANTOM_CAP;
    }

    private void tickRavagerAttack(MagicalGirlPhase phase) {
        if (this.ridingRavagerUuid == null) {
            return;
        }
        if (phase != MagicalGirlPhase.PHASE_1) {
            endRavagerAttack(true);
            return;
        }
        if (++this.ravagerAttackTicks >= MAGIC_RAVAGER_ATTACK_TICKS) {
            this.endedSpecialCycle = currentSpecialCycle();
            endRavagerAttack(true);
            return;
        }
        Entity vehicle = this.getVehicle();
        if (!(vehicle instanceof Ravager ravager) || !ravager.getUUID().equals(this.ridingRavagerUuid) || !ravager.isAlive()) {
            this.endedSpecialCycle = currentSpecialCycle();
            endRavagerAttack(true);
        }
    }

    private void endPhantomAttack(boolean removePhantoms) {
        this.stopRiding();
        this.ridingPhantomUuid = null;
        this.phantomAttackTicks = 0;
        applyBulletResistanceForPhase(getPhase());
        if (!removePhantoms || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        boolean hadTrackedSummons = !this.phantomSummonIds.isEmpty();
        cleanupTrackedSummons(serverLevel, this.phantomSummonIds, true);
        if (!hadTrackedSummons) {
            cleanupOwnedSummonsByTag(serverLevel, MAGIC_PHANTOM_OWNER_TAG, Phantom.class, Vindicator.class);
        }
    }

    private void endRavagerAttack(boolean removeRaiders) {
        this.stopRiding();
        this.ridingRavagerUuid = null;
        this.ravagerAttackTicks = 0;
        if (!removeRaiders || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        boolean hadTrackedSummons = !this.ravagerSummonIds.isEmpty();
        cleanupTrackedSummons(serverLevel, this.ravagerSummonIds, true);
        if (!hadTrackedSummons) {
            cleanupOwnedSummonsByTag(serverLevel, MAGIC_RAVAGER_OWNER_TAG, Ravager.class, Vindicator.class);
        }
    }

    private void trackSummon(List<UUID> ids, Entity entity) {
        UUID uuid = entity.getUUID();
        if (!ids.contains(uuid)) {
            ids.add(uuid);
        }
    }

    private void cleanupTrackedSummons(ServerLevel serverLevel, List<UUID> ids, boolean discard) {
        Iterator<UUID> it = ids.iterator();
        while (it.hasNext()) {
            Entity entity = serverLevel.getEntity(it.next());
            if (entity == null || !entity.isAlive()) {
                it.remove();
            } else if (discard) {
                entity.discard();
                it.remove();
            }
        }
    }

    @SafeVarargs
    private final void cleanupOwnedSummonsByTag(ServerLevel serverLevel, String ownerTag, Class<? extends Entity>... types) {
        UUID ownerUuid = this.getUUID();
        for (Entity entity : serverLevel.getAllEntities()) {
            if (!hasAnyType(entity, types)
                    || !entity.getPersistentData().hasUUID(ownerTag)
                    || !ownerUuid.equals(entity.getPersistentData().getUUID(ownerTag))) {
                continue;
            }
            entity.discard();
        }
    }

    private boolean hasAnyType(Entity entity, Class<? extends Entity>[] types) {
        for (Class<? extends Entity> type : types) {
            if (type.isInstance(entity)) {
                return true;
            }
        }
        return false;
    }

    private void blinkNear(Player target) {
        if (this.random.nextFloat() < 0.45F) {
            this.teleportTo(this.anchorX, this.anchorY, this.anchorZ);
            if (this.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.POOF, this.getX(), this.getY() + 1.0D, this.getZ(), 24, 0.5D, 0.8D, 0.5D, 0.02D);
            }
        }
    }

    @Override
    @Nullable
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return null;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        MagicalGirlPhase phase = getPhase();
        if (phase == MagicalGirlPhase.SEALED || phase == MagicalGirlPhase.SEAL_ESCAPE || phase == MagicalGirlPhase.SEAL_WAIT || phase == MagicalGirlPhase.INTRO || phase == MagicalGirlPhase.BREAK_1 || phase == MagicalGirlPhase.BREAK_2 || phase == MagicalGirlPhase.DEFEAT || phase == MagicalGirlPhase.FLASHBACK || phase == MagicalGirlPhase.END_HOOK) {
            return false;
        }
        if (this.getHealth() - amount <= 1.0F) {
            this.setHealth(1.0F);
            if (phase == MagicalGirlPhase.PHASE_1) {
                enterPhase(MagicalGirlPhase.BREAK_1);
            } else if (phase == MagicalGirlPhase.PHASE_2) {
                enterPhase(MagicalGirlPhase.BREAK_2);
            } else {
                enterPhase(MagicalGirlPhase.DEFEAT);
            }
            return true;
        }
        return super.hurt(source, amount);
    }

    private void enterPhase(MagicalGirlPhase phase) {
        if (getPhase() != phase) {
            endPhantomAttack(true);
            endRavagerAttack(true);
        }
        this.entityData.set(PHASE, phase.ordinal());
        this.entityData.set(RED_EYES, phase == MagicalGirlPhase.FINAL_PHASE || phase == MagicalGirlPhase.DEFEAT || phase == MagicalGirlPhase.FLASHBACK || phase == MagicalGirlPhase.END_HOOK);
        applyBulletResistanceForPhase(phase);
        this.stateTicks = 0;
        this.lineIndex = 0;
        this.attackCooldown = 50;
        this.endedSpecialCycle = -1;
        this.natureActions.clear();
        this.phase3Actions.clear();
        cleanupPhase3Mines(true);
        if (phase == MagicalGirlPhase.PHASE_1) {
            this.usedGrandBlossom = false;
        } else if (phase == MagicalGirlPhase.PHASE_2) {
            this.usedGrandSpellNova = false;
        } else if (phase == MagicalGirlPhase.FINAL_PHASE) {
            this.usedEmergencyExecution = false;
        }
        this.bossEvent.setColor(switch (phase) {
            case PHASE_2, BREAK_2 -> BossEvent.BossBarColor.PURPLE;
            case FINAL_PHASE -> BossEvent.BossBarColor.RED;
            default -> BossEvent.BossBarColor.PINK;
        });
        if (phase == MagicalGirlPhase.PHASE_1 || phase == MagicalGirlPhase.PHASE_2 || phase == MagicalGirlPhase.FINAL_PHASE) {
            double maxHealth = phaseMaxHealth(phase);
            this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(maxHealth);
            this.setHealth((float) maxHealth);
        }
        this.airMoveTargetTicks = 0;
    }

    private void applyBulletResistanceForPhase(MagicalGirlPhase phase) {
        AttributeInstance bulletResistance = this.getAttribute(ModAttributes.BULLET_RESISTANCE.get());
        if (bulletResistance == null) {
            return;
        }
        bulletResistance.setBaseValue(phaseBulletResistance(phase));
    }

    private double phaseBulletResistance(MagicalGirlPhase phase) {
        if (isRidingMagicPhantom()) {
            return PHANTOM_RIDING_BULLET_RESISTANCE;
        }
        return switch (phase) {
            case PHASE_1, BREAK_1 -> PHASE_1_BULLET_RESISTANCE;
            case PHASE_2, BREAK_2 -> PHASE_2_BULLET_RESISTANCE;
            case FINAL_PHASE -> FINAL_PHASE_BULLET_RESISTANCE;
            default -> 0.0D;
        };
    }

    private boolean isRidingMagicPhantom() {
        Entity vehicle = this.getVehicle();
        return vehicle instanceof Phantom && (this.ridingPhantomUuid == null || vehicle.getUUID().equals(this.ridingPhantomUuid));
    }

    private MagicalGirlPhase nextCombatPhase(MagicalGirlPhase phase) {
        return phase == MagicalGirlPhase.BREAK_1 ? MagicalGirlPhase.PHASE_2 : MagicalGirlPhase.FINAL_PHASE;
    }

    private double phaseMaxHealth(MagicalGirlPhase phase) {
        return switch (phase) {
            case PHASE_1 -> 5000.0D;
            case PHASE_2 -> 10000.0D;
            case FINAL_PHASE -> 30000.0D;
            default -> 5000.0D;
        };
    }

    private void sayNextLine() {
        List<String> lines = MagicalGirlLines.forPhase(getPhase());
        if (this.lineIndex >= lines.size()) {
            return;
        }
        Component message = Component.literal("<" + speakerName() + "> " + lines.get(this.lineIndex++));
        if (this.level() instanceof ServerLevel serverLevel) {
            for (ServerPlayer player : serverLevel.players()) {
                if (player.distanceToSqr(this) < 96.0D * 96.0D) {
                    player.sendSystemMessage(message);
                }
            }
        }
    }

    private String speakerName() {
        return "魔法少女";
    }

    private void updateBossBar() {
        MagicalGirlPhase phase = getPhase();
        if (phase == MagicalGirlPhase.SEALED || phase == MagicalGirlPhase.SEAL_ESCAPE || phase == MagicalGirlPhase.SEAL_WAIT) {
            this.bossEvent.setVisible(false);
            this.bossEvent.setProgress(0.0F);
            return;
        }
        this.bossEvent.setVisible(true);
        if (phase == MagicalGirlPhase.INTRO) {
            this.bossEvent.setProgress(Math.min(1.0F, this.stateTicks / 130.0F));
        } else if (phase == MagicalGirlPhase.BREAK_1 || phase == MagicalGirlPhase.BREAK_2) {
            this.bossEvent.setProgress(Math.min(1.0F, this.stateTicks / 120.0F));
        } else if (phase == MagicalGirlPhase.DEFEAT || phase == MagicalGirlPhase.FLASHBACK) {
            this.bossEvent.setProgress(0.0F);
        } else {
            this.bossEvent.setProgress(Math.max(0.0F, this.getHealth() / this.getMaxHealth()));
        }
    }

    private MagicalGirlPhase getPhase() {
        int ordinal = this.entityData.get(PHASE);
        MagicalGirlPhase[] values = MagicalGirlPhase.values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : MagicalGirlPhase.INTRO;
    }

    public MagicalGirlPhase getVisualPhase() {
        return getPhase();
    }

    public int getPhaseTicks() {
        return this.stateTicks;
    }

    public boolean hasRedEyes() {
        return this.entityData.get(RED_EYES);
    }

    public boolean isSealed() {
        return getPhase() == MagicalGirlPhase.SEALED;
    }

    public boolean isBreakingSeal() {
        return getPhase() == MagicalGirlPhase.SEAL_ESCAPE;
    }

    public float getSealEscapeBreakProgress(float partialTicks) {
        return getPhase() == MagicalGirlPhase.SEAL_ESCAPE
                ? (float) sealEscapeMoveProgress(partialTicks)
                : 0.0F;
    }

    public float getCastAnimationProgress(float partialTicks) {
        int ticks = this.entityData.get(CAST_ANIM_TICKS);
        if (ticks <= 0) {
            return 0.0F;
        }
        return Mth.clamp((ticks - partialTicks) / 16.0F, 0.0F, 1.0F);
    }

    public boolean hasFixedSkyColor() {
        return switch (getVisualPhase()) {
            case PHASE_1, BREAK_1, PHASE_2, BREAK_2, FINAL_PHASE, DEFEAT, FLASHBACK, END_HOOK -> true;
            default -> false;
        };
    }

    public Vec3 getFixedSkyColor() {
        return switch (getVisualPhase()) {
            case PHASE_1, BREAK_1 -> new Vec3(0.28D, 0.72D, 0.42D);
            case PHASE_2, BREAK_2 -> new Vec3(0.55D, 0.24D, 0.92D);
            case FINAL_PHASE -> new Vec3(0.95D, 0.10D, 0.22D);
            case DEFEAT, FLASHBACK, END_HOOK -> new Vec3(0.20D, 0.30D, 0.45D);
            default -> new Vec3(0.95D, 0.34D, 0.72D);
        };
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "base", 4, state -> {
            MagicalGirlPhase phase = getPhase();
            if (phase == MagicalGirlPhase.SEALED || phase == MagicalGirlPhase.SEAL_ESCAPE || phase == MagicalGirlPhase.SEAL_WAIT || phase == MagicalGirlPhase.DEFEAT) {
                return state.setAndContinue(DEFEAT_FALL_ANIM);
            } else if (phase == MagicalGirlPhase.FLASHBACK || phase == MagicalGirlPhase.END_HOOK) {
                return state.setAndContinue(DEFEAT_IDLE_ANIM);
            } else if (this.isPassenger()) {
                return state.setAndContinue(RIDING_IDLE_ANIM);
            } else if (phase == MagicalGirlPhase.PHASE_1 || phase == MagicalGirlPhase.PHASE_2 || phase == MagicalGirlPhase.FINAL_PHASE) {
                return state.setAndContinue(this.getDeltaMovement().horizontalDistanceSqr() > 0.0001D ? MOVE_FLOATING_ANIM : IDLE_FLOATING_ANIM);
            }
            return state.setAndContinue(IDLE_FLOATING_ANIM);
        }));
        controllers.add(new AnimationController<>(this, CAST_CONTROLLER, 0, state -> PlayState.STOP)
                .triggerableAnim(CAST_MAGIC_TRIGGER, CAST_MAGIC_ANIM)
                .triggerableAnim(CAST_BIG_MAGIC_TRIGGER, CAST_BIG_MAGIC_ANIM)
                .receiveTriggeredAnimations());
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public double getTick(Object object) {
        return this.tickCount;
    }

    private void executeEndFunctionOnce() {
        if (this.endFunctionExecuted || !(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        this.endFunctionExecuted = true;
        String functionName = ServerConfig.MAGICAL_GIRL_END_FUNCTION.get();
        if (functionName == null || functionName.isBlank()) {
            return;
        }
        ResourceLocation id = ResourceLocation.tryParse(functionName);
        if (id == null) {
            Meatwo310.LOGGER.warn("Invalid magical girl end function id: {}", functionName);
            return;
        }
        ServerFunctionManager functions = serverLevel.getServer().getFunctions();
        functions.get(id).ifPresent(function -> functions.execute(function, this.createCommandSourceStack().withPermission(2).withSuppressedOutput()));
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public double getMyRidingOffset() {
        if (this.getVehicle() instanceof Ravager) {
            return -0.85D;
        }
        if (this.getVehicle() instanceof Phantom) {
            return -0.35D;
        }
        return super.getMyRidingOffset();
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(PHASE, tag.contains("MagicalGirlPhase")
                ? tag.getInt("MagicalGirlPhase")
                : MagicalGirlPhase.SEALED.ordinal());
        applyBulletResistanceForPhase(getPhase());
        this.entityData.set(RED_EYES, tag.getBoolean("RedEyes"));
        this.entityData.set(CAST_ANIM_TICKS, tag.getInt("CastAnimTicks"));
        this.stateTicks = tag.getInt("StateTicks");
        this.lineIndex = tag.getInt("LineIndex");
        this.endFunctionExecuted = tag.getBoolean("EndFunctionExecuted");
        if (tag.hasUUID("RidingPhantomUuid")) {
            this.ridingPhantomUuid = tag.getUUID("RidingPhantomUuid");
        }
        this.phantomAttackTicks = tag.getInt("PhantomAttackTicks");
        if (tag.hasUUID("RidingRavagerUuid")) {
            this.ridingRavagerUuid = tag.getUUID("RidingRavagerUuid");
        }
        this.ravagerAttackTicks = tag.getInt("RavagerAttackTicks");
        this.endedSpecialCycle = tag.getInt("EndedSpecialCycle");
        if (tag.contains("AnchorX")) {
            this.anchorX = tag.getDouble("AnchorX");
            this.anchorY = tag.getDouble("AnchorY");
            this.anchorZ = tag.getDouble("AnchorZ");
        }
        if (tag.contains("AirMoveTargetX")) {
            this.airMoveTarget = new Vec3(tag.getDouble("AirMoveTargetX"), tag.getDouble("AirMoveTargetY"), tag.getDouble("AirMoveTargetZ"));
            this.airMoveTargetTicks = tag.getInt("AirMoveTargetTicks");
        }
        if (tag.contains("SealedYaw")) {
            this.sealedYaw = tag.getFloat("SealedYaw");
        }
        if (tag.contains("SealEscapeStartX")) {
            this.sealEscapeStart = new Vec3(tag.getDouble("SealEscapeStartX"), tag.getDouble("SealEscapeStartY"), tag.getDouble("SealEscapeStartZ"));
            this.sealEscapeTarget = new Vec3(tag.getDouble("SealEscapeTargetX"), tag.getDouble("SealEscapeTargetY"), tag.getDouble("SealEscapeTargetZ"));
        }
        this.usedGrandBlossom = tag.getBoolean("UsedGrandBlossom");
        this.usedGrandSpellNova = tag.getBoolean("UsedGrandSpellNova");
        this.usedEmergencyExecution = tag.getBoolean("UsedEmergencyExecution");
        applyBulletResistanceForPhase(getPhase());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("MagicalGirlPhase", this.entityData.get(PHASE));
        tag.putBoolean("RedEyes", this.entityData.get(RED_EYES));
        tag.putInt("CastAnimTicks", this.entityData.get(CAST_ANIM_TICKS));
        tag.putInt("StateTicks", this.stateTicks);
        tag.putInt("LineIndex", this.lineIndex);
        tag.putBoolean("EndFunctionExecuted", this.endFunctionExecuted);
        if (this.ridingPhantomUuid != null) {
            tag.putUUID("RidingPhantomUuid", this.ridingPhantomUuid);
        }
        tag.putInt("PhantomAttackTicks", this.phantomAttackTicks);
        if (this.ridingRavagerUuid != null) {
            tag.putUUID("RidingRavagerUuid", this.ridingRavagerUuid);
        }
        tag.putInt("RavagerAttackTicks", this.ravagerAttackTicks);
        tag.putInt("EndedSpecialCycle", this.endedSpecialCycle);
        if (!Double.isNaN(this.anchorX)) {
            tag.putDouble("AnchorX", this.anchorX);
            tag.putDouble("AnchorY", this.anchorY);
            tag.putDouble("AnchorZ", this.anchorZ);
        }
        tag.putDouble("AirMoveTargetX", this.airMoveTarget.x);
        tag.putDouble("AirMoveTargetY", this.airMoveTarget.y);
        tag.putDouble("AirMoveTargetZ", this.airMoveTarget.z);
        tag.putInt("AirMoveTargetTicks", this.airMoveTargetTicks);
        if (!Float.isNaN(this.sealedYaw)) {
            tag.putFloat("SealedYaw", this.sealedYaw);
        }
        if (this.sealEscapeStart != Vec3.ZERO || this.sealEscapeTarget != Vec3.ZERO) {
            tag.putDouble("SealEscapeStartX", this.sealEscapeStart.x);
            tag.putDouble("SealEscapeStartY", this.sealEscapeStart.y);
            tag.putDouble("SealEscapeStartZ", this.sealEscapeStart.z);
            tag.putDouble("SealEscapeTargetX", this.sealEscapeTarget.x);
            tag.putDouble("SealEscapeTargetY", this.sealEscapeTarget.y);
            tag.putDouble("SealEscapeTargetZ", this.sealEscapeTarget.z);
        }
        tag.putBoolean("UsedGrandBlossom", this.usedGrandBlossom);
        tag.putBoolean("UsedGrandSpellNova", this.usedGrandSpellNova);
        tag.putBoolean("UsedEmergencyExecution", this.usedEmergencyExecution);
    }

    private enum NatureActionType {
        ROOT_LANCE_WARN,
        ROOT_LANCE_HIT,
        BLOOM_WARN,
        BLOOM_HIT,
        IVY_WARN,
        IVY_HIT,
        PETAL_WARN,
        PETAL_HIT,
        SAKURA_CLAW_WARN,
        SAKURA_CLAW_HIT,
        TORNADO_WARN,
        TORNADO_TICK,
        TORNADO_FINISH,
        SAKURA_RAIN_WARN,
        SAKURA_RAIN_TICK,
        SAKURA_RAIN_HIT,
        GRAND_WARN,
        GRAND_HIT
    }

    private static final class NatureAction {
        private final NatureActionType type;
        private int ticks;
        private final Vec3 pos;
        private final Vec3 dir;
        private final double radius;
        private final double damage;

        private NatureAction(NatureActionType type, int ticks, Vec3 pos, Vec3 dir, double radius, double damage) {
            this.type = type;
            this.ticks = ticks;
            this.pos = pos;
            this.dir = dir;
            this.radius = radius;
            this.damage = damage;
        }
    }

    private enum Phase3ActionType {
        SUPERB_SHOT,
        CHEMICAL_AREA,
        IGNITION_REACTION,
        NEEDLE_SHOT,
        PLACE_MINE,
        FINAL_EXPLOSION
    }

    private static final class Phase3Action {
        private final Phase3ActionType type;
        private int ticks;
        private final Vec3 pos;
        private final Vec3 dir;
        private final double radius;
        private final double damage;
        private final double extra;

        private Phase3Action(Phase3ActionType type, int ticks, Vec3 pos, Vec3 dir, double radius, double damage, double extra) {
            this.type = type;
            this.ticks = ticks;
            this.pos = pos;
            this.dir = dir;
            this.radius = radius;
            this.damage = damage;
            this.extra = extra;
        }
    }

}
