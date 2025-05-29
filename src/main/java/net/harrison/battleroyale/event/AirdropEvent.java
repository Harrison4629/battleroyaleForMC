package net.harrison.battleroyale.event;

import net.harrison.battleroyale.entities.ModEntities;
import net.harrison.battleroyale.entities.airdrop.AirdropEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 空投系统管理类
 * 负责在游戏中的特定时刻生成空投，提供高级物资给玩家
 */
public class AirdropEvent {
    // 随机数生成器
    private static final Random random = new Random();

    //在安全区内生成随机坐标
    public static Vec3 generateRandomPosition(double centerX, double centerY, double centerZ, int zoneSize) {
        // 计算最大偏移量（半径）
        double maxOffset = zoneSize / 2.0 * 0.8; // 使用80%的区域避免靠近边缘

        // 生成随机偏移
        double offsetX = (random.nextDouble() * 2 - 1) * maxOffset; // -maxOffset到maxOffset之间
        double offsetZ = (random.nextDouble() * 2 - 1) * maxOffset; // -maxOffset到maxOffset之间

        // 计算最终坐标
        double posX = centerX + offsetX;
        double posZ = centerZ + offsetZ;

        return new Vec3(posX, centerY, posZ);
    }

    //在指定位置生成空投
    public static void spawnAirdrop(MinecraftServer server, Vec3 position) {
        if (server == null) return;

        ServerLevel level = server.getLevel(ServerLevel.OVERWORLD);
        if (level == null) return;

        // 获取位置的整数坐标
        int x = (int) Math.round(position.x);
        int y = (int) Math.round(position.y);
        int z = (int) Math.round(position.z);

        // 上方50格生成空投实体
        AirdropEntity airdrop = new AirdropEntity(ModEntities.AIRDROP.get(), level);
        airdrop.setPos(x, y+50, z); // 从高空掉落

        // 设置空投的战利品表
        ResourceLocation lootTableId = new ResourceLocation("battleroyale", "airdrop");
        airdrop.setLootTable(lootTableId, level.random.nextLong());

        // 将实体添加到世界
        level.addFreshEntity(airdrop);

        // 在聊天中广播空投位置
        server.getPlayerList().broadcastSystemMessage(
            Component.literal(String.format("§e§l空投已降落在 x:%d z:%d 附近！", x, z)),
            false
        );
    }
    
    //在当前阶段结束后调度空投生成
    public static void scheduleAirdrop(MinecraftServer server, ScheduledExecutorService scheduler, 
                                      double centerX, double centerY, double centerZ, int zoneSize) {
        // 获取随机位置
        Vec3 airdropPos = generateRandomPosition(centerX, centerY , centerZ, zoneSize);
        
        // 发送预告
        server.getPlayerList().broadcastSystemMessage(
            Component.literal("§e空投正在降落..."),
            false
        );
        
        // 延迟5秒后生成空投
        scheduler.schedule(() -> server.execute(() -> spawnAirdrop(server, airdropPos)), 5, TimeUnit.SECONDS);
    }

    public static void clearAllAirdrops(MinecraftServer server) {
        if (server == null) return;
        
        ServerLevel level = server.getLevel(ServerLevel.OVERWORLD);
        if (level == null) return;

        // 查找并删除所有空投实体
        for (Entity entity : level.getEntities().getAll()) {
            if (entity instanceof AirdropEntity) {
                entity.discard();
            }
        }
    }
}
