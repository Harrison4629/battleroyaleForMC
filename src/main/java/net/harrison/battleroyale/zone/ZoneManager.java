package net.harrison.battleroyale.zone;

import net.harrison.battleroyale.config.ZoneConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * 大逃杀缩圈系统管理类
 * 负责处理缩圈逻辑，包括倒计时、更新世界边界和计分板显示
 */
public class ZoneManager {
    // 每个世界独立的状态管理
    private static final Map<String, WorldZoneState> worldStates = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger("BattleRoyale");

    /**
     * 每个世界的缩圈状态
     */
    private static class WorldZoneState {
        int currentZoneStage = 0;
        ScheduledExecutorService scheduler = null;
        Map<String, ScheduledFuture<?>> scheduledTasks = new HashMap<>();
        boolean isRunning = false;
        double zoneCenterX = 0;
        double zoneCenterZ = 0;
        String worldId;

        public WorldZoneState(String worldId) {
            this.worldId = worldId;
        }

        /**
         * 停止所有定时任务并清理资源
         */
        public void shutdown() {
            if (scheduler != null && !scheduler.isShutdown()) {
                // 取消所有计划的任务
                for (ScheduledFuture<?> task : scheduledTasks.values()) {
                    if (task != null && !task.isDone() && !task.isCancelled()) {
                        task.cancel(true);
                    }
                }
                scheduledTasks.clear();
                
                // 关闭调度器
                try {
                    scheduler.shutdown();
                    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow();
                    Thread.currentThread().interrupt();
                }
                scheduler = null;
            }
            isRunning = false;
        }
        
        /**
         * 确保调度器已初始化
         */
        public void ensureSchedulerInitialized() {
            if (scheduler == null || scheduler.isShutdown()) {
                scheduler = Executors.newScheduledThreadPool(2);
                scheduledTasks.clear();
            }
        }
        
        /**
         * 添加计划任务
         * @param name 任务名称
         * @param future 任务Future
         */
        public void addTask(String name, ScheduledFuture<?> future) {
            // 先取消同名任务
            cancelTask(name);
            scheduledTasks.put(name, future);
        }
        
        /**
         * 取消指定任务
         * @param name 任务名称
         */
        public void cancelTask(String name) {
            ScheduledFuture<?> task = scheduledTasks.get(name);
            if (task != null && !task.isDone() && !task.isCancelled()) {
                task.cancel(true);
            }
            scheduledTasks.remove(name);
        }
    }

    /**
     * 获取世界ID
     * @param level 服务器世界
     * @return 世界ID字符串
     */
    private static String getWorldId(ServerLevel level) {
        return level.dimension().location().toString();
    }
    
    /**
     * 获取或创建世界状态
     * @param level 服务器世界
     * @return 世界缩圈状态对象
     */
    private static WorldZoneState getWorldState(ServerLevel level) {
        String worldId = getWorldId(level);
        return worldStates.computeIfAbsent(worldId, WorldZoneState::new);
    }

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
        
        // 获取世界状态
        WorldZoneState state = getWorldState(level);
        
        // 如果已经在运行，先停止
        if (state.isRunning) {
            stopWorldShrinking(state, server);
        }
        
        // 直接使用命令执行者的位置作为中心点
        Vec3 center = source.getPosition();
        state.currentZoneStage = stage;

        // 保存中心点坐标用于后续阶段
        state.zoneCenterX = center.x;
        state.zoneCenterZ = center.z;

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

        // 确保调度器已初始化
        state.ensureSchedulerInitialized();
        state.isRunning = true;

        // 倒计时更新任务
        ScheduledFuture<?> countTask = state.scheduler.scheduleAtFixedRate(() -> {
            server.executeBlocking(() -> {
                int currentTime = getScoreboardValue(server, "shrink_in", "zone");
                if (currentTime > 0) {
                    server.getCommands().performPrefixedCommand(
                        server.createCommandSourceStack().withSuppressedOutput(),
                        "scoreboard players remove shrink_in zone 1"
                    );
                } else {
                    state.cancelTask("countTask");
                    server.getCommands().performPrefixedCommand(
                        server.createCommandSourceStack().withSuppressedOutput(),
                        "scoreboard players reset shrink_in zone"
                    );
                }
            });
        }, 1, 1, TimeUnit.SECONDS);
        state.addTask("countTask", countTask);

        // 缩圈开始任务
        ScheduledFuture<?> shrinkStartTask = state.scheduler.schedule(() -> {
            server.executeBlocking(() -> {
                // 设置worldborder缩小
                level.getWorldBorder().lerpSizeBetween(currentSize, nextSize, shrinkTime * 1000L);

                // 设置计分板显示缩圈持续时间
                server.getCommands().performPrefixedCommand(
                    server.createCommandSourceStack().withSuppressedOutput(),
                    "scoreboard players set shrinking zone " + shrinkTime
                );

                // 缩圈期间计时更新
                ScheduledFuture<?> shrinkUpdateTask = state.scheduler.scheduleAtFixedRate(() -> {
                    server.executeBlocking(() -> {
                        int currentTime = getScoreboardValue(server, "shrinking", "zone");
                        if (currentTime > 0) {
                            server.getCommands().performPrefixedCommand(
                                server.createCommandSourceStack().withSuppressedOutput(),
                                "scoreboard players remove shrinking zone 1"
                            );
                        } else {
                            state.cancelTask("shrinkUpdateTask");
                            server.getCommands().performPrefixedCommand(
                                server.createCommandSourceStack().withSuppressedOutput(),
                                "scoreboard players reset shrinking zone"
                            );
                            
                            // 缩圈结束后的处理
                            handleShrinkingComplete(server, level, stage);
                        }
                    });
                }, 1, 1, TimeUnit.SECONDS);
                state.addTask("shrinkUpdateTask", shrinkUpdateTask);
            });
        }, warningTime, TimeUnit.SECONDS);
        state.addTask("shrinkStartTask", shrinkStartTask);
    }
    
    /**
     * 处理缩圈完成后的操作
     * @param server Minecraft服务器实例
     * @param level 服务器世界
     * @param stage 当前缩圈阶段
     */
    private static void handleShrinkingComplete(MinecraftServer server, ServerLevel level, int stage) {
        WorldZoneState state = getWorldState(level);
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
            ScheduledFuture<?> delayTask = state.scheduler.schedule(() -> {
                // 如果已被停止，不继续下一阶段
                if (!state.isRunning) {
                    return;
                }
                
                // 递归调用启动下一阶段
                server.executeBlocking(() -> {
                    CommandSourceStack fakeSource = createFakeCommandSource(server, level, state);
                    startShrinking(fakeSource, nextStage);
                });
            }, randomDelay, TimeUnit.SECONDS);
            state.addTask("delayTask", delayTask);
        } else {
            // 所有阶段已完成
            server.getPlayerList().broadcastSystemMessage(
                Component.literal("§6缩圈已全部完成！已达到最终安全区！"),
                false
            );
            state.isRunning = false;
        }
    }
    
    /**
     * 创建一个模拟的命令源，用于递归调用startShrinking
     * @param server Minecraft服务器实例
     * @param level 服务器世界
     * @param state 世界状态
     * @return 命令源对象
     */
    private static CommandSourceStack createFakeCommandSource(MinecraftServer server, ServerLevel level, WorldZoneState state) {
        // 创建一个以保存的中心点为位置的命令源
        return server.createCommandSourceStack()
            .withPosition(new Vec3(state.zoneCenterX, 0, state.zoneCenterZ))
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
     * 停止指定世界的缩圈系统
     * @param state 世界状态
     * @param server Minecraft服务器实例
     */
    private static void stopWorldShrinking(WorldZoneState state, MinecraftServer server) {
        if (!state.isRunning) {
            return;
        }
        
        // 关闭所有调度器任务
        state.shutdown();
        
        // 清除计分板
        server.executeBlocking(() -> {
            server.getCommands().performPrefixedCommand(
                server.createCommandSourceStack().withSuppressedOutput(),
                "scoreboard players reset shrink_in zone"
            );
            server.getCommands().performPrefixedCommand(
                server.createCommandSourceStack().withSuppressedOutput(),
                "scoreboard players reset shrinking zone"
            );
        });
    }
    
    /**
     * 停止当前的缩圈系统
     * @param source 命令源
     */
    public static void stopShrinking(CommandSourceStack source) {
        ServerLevel level = source.getLevel();
        WorldZoneState state = getWorldState(level);
        
        if (!state.isRunning) {
            source.sendFailure(Component.literal("§c缩圈系统未在运行！"));
            return;
        }

        stopWorldShrinking(state, source.getServer());
        //source.sendSuccess(Component.literal("§a缩圈系统已停止！"), true);
    }
    
    /**
     * 清理所有世界的资源
     * 应在服务器关闭时调用
     */
    public static void cleanupAllWorlds() {
        for (WorldZoneState state : worldStates.values()) {
            state.shutdown();
        }
        worldStates.clear();
    }
}