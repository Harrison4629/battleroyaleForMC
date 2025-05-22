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
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 大逃杀缩圈系统管理类
 * 负责处理缩圈逻辑，包括倒计时、更新世界边界和计分板显示
 * 极简版 - 仅支持单世界运行，无多世界支持
 */
public class ZoneManager {
    private static final Logger LOGGER = Logger.getLogger("BattleRoyale");
    
    // 静态全局状态
    private static int currentZoneStage = 0;
    private static ScheduledExecutorService scheduler = null;
    private static boolean isRunning = false;
    private static double zoneCenterX = 0;
    private static double zoneCenterZ = 0;
    private static ServerLevel currentLevel = null;
    
    // 当前活动的任务
    private static ScheduledFuture<?> countdownTask = null;
    private static ScheduledFuture<?> shrinkStartTask = null;
    private static ScheduledFuture<?> shrinkUpdateTask = null;
    private static ScheduledFuture<?> delayTask = null;
    
    // 计分板本地缓存，避免频繁查询服务器
    private static int cachedShrinkInValue = 0;
    private static int cachedShrinkingValue = 0;
    
    // 计分板条目名称常量
    private static final String SHRINK_IN_ENTRY = "shrink_in";
    private static final String SHRINKING_ENTRY = "shrinking";
    private static final String SCOREBOARD_OBJECTIVE = "zone";
    
    /**
     * 安全执行服务器任务
     * @param server Minecraft服务器实例
     * @param task 要执行的任务
     */
    private static void safeSubmit(MinecraftServer server, Runnable task) {
        try {
            server.submit(task);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "提交任务到服务器时出错", e);
        }
    }
    
    /**
     * 安全执行命令
     * @param server Minecraft服务器实例
     * @param command 要执行的命令
     */
    private static void safeExecuteCommand(MinecraftServer server, String command) {
        try {
            server.getCommands().performPrefixedCommand(
                server.createCommandSourceStack().withSuppressedOutput(),
                command
            );
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "执行命令时出错: " + command, e);
        }
    }
    
    /**
     * 设置计分板值
     * @param server Minecraft服务器实例
     * @param entry 计分板条目
     * @param value 要设置的值
     */
    private static void setScoreboardValue(MinecraftServer server, String entry, int value) {
        safeExecuteCommand(server, "scoreboard players set " + entry + " " + SCOREBOARD_OBJECTIVE + " " + value);
    }
    
    /**
     * 重置计分板值
     * @param server Minecraft服务器实例
     * @param entry 计分板条目
     */
    private static void resetScoreboardValue(MinecraftServer server, String entry) {
        safeExecuteCommand(server, "scoreboard players reset " + entry + " " + SCOREBOARD_OBJECTIVE);
    }
    
    /**
     * 初始化调度器
     */
    private static void initScheduler() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newScheduledThreadPool(1, r -> {
                Thread t = new Thread(r, "BattleRoyale-Zone");
                t.setDaemon(true);
                return t;
            });
        }
    }
    
    /**
     * 取消所有任务并关闭调度器
     */
    private static void cancelAllTasksAndShutdown() {
        // 取消所有运行中的任务
        cancelTask(countdownTask);
        cancelTask(shrinkStartTask);
        cancelTask(shrinkUpdateTask);
        cancelTask(delayTask);
        
        // 重置任务引用
        countdownTask = null;
        shrinkStartTask = null;
        shrinkUpdateTask = null;
        delayTask = null;
        
        // 关闭调度器
        if (scheduler != null && !scheduler.isShutdown()) {
            try {
                scheduler.shutdown();
                if (!scheduler.awaitTermination(1, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
            scheduler = null;
        }
    }
    
    /**
     * 取消单个任务
     * @param task 要取消的任务
     */
    private static void cancelTask(ScheduledFuture<?> task) {
        if (task != null && !task.isDone() && !task.isCancelled()) {
            task.cancel(false);
        }
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
        
        // 如果已经在运行，先停止
        if (isRunning) {
            stopShrinking(source);
        }
        
        // 保存当前世界引用
        currentLevel = level;
        
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

        // 设置世界边界和初始计分板
        safeSubmit(server, () -> {
            level.getWorldBorder().setCenter(center.x, center.z);
            level.getWorldBorder().setSize(currentSize);
            
            // 设置计分板显示倒计时
            setScoreboardValue(server, SHRINK_IN_ENTRY, warningTime);
            
            // 初始化缓存值
            cachedShrinkInValue = warningTime;
        });
    
        // 初始化调度器
        initScheduler();
        isRunning = true;
    
        // 创建倒计时更新任务
        createCountdownTask(server);
        
        // 创建缩圈启动任务
        createShrinkStartTask(server, level, stage, currentSize, nextSize, shrinkTime, warningTime);
    }
    
    /**
     * 创建倒计时任务
     */
    private static void createCountdownTask(MinecraftServer server) {
        countdownTask = scheduler.scheduleAtFixedRate(() -> {
            try {
                // 本地递减计数，减少服务器交互
                if (cachedShrinkInValue > 0) {
                    cachedShrinkInValue--;
                    
                    // 每秒更新计分板
                    safeSubmit(server, () -> {
                        setScoreboardValue(server, SHRINK_IN_ENTRY, cachedShrinkInValue);
                    });
                } else {
                    cancelTask(countdownTask);
                    countdownTask = null;
                    safeSubmit(server, () -> {
                        resetScoreboardValue(server, SHRINK_IN_ENTRY);
                    });
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "倒计时任务出错", e);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
    
    /**
     * 创建缩圈启动任务
     */
    private static void createShrinkStartTask(
            MinecraftServer server, 
            ServerLevel level, 
            int stage,
            int currentSize, 
            int nextSize, 
            int shrinkTime, 
            int delaySeconds) {
            
        shrinkStartTask = scheduler.schedule(() -> {
            try {
                // 启动缩圈
                safeSubmit(server, () -> {
                    // 设置worldborder缩小
                    level.getWorldBorder().lerpSizeBetween(currentSize, nextSize, shrinkTime * 1000L);
                    
                    // 设置计分板显示缩圈持续时间
                    setScoreboardValue(server, SHRINKING_ENTRY, shrinkTime);
                    
                    // 初始化缓存值
                    cachedShrinkingValue = shrinkTime;
                });
    
                // 创建缩圈进行中的更新任务
                createShrinkingUpdateTask(server, level, stage);
                
                // 清理当前任务引用
                shrinkStartTask = null;
                
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "缩圈启动任务出错", e);
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }
    
    /**
     * 创建缩圈过程中的更新任务
     */
    private static void createShrinkingUpdateTask(
            MinecraftServer server, 
            ServerLevel level, 
            int stage) {
            
        shrinkUpdateTask = scheduler.scheduleAtFixedRate(() -> {
            try {
                // 本地递减计数，减少服务器交互
                if (cachedShrinkingValue > 0) {
                    cachedShrinkingValue--;
                    
                    // 每秒更新计分板
                    safeSubmit(server, () -> {
                        setScoreboardValue(server, SHRINKING_ENTRY, cachedShrinkingValue);
                    });
                } else {
                    cancelTask(shrinkUpdateTask);
                    shrinkUpdateTask = null;
                    safeSubmit(server, () -> {
                        resetScoreboardValue(server, SHRINKING_ENTRY);
                        
                        // 缩圈结束后的处理
                        handleShrinkingComplete(server, level, stage);
                    });
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "缩圈更新任务出错", e);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }
    
    /**
     * 处理缩圈完成后的操作
     * @param server Minecraft服务器实例
     * @param level 服务器世界
     * @param stage 当前缩圈阶段
     */
    private static void handleShrinkingComplete(MinecraftServer server, ServerLevel level, int stage) {
        int nextStage = stage + 1;
        
        // 检查是否还有下一阶段
        if (nextStage < 6) {
            Random random = new Random();
            int randomDelay = random.nextInt(11) + 10;
            
            // 创建延迟任务
            delayTask = scheduler.schedule(() -> {
                // 如果已被停止，不继续下一阶段
                if (!isRunning) {
                    return;
                }
                
                // 清理引用
                delayTask = null;
                
                // 递归调用启动下一阶段
                safeSubmit(server, () -> {
                    CommandSourceStack fakeSource = createFakeCommandSource(server, level);
                    startShrinking(fakeSource, nextStage);
                });
                
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
     * 停止当前的缩圈系统
     * @param source 命令源
     */
    public static void stopShrinking(CommandSourceStack source) {
        if (!isRunning) {
            source.sendFailure(Component.literal("§c缩圈系统未在运行！"));
            return;
        }

        // 取消所有任务并关闭调度器
        cancelAllTasksAndShutdown();
        isRunning = false;
        
        // 清除计分板
        safeSubmit(source.getServer(), () -> {
            resetScoreboardValue(source.getServer(), SHRINK_IN_ENTRY);
            resetScoreboardValue(source.getServer(), SHRINKING_ENTRY);
        });
    }
    
    /**
     * 清理所有资源
     * 应在服务器关闭时调用
     */
    public static void cleanupAllWorlds() {
        LOGGER.info("正在清理缩圈系统资源...");
        cancelAllTasksAndShutdown();
        isRunning = false;
        currentLevel = null;
    }
}