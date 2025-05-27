package net.harrison.battleroyale.networking.packet;

import net.harrison.battleroyale.util.PhaseTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 客户端到服务器的数据包：玩家请求停止位移
 * 当玩家按下停止位移按键时，客户端发送此数据包到服务器
 * 服务器接收后记录玩家的按键状态，PhaseTracker将据此终止位移
 */
public class StopPhasingC2SPacket {
    
    /**
     * 记录哪些玩家按下了停止位移的按键
     */
    private static final Map<UUID, Boolean> KEY_PRESSED_MAP = new HashMap<>();
    
    /**
     * 默认构造函数
     * 创建一个空的数据包实例
     */
    public StopPhasingC2SPacket() {
    }

    /**
     * 从网络缓冲区读取数据包
     * @param buf 网络缓冲区
     */
    public StopPhasingC2SPacket(FriendlyByteBuf buf) {
        // 无数据需要读取
    }

    /**
     * 将数据包写入网络缓冲区
     * @param buf 网络缓冲区
     */
    public void toBytes(FriendlyByteBuf buf) {
        // 无数据需要写入
    }
    
    /**
     * 检查指定玩家是否按下了停止位移按键
     * @param playerId 玩家UUID
     * @return 是否按下按键
     */
    public static boolean isKeyPressed(UUID playerId) {
        return KEY_PRESSED_MAP.getOrDefault(playerId, false);
    }

    /**
     * 重置玩家的按键状态
     * 当位移结束后调用此方法清理状态
     * @param playerId 玩家UUID
     */
    public static void resetKeyPressed(UUID playerId) {
        KEY_PRESSED_MAP.remove(playerId);
    }
    
    /**
     * 处理接收到的数据包
     * 记录玩家的按键状态，用于中断位移过程
     * @param supplier 网络事件上下文供应商
     */
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // 获取发送数据包的玩家
            ServerPlayer player = context.getSender();
            if (player != null) {
                UUID playerId = player.getUUID();
                // 仅当玩家正在位移时记录按键状态
                if (PhaseTracker.isPhasing(playerId)) {
                    KEY_PRESSED_MAP.put(playerId, true);
                } else {
                    // 如果玩家不在位移状态，清除按键状态
                    resetKeyPressed(playerId);
                }
            }
        });
        context.setPacketHandled(true);
    }
}