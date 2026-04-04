package com.dousiyo.meatwo310.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class KillMessageS2CPacket {
    public static Consumer<KillMessageS2CPacket> CLIENT_HANDLER = msg -> {};

    private final String victimName;
    private final boolean hasVictimColor;
    private final int victimColor;

    public KillMessageS2CPacket(String victimName, boolean hasVictimColor, int victimColor) {
        this.victimName = victimName;
        this.hasVictimColor = hasVictimColor;
        this.victimColor = victimColor;
    }

    public String victimName() {
        return victimName;
    }

    public boolean hasVictimColor() {
        return hasVictimColor;
    }

    public int victimColor() {
        return victimColor;
    }

    public static void encode(KillMessageS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.victimName, 64);
        buf.writeBoolean(msg.hasVictimColor);
        buf.writeInt(msg.victimColor);
    }

    public static KillMessageS2CPacket decode(FriendlyByteBuf buf) {
        return new KillMessageS2CPacket(buf.readUtf(64), buf.readBoolean(), buf.readInt());
    }

    public static void handle(KillMessageS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> CLIENT_HANDLER.accept(msg));
        ctx.get().setPacketHandled(true);
    }
}
