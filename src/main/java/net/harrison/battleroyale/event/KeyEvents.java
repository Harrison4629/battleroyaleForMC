package net.harrison.battleroyale.event;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.networking.packet.StopPhasingPacket;
import net.harrison.battleroyale.util.KeyBinding;
import net.harrison.battleroyale.util.PhaseData;
import net.harrison.battleroyale.util.PhaseTracker;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

public class KeyEvents {
    /**
     * 传送玩家回原位
     */
    private static void teleportBack(Player player, PhaseData phaseData) {
        Vec3 originalPos = phaseData.getOriginalPosition();
        System.out.println("Teleporting to: " + originalPos.toString() + " from: " + player.position());

        // 传送玩家
        player.setPos(originalPos.x, originalPos.y+1, originalPos.z);

        // 播放传送音效
        if (!player.level.isClientSide) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false));

            // 显示提示信息
            player.displayClientMessage(
                    Component.translatable("item.battleroyale.phase_core.trace_back"),
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

    @Mod.EventBusSubscriber(modid = Battleroyale.MODID, value = Dist.CLIENT)
    public static class KeyBindingEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.STOP_PHASING_KEY);

        }


        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if(KeyBinding.STOP_PHASING_KEY.consumeClick()) {
                // 获取客户端玩家
                net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
                Player player = minecraft.player;
                
                if (player != null) {
                    UUID playerId = player.getUUID();
                    // 如果玩家正在位移，发送网络包到服务器
                    if (PhaseTracker.isPhasing(playerId)) {
                        // 发送网络数据包到服务器
                        net.harrison.battleroyale.networking.ModMessages.sendToServer(new StopPhasingPacket());
                        
                        // 客户端显示粒子效果
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
                }
            }
        }
    }



}
