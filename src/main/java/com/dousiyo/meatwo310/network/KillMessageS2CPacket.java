package com.dousiyo.meatwo310.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class KillMessageS2CPacket {
    public static BiConsumer<String, UUID> CLIENT_HANDLER = (victimName, victimUUID) -> {};

    private final String victimName;
    private final UUID victimUUID;

    public KillMessageS2CPacket(String victimName, UUID victimUUID) {
        this.victimName = victimName;
        this.victimUUID = victimUUID;
    }

    public static void encode(KillMessageS2CPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.victimName, 64);
        buf.writeUUID(msg.victimUUID);
    }

    public static KillMessageS2CPacket decode(FriendlyByteBuf buf) {
        return new KillMessageS2CPacket(buf.readUtf(64), buf.readUUID());
    }

    public static void handle(KillMessageS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> CLIENT_HANDLER.accept(msg.victimName, msg.victimUUID));
        ctx.get().setPacketHandled(true);
    }
}
