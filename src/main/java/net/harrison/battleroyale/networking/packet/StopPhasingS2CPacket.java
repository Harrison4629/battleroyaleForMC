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

public class StopPhasingS2CPacket {

    public StopPhasingS2CPacket() {
    }

    public StopPhasingS2CPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

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
    
    // 使用静态内部类完全隔离客户端代码
    public static class ClientHandler {
        public static void handlePacket() {
            // 这个方法在客户端上调用
            // 在客户端上导入和使用Minecraft类
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () ->
                    ClientHandler::createParticles);
        }
        
        /**
         * 这个方法将被移动到单独的客户端处理类中
         */
        @OnlyIn(Dist.CLIENT)
        private static void createParticles() {
            // 从Minecraft获取客户端玩家
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            
            if (player != null) {
                // 在客户端生成粒子效果
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
