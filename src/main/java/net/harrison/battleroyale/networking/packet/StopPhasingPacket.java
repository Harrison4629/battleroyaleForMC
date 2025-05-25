package net.harrison.battleroyale.networking.packet;

import net.harrison.battleroyale.util.PhaseData;
import net.harrison.battleroyale.util.PhaseTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;


public class StopPhasingPacket {
    
    // 构造函数 - 无需参数
    public StopPhasingPacket() {}
    
    // 序列化 - 将数据包编码到字节缓冲区
    public void encode(FriendlyByteBuf buf) {
        // 此数据包不需要传递任何数据
    }
    
    // 反序列化 - 从字节缓冲区解码数据包
    public static StopPhasingPacket decode(FriendlyByteBuf buf) {
        return new StopPhasingPacket();
    }
    
    // 处理 - 在接收端处理数据包
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 确保这是在服务器端处理
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                UUID playerId = player.getUUID();
                
                // 检查玩家是否正在位移
                if (PhaseTracker.isPhasing(playerId)) {
                    PhaseData phaseData = PhaseTracker.getPhaseData(playerId);
                    
                    // 传送回原位
                    Vec3 originalPos = phaseData.getOriginalPosition();
                    player.teleportTo(originalPos.x, originalPos.y + 1, originalPos.z);
                    
                    // 播放传送音效
                    player.level.playSound(null, player.getX(), player.getY(), player.getZ(),
                            SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                    
                    // 添加缓降效果
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false));
                    
                    // 显示提示信息
                    player.displayClientMessage(
                            Component.translatable("item.battleroyale.phase_core.trace_back"),
                            true);
                    
                    // 移除位移数据
                    PhaseTracker.stopPhasing(playerId);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
