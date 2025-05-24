package net.harrison.battleroyale.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 追踪玩家的位移状态
 */
public class PhaseTracker {
    private static final Map<UUID, PhaseData> PHASING_PLAYERS = new HashMap<>();
    
    /**
     * 开始玩家的位移
     * @param player 玩家
     * @param duration 持续时间（tick）
     * @param moveSpeed 移动速度
     */
    public static void startPhasing(Player player, int duration, float moveSpeed) {
        if (player == null) return;
        
        UUID playerId = player.getUUID();
        // 记录当前位置和方向
        Vec3 position = player.position();
        Vec3 direction = player.getViewVector(1.0F).normalize();
        
        // 创建位移数据并存储
        PhaseData phaseData = new PhaseData(
                position,
                direction,
                player.tickCount,
                duration,
                moveSpeed
        );
        
        PHASING_PLAYERS.put(playerId, phaseData);
    }
    
    /**
     * 获取玩家的位移数据
     * @param playerId 玩家ID
     * @return 位移数据，如果不存在则返回null
     */
    public static PhaseData getPhaseData(UUID playerId) {
        return PHASING_PLAYERS.get(playerId);
    }
    
    /**
     * 停止玩家的位移
     * @param playerId 玩家ID
     */
    public static void stopPhasing(UUID playerId) {
        PHASING_PLAYERS.remove(playerId);
    }
    
    /**
     * 检查玩家是否正在位移
     * @param playerId 玩家ID
     * @return 是否正在位移
     */
    public static boolean isPhasing(UUID playerId) {
        return PHASING_PLAYERS.containsKey(playerId);
    }
    
    /**
     * 清理所有过期的位移数据
     * @param currentTime 当前时间
     */
    public static void cleanupExpiredPhasing(int currentTime) {
        PHASING_PLAYERS.entrySet().removeIf(entry -> 
                entry.getValue().isFinished(currentTime));
    }
}
