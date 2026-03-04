package com.dousiyo.meatwo310.network;

import com.dousiyo.meatwo310.Meatwo310;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private ModNetwork() {}

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(Meatwo310.MODID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();

    private static int id = 0;

    public static void register() {
        CHANNEL.messageBuilder(BlinkTeleportC2SPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(BlinkTeleportC2SPacket::encode)
                .decoder(BlinkTeleportC2SPacket::decode)
                .consumerMainThread(BlinkTeleportC2SPacket::handle)
                .add();

        CHANNEL.messageBuilder(KillMessageS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(KillMessageS2CPacket::encode)
                .decoder(KillMessageS2CPacket::decode)
                .consumerMainThread(KillMessageS2CPacket::handle)
                .add();

        CHANNEL.messageBuilder(TimerHudUpdateS2CPacket.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(TimerHudUpdateS2CPacket::encode)
                .decoder(TimerHudUpdateS2CPacket::decode)
                .consumerMainThread(TimerHudUpdateS2CPacket::handle)
                .add();
    }
}
