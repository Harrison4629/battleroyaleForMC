package net.harrison.battleroyale.networking;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.networking.packet.PhasingDurationS2CPacket;
import net.harrison.battleroyale.networking.packet.StopPhasingC2SPacket;
import net.harrison.battleroyale.networking.packet.StopPhasingS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTANCE;
    
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }
    
    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(ResourceLocation.fromNamespaceAndPath(Battleroyale.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();


        INSTANCE = net;
        net.messageBuilder(StopPhasingC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(StopPhasingC2SPacket::new)
                .encoder(StopPhasingC2SPacket::toBytes)
                .consumerMainThread(StopPhasingC2SPacket::handle)
                .add();
        net.messageBuilder(StopPhasingS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StopPhasingS2CPacket::new)
                .encoder(StopPhasingS2CPacket::toBytes)
                .consumerMainThread(StopPhasingS2CPacket::handle)
                .add();
        net.messageBuilder(PhasingDurationS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PhasingDurationS2CPacket::new)
                .encoder(PhasingDurationS2CPacket::toBytes)
                .consumerMainThread(PhasingDurationS2CPacket::handle)
                .add();
    }
    
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
    
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
