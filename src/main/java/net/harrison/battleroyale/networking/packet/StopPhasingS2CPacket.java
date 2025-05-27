package net.harrison.battleroyale.networking.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务器到客户端的数据包：位移结束时触发粒子效果
 * 当玩家的位移结束并返回原始位置时，服务器发送此数据包到客户端
 * 客户端接收后在玩家位置生成传送门粒子效果
 */
public class StopPhasingS2CPacket {
    
    /**
     * 默认构造函数
     * 创建一个新的空数据包实例
     */
    public StopPhasingS2CPacket() {
    }

    /**
     * 从网络缓冲区读取数据包
     * @param buf 网络缓冲区
     */
    public StopPhasingS2CPacket(FriendlyByteBuf buf) {
        // 这个数据包不包含任何数据
    }

    /**
     * 将数据包写入网络缓冲区
     * @param buf 网络缓冲区
     */
    public void toBytes(FriendlyByteBuf buf) {
        // 这个数据包不包含任何数据
    }

    /**
     * 处理接收到的数据包
     * 确保只在客户端执行相关代码
     * @param supplier 网络事件上下文供应商
     */
    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        
        // 确保这个包只在客户端处理
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.enqueueWork(() -> {
                // 使用安全的方式调用客户端代码
                DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientHandler::handlePacket);
            });
        }
        
        context.setPacketHandled(true);
    }
    
    /**
     * 客户端处理器
     * 使用静态内部类完全隔离客户端代码，避免在服务器加载
     */
    public static class ClientHandler {
        /**
         * 处理数据包的客户端方法
         */
        public static void handlePacket() {
            // 这个方法在客户端上调用
            // 在客户端上导入和使用Minecraft类
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientHandler::createParticles);
        }
        
        /**
         * 创建传送门粒子效果
         * 仅在客户端执行
         */
        @OnlyIn(Dist.CLIENT)
        private static void createParticles() {
            // 从Minecraft获取客户端玩家
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            
            if (player != null) {
                // 在客户端生成传送门粒子效果
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
