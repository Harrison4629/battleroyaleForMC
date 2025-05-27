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
 * 服务器到客户端的数据包：位移持续期间的效果
 * 在玩家位移过程中定期发送，用于生成轻微的粒子效果
 * 显示玩家正在进行位移的视觉提示
 */
public class PhasingDurationS2CPacket {

    /**
     * 默认构造函数
     */
    public PhasingDurationS2CPacket() {
    }

    /**
     * 从网络缓冲区读取数据包
     * @param buf 网络缓冲区
     */
    public PhasingDurationS2CPacket(FriendlyByteBuf buf) {
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
     * 处理接收到的数据包
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
         * 处理数据包的客户端入口方法
         */
        public static void handlePacket() {
            // 安全地调用客户端特定代码
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ClientHandler::spawnPhaseParticles);
        }

        /**
         * 生成位移过程中的粒子效果
         * 仅在客户端执行
         */
        @OnlyIn(Dist.CLIENT)
        private static void spawnPhaseParticles() {
            // 从Minecraft获取客户端玩家
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;

            if (player != null) {
                // 在客户端生成少量粒子效果，表示位移正在进行
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
    }
}
