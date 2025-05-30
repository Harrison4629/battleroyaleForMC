package net.harrison.battleroyale.util;

import net.harrison.battleroyale.networking.packet.StopPhasingC2SPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
     * 批量更新所有正在位移的玩家 - 服务端调用
     * @param level 服务器世界
     */
    public static void updateAllPhasingPlayers(ServerLevel level) {
        if (level == null || PHASING_PLAYERS.isEmpty()) return;

        // 创建一个副本以避免并发修改问题
        Map<UUID, PhaseData> phasingPlayersCopy = new HashMap<>(PHASING_PLAYERS);

        for (UUID playerId : phasingPlayersCopy.keySet()) {
            ServerPlayer player = level.getServer().getPlayerList().getPlayer(playerId);
            if (player != null && player.level == level) {
                updatePhasing(player);
            }
        }
    }
    /**
     * 更新玩家位移 - 在服务端执行，应用位移效果
     * @param player 需要更新的玩家
     */
    public static void updatePhasing(ServerPlayer player) {
        if (player == null) return;
        
        UUID playerId = player.getUUID();
        if (!isPhasing(playerId)) return;
        
        PhaseData phaseData = getPhaseData(playerId);
        if (phaseData == null) return;
        
        // 检查位移是否已结束或按下按键
        if (phaseData.isFinished(player.tickCount) || StopPhasingC2SPacket.isKeyPressed(playerId)) {
    
            Vec3 originalPos = phaseData.getOriginalPosition();


            for (int i = 0; i < 32; i++) {
                player.level.addParticle(
                        ParticleTypes.PORTAL,
                        player.getX() + (player.level.random.nextDouble() - 0.5) * 2,
                        player.getY() + player.level.random.nextDouble() * 2,
                        player.getZ() + (player.level.random.nextDouble() - 0.5) * 2,
                        0, 0.1, 0);
            }

            // 位移结束，返回原始位置
            player.moveTo(originalPos.x, originalPos.y, originalPos.z);

            player.displayClientMessage(Component.translatable("item.battleroyale.phase_core.trace_back")
                    .withStyle(ChatFormatting.BLUE), true);


            player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

            stopPhasing(playerId);
            // 重置按键状态
            StopPhasingC2SPacket.resetKeyPressed(playerId);

        } else {

            // 按照方向和速度移动玩家
            Vec3 direction = phaseData.getDirection();
            float speed = phaseData.getMoveSpeed();

            Vec3 movement = direction.scale(speed);

            player.teleportTo(player.getX() + movement.x, player.getY() + movement.y, player.getZ() + movement.z);
        }
    }
    /**
     * 玩家的位移开始前的获取原始位置
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
}
