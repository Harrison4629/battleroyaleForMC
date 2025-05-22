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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
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
        
        // 计分板本地缓存，避免频繁查询服务器
        int cachedShrinkInValue = 0;
        int cachedShrinkingValue = 0;

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
                
                // 关闭调度器 - 使用优化的关闭方式
                try {
                    scheduler.shutdown();
                    // 短等待时间，避免长时间阻塞
                    if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
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
         * 确保调度器已初始化 - 使用优化的线程池配置
         */
        public void ensureSchedulerInitialized() {
            if (scheduler == null || scheduler.isShutdown()) {
                // 创建适合该世界的线程池，只需要2个线程即可满足需求
                scheduler = Executors.newScheduledThreadPool(2, r -> {
                    Thread t = new Thread(r, "BattleRoyale-Zone-" + worldId);
                    t.setDaemon(true); // 使用守护线程，避免阻止服务器关闭
                    return t;
                });
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
     * 获取计分板条目名称
     * @param baseName 基本名称
     * @return 计分板条目名称
     */
    private static String getScoreboardEntry(String baseName) {
        return baseName;
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
            stopWorldShrinking(state, server, level);
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
        
        // 获取计分板条目名称
        String shrinkInEntry = getScoreboardEntry("shrink_in");
        String shrinkingEntry = getScoreboardEntry("shrinking");

        try {
            // 设置worldborder初始大小和位置 - 使用异步任务队列而非直接阻塞
            server.submit(() -> {
                try {
                    level.getWorldBorder().setCenter(center.x, center.z);
                    level.getWorldBorder().setSize(currentSize);
    
                    // 设置计分板显示倒计时
                    server.getCommands().performPrefixedCommand(
                        server.createCommandSourceStack().withSuppressedOutput(),
                        "scoreboard players set " + shrinkInEntry + " zone " + warningTime
                    );
                    
                    // 初始化缓存值
                    state.cachedShrinkInValue = warningTime;
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "设置世界边界时出错", e);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "提交任务到服务器时出错", e);
            return;
        }

        // 确保调度器已初始化
        state.ensureSchedulerInitialized();
        state.isRunning = true;

        // 倒计时更新任务 - 每秒更新一次
        ScheduledFuture<?> countTask = state.scheduler.scheduleAtFixedRate(() -> {
            try {
                // 本地递减计数，减少服务器交互
                if (state.cachedShrinkInValue > 0) {
                    state.cachedShrinkInValue--;
                    
                    // 每秒都更新计分板，确保实时性
                    server.submit(() -> {
                        try {
                            server.getCommands().performPrefixedCommand(
                                server.createCommandSourceStack().withSuppressedOutput(),
                                "scoreboard players set " + shrinkInEntry + " zone " + state.cachedShrinkInValue
                            );
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "更新计分板时出错", e);
                        }
                    });
                } else {
                    state.cancelTask("countTask");
                    server.submit(() -> {
                        try {
                            server.getCommands().performPrefixedCommand(
                                server.createCommandSourceStack().withSuppressedOutput(),
                                "scoreboard players reset " + shrinkInEntry + " zone"
                            );
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "重置计分板时出错", e);
                        }
                    });
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "倒计时任务出错", e);
            }
        }, 1, 1, TimeUnit.SECONDS);
        state.addTask("countTask", countTask);

        // 缩圈开始任务
        ScheduledFuture<?> shrinkStartTask = state.scheduler.schedule(() -> {
            try {
                server.submit(() -> {
                    try {
                        // 设置worldborder缩小
                        level.getWorldBorder().lerpSizeBetween(currentSize, nextSize, shrinkTime * 1000L);
    
                        // 设置计分板显示缩圈持续时间
                        server.getCommands().performPrefixedCommand(
                            server.createCommandSourceStack().withSuppressedOutput(),
                            "scoreboard players set " + shrinkingEntry + " zone " + shrinkTime
                        );
                        
                        // 初始化缓存值
                        state.cachedShrinkingValue = shrinkTime;
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "开始缩圈时出错", e);
                    }
                });

                // 缩圈期间计时更新 - 减少更新频率
                ScheduledFuture<?> shrinkUpdateTask = state.scheduler.scheduleAtFixedRate(() -> {
                    try {
                        // 本地递减计数，减少服务器交互
                        if (state.cachedShrinkingValue > 0) {
                            state.cachedShrinkingValue--;
                            
                            // 每秒都更新计分板，确保实时性
                            server.submit(() -> {
                                try {
                                    server.getCommands().performPrefixedCommand(
                                        server.createCommandSourceStack().withSuppressedOutput(),
                                        "scoreboard players set " + shrinkingEntry + " zone " + state.cachedShrinkingValue
                                    );
                                } catch (Exception e) {
                                    LOGGER.log(Level.WARNING, "更新缩圈计分板时出错", e);
                                }
                            });
                        } else {
                            state.cancelTask("shrinkUpdateTask");
                            server.submit(() -> {
                                try {
                                    server.getCommands().performPrefixedCommand(
                                        server.createCommandSourceStack().withSuppressedOutput(),
                                        "scoreboard players reset " + shrinkingEntry + " zone"
                                    );
                                    
                                    // 缩圈结束后的处理
                                    handleShrinkingComplete(server, level, stage);
                                } catch (Exception e) {
                                    LOGGER.log(Level.SEVERE, "缩圈结束处理时出错", e);
                                }
                            });
                        }
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "缩圈更新任务出错", e);
                    }
                }, 1, 1, TimeUnit.SECONDS);
                state.addTask("shrinkUpdateTask", shrinkUpdateTask);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "缩圈启动任务出错", e);
            }
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
        
        // 检查是否还有下一阶段
        if (nextStage < 6) {
            Random random = new Random();
            int randomDelay = random.nextInt(11) + 10;
            
            // 创建延迟任务
            ScheduledFuture<?> delayTask = state.scheduler.schedule(() -> {
                try {
                    // 如果已被停止，不继续下一阶段
                    if (!state.isRunning) {
                        return;
                    }
                    
                    // 递归调用启动下一阶段
                    server.submit(() -> {
                        try {
                            CommandSourceStack fakeSource = createFakeCommandSource(server, level, state);
                            startShrinking(fakeSource, nextStage);
                        } catch (Exception e) {
                            LOGGER.log(Level.SEVERE, "启动下一阶段缩圈时出错", e);
                        }
                    });
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "延迟任务出错", e);
                }
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
            LOGGER.log(Level.WARNING, "获取计分板值时出错: " + e.getMessage());
            return 0;
        }
    }

    /**
     * 停止指定世界的缩圈系统
     * @param state 世界状态
     * @param server Minecraft服务器实例
     * @param level 服务器世界
     */
    private static void stopWorldShrinking(WorldZoneState state, MinecraftServer server, ServerLevel level) {
        if (!state.isRunning) {
            return;
        }
        
        // 关闭所有调度器任务
        state.shutdown();
        
        // 获取计分板条目名称
        String shrinkInEntry = getScoreboardEntry("shrink_in");
        String shrinkingEntry = getScoreboardEntry("shrinking");
        
        // 清除计分板
        try {
            server.submit(() -> {
                try {
                    server.getCommands().performPrefixedCommand(
                        server.createCommandSourceStack().withSuppressedOutput(),
                        "scoreboard players reset " + shrinkInEntry + " zone"
                    );
                    server.getCommands().performPrefixedCommand(
                        server.createCommandSourceStack().withSuppressedOutput(),
                        "scoreboard players reset " + shrinkingEntry + " zone"
                    );
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "清除计分板时出错", e);
                }
            });
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "提交清除计分板任务时出错", e);
        }
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

        stopWorldShrinking(state, source.getServer(), level);
    }
    
    /**
     * 清理所有世界的资源
     * 应在服务器关闭时调用
     */
    public static void cleanupAllWorlds() {
        LOGGER.info("正在清理所有世界的缩圈系统资源...");
        for (WorldZoneState state : worldStates.values()) {
            state.shutdown();
        }
        worldStates.clear();
    }
}