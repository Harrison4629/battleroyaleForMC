package net.harrison.battleroyale.networking.packet;

import net.harrison.battleroyale.util.PhaseData;
import net.harrison.battleroyale.util.PhaseTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;


public class StopPhasingC2SPacket {
    
    private static final Map<UUID, Boolean> KEY_PRESSED_MAP = new HashMap<>();
    
    // 构造函数 - 无需参数
    public StopPhasingC2SPacket() {

    }

    public StopPhasingC2SPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }
    
    /**
     * 检查指定玩家是否按下了按键
     * @param playerId 玩家UUID
     * @return 是否按下按键
     */
    public static boolean isKeyPressed(UUID playerId) {
        return KEY_PRESSED_MAP.getOrDefault(playerId, false);
    }
    
    /**
     * 设置玩家按键状态
     * @param playerId 玩家UUID
     * @param pressed 按键状态
     */
    public static void setKeyPressed(UUID playerId, boolean pressed) {
        KEY_PRESSED_MAP.put(playerId, pressed);
    }
    
    /**
     * 重置玩家的按键状态
     * @param playerId 玩家UUID
     */
    public static void resetKeyPressed(UUID playerId) {
        KEY_PRESSED_MAP.remove(playerId);
    }
    
    // 处理 - 在接收端处理数据包
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(()->{
            ServerPlayer player = context.getSender();
            if (player != null) {
                UUID playerId = player.getUUID();
                if (PhaseTracker.isPhasing(playerId)) {
                    setKeyPressed(playerId, true);
                } else {
                    resetKeyPressed(playerId);
                }
            }
        });
        supplier.get().setPacketHandled(true);
    }

}