package net.harrison.battleroyale.event;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.util.PhaseData;
import net.harrison.battleroyale.util.PhaseTracker;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID)
public class PhaseEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            Player player = event.player;
            UUID playerId = player.getUUID();
            
            // 检查玩家是否在位移
            if (PhaseTracker.isPhasing(playerId)) {
                PhaseData phaseData = PhaseTracker.getPhaseData(playerId);
                
                // 如果位移数据已完成，将玩家传送回原位
                if (phaseData.isFinished(player.tickCount)) {
                    // 传送回原位
                    teleportBack(player, phaseData);
                    // 移除位移数据
                    PhaseTracker.stopPhasing(playerId);
                } else {
                    // 平滑移动玩家
                    movePlayer(player, phaseData);

                    // 粒子效果
                    if (player.level.isClientSide && player.tickCount % 2 == 0) {
                        spawnPhaseParticles(player);
                    }
                }
            }
        }
    }

    /**
     * 平滑移动玩家
     */
    private static void movePlayer(Player player, PhaseData phaseData) {
        Vec3 direction = phaseData.getDirection();
        float speed = phaseData.getMoveSpeed();

        // 计算移动向量
        Vec3 movement = direction.multiply(speed, speed, speed);

        // 移动玩家
        player.setDeltaMovement(movement);
    }

    /**
     * 传送玩家回原位
     */
    private static void teleportBack(Player player, PhaseData phaseData) {
        Vec3 originalPos = phaseData.getOriginalPosition();

        // 传送玩家
        player.teleportTo(originalPos.x, originalPos.y, originalPos.z);
        player.setDeltaMovement(Vec3.ZERO); // 停止所有移动

        // 播放传送音效
        if (!player.level.isClientSide) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

            // 显示提示信息
            player.displayClientMessage(
                    net.minecraft.network.chat.Component.translatable("item.battleroyale.phase_core.trace_back"),
                    true);
        }
        
        // 粒子效果
        if (player.level.isClientSide) {
            for (int i = 0; i < 32; i++) {
                player.level.addParticle(
                        ParticleTypes.PORTAL,
                        player.getX() + (player.level.random.nextDouble() - 0.5) * 2,
                        player.getY() + player.level.random.nextDouble() * 2,
                        player.getZ() + (player.level.random.nextDouble() - 0.5) * 2,
                        0, 0.1, 0);
            }
        }
    }
    
    /**
     * 生成位移时的粒子效果
     */
    private static void spawnPhaseParticles(Player player) {
        for (int i = 0; i < 2; i++) {
            player.level.addParticle(
                    ParticleTypes.PORTAL,
                    player.getX() + (player.level.random.nextDouble() - 0.5) * 0.5,
                    player.getY() + 0.5 + player.level.random.nextDouble() * 0.5,
                    player.getZ() + (player.level.random.nextDouble() - 0.5) * 0.5,
                    0, 0.1, 0);
        }
    }
}
