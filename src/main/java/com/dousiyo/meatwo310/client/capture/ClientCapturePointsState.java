package com.dousiyo.meatwo310.client.capture;

import com.dousiyo.meatwo310.capture.core.PointState;
import com.dousiyo.meatwo310.capture.core.TeamSide;
import com.dousiyo.meatwo310.network.CapturePointEventS2CPacket;
import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientCapturePointsState {
    private static final Map<Integer, Snapshot> POINTS = new ConcurrentHashMap<>();
    private static volatile int focusedSlot = -1;

    private ClientCapturePointsState() {}

    public static void applyPointEvent(CapturePointEventS2CPacket msg) {
        int slot = msg.getSlotIndex();
        if (slot < 0 || slot > 4) {
            return;
        }
        POINTS.put(slot, new Snapshot(msg.getServerGameTime(), msg.getState(), msg.getOwner(),
                msg.getProgress0(), msg.getCaptureTeam(), msg.getRatePerTick()));
    }

    public static void applyFocusSlot(int slotIndex) {
        focusedSlot = slotIndex;
    }

    public static int getFocusedSlot() {
        return focusedSlot;
    }

    public static List<Integer> getSortedSlots() {
        List<Integer> slots = new ArrayList<>(POINTS.keySet());
        slots.sort(Comparator.naturalOrder());
        return slots;
    }

    public static Snapshot getSnapshot(int slot) {
        return POINTS.get(slot);
    }

    public static float resolveProgress(Snapshot snapshot, Minecraft mc) {
        if (snapshot == null) {
            return 0.5F;
        }
        if (mc.level == null || snapshot.state != PointState.CAPTURING) {
            return snapshot.progress0;
        }

        long dt = Math.max(0L, mc.level.getGameTime() - snapshot.serverGameTime);
        float next = snapshot.progress0 + snapshot.ratePerTick * dt;
        if (next < 0.0F) {
            return 0.0F;
        }
        if (next > 1.0F) {
            return 1.0F;
        }
        return next;
    }

    public record Snapshot(long serverGameTime,
                           PointState state,
                           TeamSide owner,
                           float progress0,
                           TeamSide captureTeam,
                           float ratePerTick) {
    }
}

