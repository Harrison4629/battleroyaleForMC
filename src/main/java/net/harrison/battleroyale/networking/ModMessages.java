package net.harrison.battleroyale.networking;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.networking.packet.LiftS2CPacket;
import net.harrison.battleroyale.networking.packet.PhasingDurationS2CPacket;
import net.harrison.battleroyale.networking.packet.StopPhasingC2SPacket;
import net.harrison.battleroyale.networking.packet.StopPhasingS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * 处理模组的网络通信
 * 注册所有网络数据包并提供发送方法
 */
public class ModMessages {
    // 网络通道实例
    private static SimpleChannel INSTANCE;
    
    // 网络包ID计数器
    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }
    
    /**
     * 注册所有网络数据包
     * 在模组初始化时调用
     */
    public static void register() {
        // 创建网络通道
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(Battleroyale.MODID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;
        
        // 注册客户端到服务器的数据包
        registerC2SPackets(net);
        
        // 注册服务器到客户端的数据包
        registerS2CPackets(net);
    }
    
    /**
     * 注册客户端到服务器的数据包
     */
    private static void registerC2SPackets(SimpleChannel net) {
        // 停止位移的按键数据包
        net.messageBuilder(StopPhasingC2SPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(StopPhasingC2SPacket::new)
                .encoder(StopPhasingC2SPacket::toBytes)
                .consumerMainThread(StopPhasingC2SPacket::handle)
                .add();
    }
    
    /**
     * 注册服务器到客户端的数据包
     */
    private static void registerS2CPackets(SimpleChannel net) {
        // 停止位移时触发粒子效果的数据包
        net.messageBuilder(StopPhasingS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(StopPhasingS2CPacket::new)
                .encoder(StopPhasingS2CPacket::toBytes)
                .consumerMainThread(StopPhasingS2CPacket::handle)
                .add();
        
        // 位移持续时间的数据包
        net.messageBuilder(PhasingDurationS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PhasingDurationS2CPacket::new)
                .encoder(PhasingDurationS2CPacket::toBytes)
                .consumerMainThread(PhasingDurationS2CPacket::handle)
                .add();

        net.messageBuilder(LiftS2CPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(LiftS2CPacket::new)
                .encoder(LiftS2CPacket::toBytes)
                .consumerMainThread(LiftS2CPacket::handle)
                .add();
    }
    
    /**
     * 发送数据包到服务器
     * @param message 要发送的数据包
     */
    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }
    
    /**
     * 发送数据包到特定玩家
     * @param message 要发送的数据包
     * @param player 目标玩家
     */
    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }
    
    /**
     * 发送数据包到指定维度中的所有玩家
     * @param message 要发送的数据包
     * @param level 目标维度
     */
    public static <MSG> void sendToDimension(MSG message, ServerLevel level) {
        INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), message);
    }
    
    /**
     * 发送数据包到服务器上的所有玩家
     * @param message 要发送的数据包
     */
    public static <MSG> void sendToAll(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
}
