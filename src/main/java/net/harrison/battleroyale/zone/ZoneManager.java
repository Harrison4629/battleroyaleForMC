package net.harrison.battleroyale.zone;

import net.harrison.battleroyale.config.ZoneConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;
import java.util.Random;
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
    private static ScheduledExecutorService scheduler;
    private static boolean isRunning = false;
    private static double zoneCenterX = 0;
    private static double zoneCenterZ = 0;

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

        // 保存中心点坐标用于后续阶段
        zoneCenterX = center.x;
        zoneCenterZ = center.z;

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

        //    // 广播消息
        //    server.getPlayerList().broadcastSystemMessage(
        //        Component.literal("§c警告：毒圈将在 §e" + warningTime + "§c 秒后开始缩小！"),
        //        false
        //    );
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
                            
                            // 缩圈结束后的处理
                            handleShrinkingComplete(server, level, stage);
                        }
                    });
                }, 1, 1, TimeUnit.SECONDS);
                
                
                            });
                        }, warningTime, TimeUnit.SECONDS);
                    }
                    
                    /**
                     * 处理缩圈完成后的操作
                     * @param server Minecraft服务器实例
                     * @param level 服务器世界
                     * @param stage 当前缩圈阶段
                     */
                    private static void handleShrinkingComplete(MinecraftServer server, ServerLevel level, int stage) {
                        int nextStage = stage + 1;
                        
                    //    // 广播缩圈完成消息
                    //    server.getPlayerList().broadcastSystemMessage(
                    //        Component.literal("§a毒圈已稳定至第" + nextStage + "圈！"),
                    //        false
                    //    );
                        
                        // 检查是否还有下一阶段
                        if (nextStage < 6) {
                            Random random = new Random();
                            int randomDelay = random.nextInt(11) + 10;
            //                // 广播下一阶段即将开始
            //                server.getPlayerList().broadcastSystemMessage(
            //    Component.literal("§e10秒后将自动开始第" + nextStage + "圈的缩小！"),
            //    false
            //                );
                            
                            // 创建延迟任务
                            ScheduledExecutorService delayScheduler = Executors.newSingleThreadScheduledExecutor();
                            delayScheduler.schedule(() -> {
                                        // 如果已被停止，不继续下一阶段
                                        if (!isRunning) {
                                            delayScheduler.shutdown();
                                            return;
                                        }
                                        
                                        // 递归调用启动下一阶段
                                        server.executeBlocking(() -> {
                                            CommandSourceStack fakeSource = createFakeCommandSource(server, level);
                                            startShrinking(fakeSource, nextStage);
                                        });
                                        
                                        delayScheduler.shutdown();
                            }, randomDelay, TimeUnit.SECONDS);
                        } else {
                            // 所有阶段已完成
                            server.getPlayerList().broadcastSystemMessage(
                Component.literal("§6缩圈已全部完成！已达到最终安全区！"),
                false
                            );
                            isRunning = false;
                        }
                    }
                    
                    /**
                     * 创建一个模拟的命令源，用于递归调用startShrinking
                     * @param server Minecraft服务器实例
                     * @param level 服务器世界
                     * @return 命令源对象
                     */
                    private static CommandSourceStack createFakeCommandSource(MinecraftServer server, ServerLevel level) {
                        // 创建一个以保存的中心点为位置的命令源
                        return server.createCommandSourceStack()
                            .withPosition(new Vec3(zoneCenterX, 0, zoneCenterZ))
                            .withLevel(level);
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
     * 开始下一阶段的缩圈
     * @param server Minecraft服务器实例
     * @param level 服务器世界
     * @param stage 下一阶段编号
     */
    private static void startNextStage(MinecraftServer server, ServerLevel level, int stage) {


        // 如果不在运行状态，则不启动
        if (!isRunning) {
            return;
        }
        
        // 更新当前阶段
        currentZoneStage = stage;
        
        // 获取当前圈的参数
        int currentSize = ZoneConfig.getZoneSize(stage);
        int nextSize = ZoneConfig.getZoneSize(stage + 1);
        int warningTime = ZoneConfig.getWarningTime(stage);
        int shrinkTime = ZoneConfig.getShrinkTime(stage);
        
        // 设置worldborder初始大小和位置
        server.executeBlocking(() -> {
            // 使用保存的中心点坐标
            level.getWorldBorder().setCenter(zoneCenterX, zoneCenterZ);
            level.getWorldBorder().setSize(currentSize);
            
            // 清除之前的计分板项目
            server.getCommands().performPrefixedCommand(
                server.createCommandSourceStack().withSuppressedOutput(),
                "scoreboard players reset * zone"
            );
            
            // 设置计分板显示倒计时
            server.getCommands().performPrefixedCommand(
                server.createCommandSourceStack().withSuppressedOutput(),
                "scoreboard players set shrink_in zone " + warningTime
            );
    
            // 广播消息
            //server.getPlayerList().broadcastSystemMessage(
            //    Component.literal("§c警告：第" + stage + "圈将在 §e" + warningTime + "§c 秒后开始缩小！"),
            //    false
            //);
        });
    
        // 启动倒计时和缩圈计时器
        scheduler = Executors.newScheduledThreadPool(1);
        
        // 倒计时更新任务
        scheduler.scheduleAtFixedRate(() -> {
            server.executeBlocking(() -> {
                int currentTime = getScoreboardValue(server, "shrink_in", "zone");
                if (currentTime > 0) {
                    server.getCommands().performPrefixedCommand(
                        server.createCommandSourceStack().withSuppressedOutput(),
                        "scoreboard players remove shrink_in zone 1"
                    );
                }
            });
        }, 1, 1, TimeUnit.SECONDS);
    
        // 缩圈开始任务
        scheduler.schedule(() -> {
            server.executeBlocking(() -> {
                // 设置worldborder缩小
                level.getWorldBorder().lerpSizeBetween(currentSize, nextSize, shrinkTime * 1000L);
                
                // 设置计分板显示缩圈持续时间
                server.getCommands().performPrefixedCommand(
                    server.createCommandSourceStack().withSuppressedOutput(),
                    "scoreboard players set shrinking zone " + shrinkTime
                );
                server.getCommands().performPrefixedCommand(
                    server.createCommandSourceStack().withSuppressedOutput(),
                    "scoreboard players reset shrink_in zone"
                );
                
                // 缩圈期间计时更新
                scheduler.scheduleAtFixedRate(() -> {
                    server.executeBlocking(() -> {
                        int currentTime = getScoreboardValue(server, "shrinking", "zone");
                        if (currentTime > 0) {
                            server.getCommands().performPrefixedCommand(
                                server.createCommandSourceStack().withSuppressedOutput(),
                                "scoreboard players remove shrinking zone 1"
                            );
                        }
                    });
                }, 1, 1, TimeUnit.SECONDS);
                
                // 缩圈结束任务
                scheduler.schedule(() -> {
                    server.executeBlocking(() -> {
                        Random random = new Random();
                        int randomDelay = random.nextInt(11) + 10;
                        int nextStage = stage + 1;
                        //server.getPlayerList().broadcastSystemMessage(
                        //    Component.literal("§a毒圈已稳定至第" + nextStage + "圈！"),
                        //    false
                        //);
                        
                        // 关闭当前调度器
                        scheduler.shutdown();
                        
                        // 检查是否还有下一圈
                        if (nextStage < 6) {
                            //server.getPlayerList().broadcastSystemMessage(
                            //    Component.literal("§e10秒后将开始第" + nextStage + "圈的缩小！"),
                            //    false
                            //);
                            
                            // 10秒后自动开始下一阶段
                            ScheduledExecutorService nextStageScheduler = Executors.newSingleThreadScheduledExecutor();
                            nextStageScheduler.schedule(() -> {
                                // 开始下一阶段缩圈
                                startNextStage(server, level, nextStage);
                            }, randomDelay, TimeUnit.SECONDS);
                        } else {
                            //// 最终圈已完成
                            //server.getPlayerList().broadcastSystemMessage(
                            //    Component.literal("§6缩圈已完成！已达到最终安全区！"),
                            //    false
                            //);
                            isRunning = false;
                        }
                    });
                }, shrinkTime, TimeUnit.SECONDS);
            });
        }, warningTime, TimeUnit.SECONDS);
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

        // 关闭所有可能的调度器
        if (countScheduler != null && !countScheduler.isShutdown()) {
            countScheduler.shutdownNow();
        }
        
        if (shrinkScheduler != null && !shrinkScheduler.isShutdown()) {
            shrinkScheduler.shutdownNow();
        }
        
        // 清除计分板
        source.getServer().executeBlocking(() -> {
            source.getServer().getCommands().performPrefixedCommand(
                source.getServer().createCommandSourceStack().withSuppressedOutput(),
                "scoreboard players reset shrink_in zone"
            );
            source.getServer().getCommands().performPrefixedCommand(
                source.getServer().createCommandSourceStack().withSuppressedOutput(),
                "scoreboard players reset shrinking zone"
            );
        });
        
        isRunning = false;
        //source.sendSuccess(Component.literal("§a缩圈系统已停止！"), true);
    }
}