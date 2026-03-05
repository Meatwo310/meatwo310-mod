package com.dousiyo.meatwo310.capture.core;

import com.dousiyo.meatwo310.capture.data.CapturePointsDefinition;
import com.dousiyo.meatwo310.config.ServerConfig;
import com.dousiyo.meatwo310.network.CapturePointEventS2CPacket;
import com.dousiyo.meatwo310.network.ModNetwork;
import com.dousiyo.meatwo310.network.PlayerPointFocusS2CPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Team;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

public final class CaptureManager {
    private static final float EPSILON = 1.0e-4F;
    private static final float DECAP_RATE_PER_TICK = 0.0125F; // 5% every 2 ticks
    private static final Map<MinecraftServer, CaptureManager> INSTANCES = new WeakHashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Path definitionPath;
    private CapturePointsDefinition definition;
    private final Map<Integer, PointRuntime> points = new LinkedHashMap<>();
    private final Map<UUID, Integer> focusByPlayer = new HashMap<>();

    private long nextOccupancyCheckGameTime;

    private CaptureManager(Path definitionPath) {
        this.definitionPath = definitionPath;
        this.definition = CapturePointsDefinition.empty();
        reloadInMemoryDefinition(CapturePointsDefinition.empty());
    }

    public static CaptureManager get(ServerLevel level) {
        return INSTANCES.computeIfAbsent(level.getServer(), ignored -> {
            CaptureManager manager = new CaptureManager(FMLPaths.CONFIGDIR.get().resolve("capture_points.json"));
            manager.reloadFromDisk(level, false);
            return manager;
        });
    }

    public int reloadFromDisk(ServerLevel level, boolean broadcast) {
        CapturePointsDefinition loaded;
        try {
            loaded = CapturePointsDefinition.load(definitionPath);
        } catch (Exception e) {
            LOGGER.error("Failed to load capture points from {}", definitionPath, e);
            loaded = CapturePointsDefinition.empty();
        }

        reloadInMemoryDefinition(loaded);
        nextOccupancyCheckGameTime = 0L;

        if (broadcast) {
            syncAllPlayers(level);
        }
        return loaded.size();
    }

    public Collection<CapturePointsDefinition.PointDefinition> listPoints() {
        return definition.points();
    }

    public Optional<CapturePointsDefinition.PointDefinition> getPoint(int slot) {
        return definition.get(slot);
    }

    public void setPointArea(ServerLevel level, int slot, int x1, int y1, int z1, int x2, int y2, int z2) {
        String id = definition.get(slot).map(CapturePointsDefinition.PointDefinition::id).orElse("slot_" + slot);
        CapturePointsDefinition.PointDefinition point = new CapturePointsDefinition.PointDefinition(slot, id, x1, y1, z1, x2, y2, z2).normalized();
        definition = definition.withPoint(point);
        reloadInMemoryDefinition(definition);
        syncAllPlayers(level);
    }

    public void removePoint(ServerLevel level, int slot) {
        definition = definition.withoutSlot(slot);
        reloadInMemoryDefinition(definition);
        syncAllPlayers(level);
    }

    public void onPlayerJoin(ServerPlayer player) {
        syncAllPointsToPlayer(player);
        syncFocusToPlayer(player, findContainingSlot(player));
    }

    public void onPlayerLogout(ServerPlayer player) {
        focusByPlayer.remove(player.getUUID());
    }

    public void serverTick(ServerLevel level) {
        long now = level.getGameTime();
        processCaptureCompletions(level, now);

        if (now < nextOccupancyCheckGameTime) {
            return;
        }

        int interval = Math.max(1, ServerConfig.CAPTURE_OCCUPANCY_UPDATE_INTERVAL_TICKS.get());
        nextOccupancyCheckGameTime = now + interval;

        List<ServerPlayer> players = level.players();
        processOccupancy(level, now, players);
        updateFocusSlots(level);
    }

    private void reloadInMemoryDefinition(CapturePointsDefinition newDefinition) {
        this.definition = newDefinition;
        points.clear();
        for (CapturePointsDefinition.PointDefinition point : newDefinition.points()) {
            points.put(point.slot(), PointRuntime.neutral(point));
        }
    }

    private void processCaptureCompletions(ServerLevel level, long now) {
        for (PointRuntime point : points.values()) {
            if (point.state != PointState.CAPTURING) {
                continue;
            }

            float progressNow = point.progressAt(now);
            if (progressNow >= 1.0F - EPSILON) {
                point.progress = 1.0F;
                point.state = PointState.OWNED;
                point.owner = TeamSide.BLUE;
                point.captureTeam = TeamSide.NONE;
                point.ratePerTick = 0.0F;
                point.pendingTeam = TeamSide.NONE;
                broadcastState(level, point, now);
            } else if (progressNow <= 0.0F + EPSILON) {
                point.progress = 0.0F;
                point.state = PointState.OWNED;
                point.owner = TeamSide.RED;
                point.captureTeam = TeamSide.NONE;
                point.ratePerTick = 0.0F;
                point.pendingTeam = TeamSide.NONE;
                broadcastState(level, point, now);
            }
        }
    }

    private void processOccupancy(ServerLevel level, long now, List<ServerPlayer> players) {
        for (PointRuntime point : points.values()) {
            int blueCount = 0;
            int redCount = 0;
            for (ServerPlayer player : players) {
                if (!point.aabb.contains(player.position())) {
                    continue;
                }
                TeamSide side = resolvePlayerTeam(player);
                if (side == TeamSide.BLUE) {
                    blueCount++;
                } else if (side == TeamSide.RED) {
                    redCount++;
                }
            }

            float progressNow = point.progressAt(now);
            if (blueCount > 0 && redCount > 0) {
                boolean changed = applySnapshot(point, PointState.CONTESTED, ownerFromProgress(progressNow), progressNow,
                        TeamSide.NONE, 0.0F, now);
                point.pendingTeam = TeamSide.NONE;
                if (changed) {
                    broadcastState(level, point, now);
                }
                continue;
            }

            if (blueCount == 0 && redCount == 0) {
                PointState restingState = restingState(progressNow);
                boolean changed = applySnapshot(point, restingState, ownerFromProgress(progressNow), progressNow,
                        TeamSide.NONE, 0.0F, now);
                point.pendingTeam = TeamSide.NONE;
                if (changed) {
                    broadcastState(level, point, now);
                }
                continue;
            }

            TeamSide dominant = blueCount > 0 ? TeamSide.BLUE : TeamSide.RED;
            if ((dominant == TeamSide.BLUE && progressNow >= 1.0F - EPSILON)
                    || (dominant == TeamSide.RED && progressNow <= 0.0F + EPSILON)) {
                TeamSide owner = dominant;
                float edgeProgress = dominant == TeamSide.BLUE ? 1.0F : 0.0F;
                boolean changed = applySnapshot(point, PointState.OWNED, owner, edgeProgress,
                        TeamSide.NONE, 0.0F, now);
                point.pendingTeam = TeamSide.NONE;
                if (changed) {
                    broadcastState(level, point, now);
                }
                continue;
            }

            if (point.state == PointState.CAPTURING) {
                if (point.captureTeam == dominant) {
                    boolean decapPhaseNow = (dominant == TeamSide.BLUE && progressNow < 0.5F - EPSILON)
                            || (dominant == TeamSide.RED && progressNow > 0.5F + EPSILON);
                    float expectedRate = decapPhaseNow
                            ? (dominant == TeamSide.BLUE ? DECAP_RATE_PER_TICK : -DECAP_RATE_PER_TICK)
                            : (dominant == TeamSide.BLUE ? baseRatePerTick() : -baseRatePerTick());
                    if (Math.abs(point.ratePerTick - expectedRate) > EPSILON) {
                        boolean changed = applySnapshot(point, PointState.CAPTURING, ownerFromProgress(progressNow),
                                progressNow, dominant, expectedRate, now);
                        if (changed) {
                            broadcastState(level, point, now);
                        }
                    }
                    point.pendingTeam = TeamSide.NONE;
                    continue;
                }
                boolean changed = applySnapshot(point, PointState.CONTESTED, ownerFromProgress(progressNow), progressNow,
                        TeamSide.NONE, 0.0F, now);
                point.pendingTeam = TeamSide.NONE;
                if (changed) {
                    broadcastState(level, point, now);
                }
                continue;
            }

            if (point.pendingTeam != dominant) {
                point.pendingTeam = dominant;
                point.pendingStartGameTime = now;
                PointState restingState = restingState(progressNow);
                boolean changed = applySnapshot(point, restingState, ownerFromProgress(progressNow), progressNow,
                        TeamSide.NONE, 0.0F, now);
                if (changed) {
                    broadcastState(level, point, now);
                }
                continue;
            }

            long delayTicks = (long) Math.max(0, ServerConfig.CAPTURE_START_DELAY_SECONDS.get()) * 20L;
            if (now - point.pendingStartGameTime < delayTicks) {
                continue;
            }

            boolean decapPhase = (dominant == TeamSide.BLUE && progressNow < 0.5F - EPSILON)
                    || (dominant == TeamSide.RED && progressNow > 0.5F + EPSILON);
            float rate;
            if (decapPhase) {
                rate = dominant == TeamSide.BLUE ? DECAP_RATE_PER_TICK : -DECAP_RATE_PER_TICK;
            } else {
                rate = dominant == TeamSide.BLUE ? baseRatePerTick() : -baseRatePerTick();
            }

            boolean changed = applySnapshot(point, PointState.CAPTURING, ownerFromProgress(progressNow), progressNow,
                    dominant, rate, now);
            point.pendingTeam = TeamSide.NONE;
            if (changed) {
                broadcastState(level, point, now);
            }
        }
    }

    private void updateFocusSlots(ServerLevel level) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            int nextSlot = player.level() == level ? findContainingSlot(player) : -1;
            int previousSlot = focusByPlayer.getOrDefault(player.getUUID(), -1);
            if (previousSlot == nextSlot) {
                continue;
            }
            syncFocusToPlayer(player, nextSlot);
        }
    }

    private int findContainingSlot(ServerPlayer player) {
        for (PointRuntime point : points.values()) {
            if (point.aabb.contains(player.position())) {
                return point.slot;
            }
        }
        return -1;
    }

    private TeamSide resolvePlayerTeam(ServerPlayer player) {
        Team team = player.getTeam();
        if (team == null) {
            return TeamSide.NONE;
        }
        String teamName = team.getName();
        if (teamName.equals(ServerConfig.CAPTURE_BLUE_TEAM_NAME.get())) {
            return TeamSide.BLUE;
        }
        if (teamName.equals(ServerConfig.CAPTURE_RED_TEAM_NAME.get())) {
            return TeamSide.RED;
        }
        return TeamSide.NONE;
    }

    private PointState restingState(float progress) {
        TeamSide owner = ownerFromProgress(progress);
        return owner == TeamSide.NONE ? PointState.IDLE : PointState.OWNED;
    }

    private TeamSide ownerFromProgress(float progress) {
        if (progress >= 1.0F - EPSILON) {
            return TeamSide.BLUE;
        }
        if (progress <= 0.0F + EPSILON) {
            return TeamSide.RED;
        }
        return TeamSide.NONE;
    }

    private boolean applySnapshot(PointRuntime point,
                                  PointState nextState,
                                  TeamSide nextOwner,
                                  float nextProgress,
                                  TeamSide nextCaptureTeam,
                                  float nextRatePerTick,
                                  long now) {
        float clamped = clamp(nextProgress);

        boolean changed = point.state != nextState
                || point.owner != nextOwner
                || Math.abs(point.progress - clamped) > EPSILON
                || point.captureTeam != nextCaptureTeam
                || Math.abs(point.ratePerTick - nextRatePerTick) > EPSILON;

        if (!changed) {
            return false;
        }

        point.state = nextState;
        point.owner = nextOwner;
        point.progress = clamped;
        point.captureTeam = nextCaptureTeam;
        point.ratePerTick = nextRatePerTick;
        point.captureStartProgress = clamped;
        point.captureStartGameTime = now;
        return true;
    }

    private float clamp(float value) {
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private float baseRatePerTick() {
        float captureSeconds = Math.max(1, ServerConfig.CAPTURE_SECONDS.get());
        return 0.5F / (captureSeconds * 20.0F);
    }

    private void syncAllPlayers(ServerLevel level) {
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            syncAllPointsToPlayer(player);
            syncFocusToPlayer(player, player.level() == level ? findContainingSlot(player) : -1);
        }
    }

    private void syncAllPointsToPlayer(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        for (PointRuntime point : points.values()) {
            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), buildPacket(point, now));
        }
    }

    private void broadcastState(ServerLevel level, PointRuntime point, long now) {
        ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), buildPacket(point, now));
    }

    private CapturePointEventS2CPacket buildPacket(PointRuntime point, long now) {
        float progress0 = point.progressAt(now);
        float rate = point.state == PointState.CAPTURING ? point.ratePerTick : 0.0F;
        TeamSide captureTeam = point.state == PointState.CAPTURING ? point.captureTeam : TeamSide.NONE;
        return new CapturePointEventS2CPacket((byte) point.slot, now, point.state, point.owner, progress0, captureTeam, rate);
    }

    private void syncFocusToPlayer(ServerPlayer player, int slot) {
        focusByPlayer.put(player.getUUID(), slot);
        ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new PlayerPointFocusS2CPacket((byte) slot));
    }

    private static final class PointRuntime {
        private final int slot;
        private final String id;
        private final AABB aabb;

        private PointState state = PointState.IDLE;
        private TeamSide owner = TeamSide.NONE;
        private float progress = 0.5F;
        private TeamSide captureTeam = TeamSide.NONE;
        private float ratePerTick = 0.0F;
        private float captureStartProgress = 0.5F;
        private long captureStartGameTime = 0L;
        private TeamSide pendingTeam = TeamSide.NONE;
        private long pendingStartGameTime = 0L;

        private PointRuntime(int slot, String id, AABB aabb) {
            this.slot = slot;
            this.id = id;
            this.aabb = aabb;
        }

        static PointRuntime neutral(CapturePointsDefinition.PointDefinition definition) {
            return new PointRuntime(definition.slot(), definition.id(), definition.toAabb());
        }

        float progressAt(long nowGameTime) {
            if (state != PointState.CAPTURING) {
                return progress;
            }
            long dt = Math.max(0L, nowGameTime - captureStartGameTime);
            float next = captureStartProgress + ratePerTick * dt;
            if (next < 0.0F) {
                return 0.0F;
            }
            if (next > 1.0F) {
                return 1.0F;
            }
            return next;
        }
    }
}








