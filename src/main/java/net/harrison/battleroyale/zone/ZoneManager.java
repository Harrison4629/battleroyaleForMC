package net.harrison.battleroyale.zone;

import net.harrison.battleroyale.config.ZoneConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 大逃杀缩圈系统管理类
 * 负责处理缩圈逻辑，包括倒计时、更新世界边界和计分板显示
 */
public class ZoneManager {
    private static int currentZoneStage = 0;
    private static ScheduledExecutorService countScheduler;
    private static ScheduledExecutorService shrinkScheduler;
    private static boolean isRunning = false;

    /**
     * 启动缩圈系统
     * @param source 命令源
     * @param stage 当前缩圈阶段
     */
    public static void startShrinking(CommandSourceStack source, int stage) {


        if (stage < 1 || stage > 5) {
            source.sendFailure(Component.literal("§c无效的缩圈阶段！请选择1-5的阶段"));
            return;
        }

        MinecraftServer server = source.getServer();
        ServerLevel level = source.getLevel();

        // 直接使用命令执行者的位置作为中心点
        Vec3 center = source.getPosition();
        currentZoneStage = stage;



        // 获取当前圈的参数
        int currentSize = ZoneConfig.getZoneSize(stage);
        int nextSize = ZoneConfig.getZoneSize(stage + 1);
        int warningTime = ZoneConfig.getWarningTime(stage);
        int shrinkTime = ZoneConfig.getShrinkTime(stage);


        // 设置worldborder初始大小和位置
        server.executeBlocking(() -> {
            level.getWorldBorder().setCenter(center.x, center.z);
            level.getWorldBorder().setSize(currentSize);

            // 设置计分板显示倒计时
            server.getCommands().performPrefixedCommand(
                server.createCommandSourceStack().withSuppressedOutput(),
                "scoreboard players set shrink_in zone " + warningTime
            );

            // 广播消息
            server.getPlayerList().broadcastSystemMessage(
                Component.literal("§c警告：毒圈将在 §e" + warningTime + "§c 秒后开始缩小！"),
                false
            );
        });

        // 启动倒计时和缩圈计时器
        countScheduler = Executors.newScheduledThreadPool(1);
        shrinkScheduler = Executors.newScheduledThreadPool(1);
        isRunning = true;

        // 倒计时更新任务
        countScheduler.scheduleAtFixedRate(() -> {
            server.executeBlocking(() -> {
                int currentTime = getScoreboardValue(server, "shrink_in", "zone");
                if (currentTime > 0) {
                    server.getCommands().performPrefixedCommand(
                        server.createCommandSourceStack().withSuppressedOutput(),
                        "scoreboard players remove shrink_in zone 1"
                    );
                } else {
                    countScheduler.shutdown();
                    server.getCommands().performPrefixedCommand(
                            server.createCommandSourceStack().withSuppressedOutput(),
                            "scoreboard players reset shrink_in zone"
                    );
                }
            });
        }, 1, 1, TimeUnit.SECONDS);

        // 缩圈开始任务
        shrinkScheduler.schedule(() -> {
            server.executeBlocking(() -> {
                // 设置worldborder缩小
                level.getWorldBorder().lerpSizeBetween(currentSize, nextSize, shrinkTime * 1000L);

                // 设置计分板显示缩圈持续时间
                server.getCommands().performPrefixedCommand(
                    server.createCommandSourceStack().withSuppressedOutput(),
                    "scoreboard players set shrinking zone " + shrinkTime
                );

                // 缩圈期间计时更新
                shrinkScheduler.scheduleAtFixedRate(() -> {
                    server.executeBlocking(() -> {
                        int currentTime = getScoreboardValue(server, "shrinking", "zone");
                        if (currentTime > 0) {
                            server.getCommands().performPrefixedCommand(
                                server.createCommandSourceStack().withSuppressedOutput(),
                                "scoreboard players remove shrinking zone 1"
                            );
                        } else {
                            shrinkScheduler.shutdown();
                            server.getCommands().performPrefixedCommand(
                                    server.createCommandSourceStack().withSuppressedOutput(),
                                    "scoreboard players reset shrinking zone"
                            );
                        }
                    });
                }, 1, 1, TimeUnit.SECONDS);


            });
        }, warningTime, TimeUnit.SECONDS);



        isRunning = false;
    }


    /**
     * 获取计分板上的值
     * @param server Minecraft服务器实例
     * @param name 计分板名称
     * @param objective 计分板目标
     * @return 计分板上的值
     */
    private static int getScoreboardValue(MinecraftServer server, String name, String objective) {
        try {
            return server.getScoreboard().getOrCreatePlayerScore(name, 
                server.getScoreboard().getObjective(objective)).getScore();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 停止当前的缩圈系统
     * @param source 命令源
     */
    public static void stopShrinking(CommandSourceStack source) {
        if (!isRunning) {
            source.sendFailure(Component.literal("§c缩圈系统未在运行！"));
            return;
        }

        if (countScheduler != null && !countScheduler.isShutdown()) {
            countScheduler.shutdown();
        }
        
        isRunning = false;
        source.sendSuccess(Component.literal("§a缩圈系统已停止！"), true);
    }
}