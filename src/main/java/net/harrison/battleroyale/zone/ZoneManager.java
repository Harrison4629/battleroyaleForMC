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

/**
 * 大逃杀缩圈系统管理类
 * 负责处理缩圈逻辑，包括倒计时、更新世界边界和计分板显示
 */
public class ZoneManager {

    //缩圈系统状态枚举,用于更清晰地表示当前系统的运行状态
    public enum ZoneState {
        IDLE,           // 空闲状态
        WARNING,        // 警告阶段（倒计时）
        SHRINKING,      // 缩圈中
        BETWEEN_STAGES  // 两阶段之间的休息期
    }

    //当前系统状态
    private static ZoneState currentState = ZoneState.IDLE;

    //计分板相关常量
    private static class ScoreboardConstants {
        public static final String SHRINK_IN_ENTRY = "shrink_in";     // 倒计时计分板条目
        public static final String SHRINKING_ENTRY = "shrinking";     // 缩圈时间计分板条目
        public static final String SCOREBOARD_OBJECTIVE = "zone";     // 计分板目标名称
    }
    
    // 静态全局状态
    private static ScheduledExecutorService scheduler = null;
    private static boolean isRunning = false;
    private static double zoneCenterX = 0;
    private static double zoneCenterZ = 0;

    //任务管理器内部类,负责管理和追踪所有调度任务
    private static class TaskManager {
        private ScheduledFuture<?> countdownTask = null;
        private ScheduledFuture<?> shrinkStartTask = null;
        private ScheduledFuture<?> shrinkUpdateTask = null;
        private ScheduledFuture<?> delayTask = null;

        //取消所有活动任务
        public void cancelAllTasks() {
            cancelTask(countdownTask);
            cancelTask(shrinkStartTask);
            cancelTask(shrinkUpdateTask);
            cancelTask(delayTask);
            
            countdownTask = null;
            shrinkStartTask = null;
            shrinkUpdateTask = null;
            delayTask = null;
        }
        
        //取消任务的方法
        private void cancelTask(ScheduledFuture<?> task) {
            if (task != null && !task.isDone() && !task.isCancelled()) {
                task.cancel(false);
            }
        }

        //设置倒计时任务
        public void setCountdownTask(ScheduledFuture<?> task) {
            this.countdownTask = task;
        }

        //设置缩圈启动任务
        public void setShrinkStartTask(ScheduledFuture<?> task) {
            this.shrinkStartTask = task;
        }

        //设置缩圈更新任务
        public void setShrinkUpdateTask(ScheduledFuture<?> task) {
            this.shrinkUpdateTask = task;
        }

        //设置延迟任务
        public void setDelayTask(ScheduledFuture<?> task) {
            this.delayTask = task;
        }

        //清除倒计时任务
        public void clearCountdownTask() {
            this.countdownTask = null;
        }

        //清除缩圈启动任务
        public void clearShrinkStartTask() {
            this.shrinkStartTask = null;
        }

        //清除缩圈更新任务
        public void clearShrinkUpdateTask() {
            this.shrinkUpdateTask = null;
        }

        //清除延迟任务
        public void clearDelayTask() {
            this.delayTask = null;
        }
    }
    
    // 任务管理器实例
    private static final TaskManager taskManager = new TaskManager();
    
    // 计分板本地缓存，避免频繁查询服务器
    private static int cachedShrinkInValue = 0;
    private static int cachedShrinkingValue = 0;

    //执行服务器任务
    private static void safeSubmit(MinecraftServer server, Runnable task) {
        server.submit(task);
    }

    //执行命令
    private static void safeExecuteCommand(MinecraftServer server, String command) {
        server.getCommands().performPrefixedCommand(
            server.createCommandSourceStack().withSuppressedOutput(),
            command
        );
    }

    //设置计分板值
    private static void setScoreboardValue(MinecraftServer server, String entry, int value) {
        safeExecuteCommand(server, 
            "scoreboard players set " + entry + " " + ScoreboardConstants.SCOREBOARD_OBJECTIVE + " " + value);
    }

    //重置计分板值
    private static void resetScoreboardValue(MinecraftServer server, String entry) {
        safeExecuteCommand(server, 
            "scoreboard players reset " + entry + " " + ScoreboardConstants.SCOREBOARD_OBJECTIVE);
    }

    //初始化调度器,仅当需要时创建新的调度器实例
    private static void initScheduler() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newScheduledThreadPool(1, r -> {
                Thread t = new Thread(r, "BattleRoyale-Zone");
                t.setDaemon(true);
                return t;
            });
        }
    }

    //取消所有任务并关闭调度器,在停止缩圈系统或服务器关闭时调用
    private static void cancelAllTasksAndShutdown() {
        // 取消所有运行中的任务
        taskManager.cancelAllTasks();
        
        // 关闭调度器
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    //启动缩圈系统
    public static void startShrinking(CommandSourceStack source, int stage) {
        // 验证阶段有效性
        int maxStage = ZoneConfig.getMaxStage();
        if (stage < 1 || stage > maxStage) {
            source.sendFailure(Component.literal(String.format("§c无效的缩圈阶段！请选择1-%d的阶段", maxStage)));
            return;
        }

        MinecraftServer server = source.getServer();
        
        // 如果已经在运行，先停止
        if (isRunning) {
            stopShrinking(source);
        }
        
        // 直接使用命令执行者的位置作为中心点
        Vec3 center = source.getPosition();
        currentState = ZoneState.WARNING;
    
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
            server.overworld().getWorldBorder().setCenter(center.x, center.z);
            server.overworld().getWorldBorder().setSize(currentSize);
            
            // 设置计分板显示倒计时
            setScoreboardValue(server, ScoreboardConstants.SHRINK_IN_ENTRY, warningTime);
            
            // 初始化缓存值
            cachedShrinkInValue = warningTime;
        });
    
        // 初始化调度器
        initScheduler();
        isRunning = true;
    
        // 创建倒计时更新任务
        createCountdownTask(server);
        
        // 创建缩圈启动任务
        createShrinkStartTask(server, stage, currentSize, nextSize, shrinkTime, warningTime);
    }

    //创建倒计时任务,负责每秒更新倒计时计分板
    private static void createCountdownTask(MinecraftServer server) {
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            // 本地递减计数，减少服务器交互
            if (cachedShrinkInValue > 0) {
                cachedShrinkInValue--;
                
                // 每秒更新计分板
                safeSubmit(server, () -> setScoreboardValue(server, ScoreboardConstants.SHRINK_IN_ENTRY, cachedShrinkInValue));
            } else {
                // 倒计时结束，取消任务
                taskManager.cancelTask(taskManager.countdownTask);
                taskManager.clearCountdownTask();
                
                safeSubmit(server, () -> resetScoreboardValue(server, ScoreboardConstants.SHRINK_IN_ENTRY));
            }
        }, 1, 1, TimeUnit.SECONDS);
        taskManager.setCountdownTask(task);
    }
    
    //创建缩圈启动任务,在倒计时结束后执行，开始缩圈并启动缩圈更新任务
    private static void createShrinkStartTask(
            MinecraftServer server, 
            int stage,
            int currentSize, 
            int nextSize, 
            int shrinkTime, 
            int delaySeconds) {
            
        ScheduledFuture<?> task = scheduler.schedule(() -> {
            // 更新状态
            currentState = ZoneState.SHRINKING;
            
            // 启动缩圈
            safeSubmit(server, () -> {
                // 设置worldborder缩小
                server.overworld().getWorldBorder().lerpSizeBetween(currentSize, nextSize, shrinkTime * 1000L);
                
                // 设置计分板显示缩圈持续时间
                setScoreboardValue(server, ScoreboardConstants.SHRINKING_ENTRY, shrinkTime);
                
                // 初始化缓存值
                cachedShrinkingValue = shrinkTime;
                
                // 发送缩圈开始通知
                int finalStage = ZoneConfig.getFinalStage();
                String message;
                if (stage + 1 == finalStage) {
                    message = String.format("§6安全区正在缩小！将在%d秒内缩小到最终大小(%d格)", 
                                           shrinkTime, nextSize);
                } else {
                    message = String.format("§6安全区正在缩小！将在%d秒内缩小到%d格大小", 
                                           shrinkTime, nextSize);
                }
                server.getPlayerList().broadcastSystemMessage(
                    Component.literal(message), 
                    false
                );
            });
    
            // 创建缩圈进行中的更新任务
            createShrinkingUpdateTask(server, stage);
            
            // 清理当前任务引用
            taskManager.clearShrinkStartTask();
            
        }, delaySeconds, TimeUnit.SECONDS);
        
        taskManager.setShrinkStartTask(task);
    }

    //创建缩圈过程中的更新任务,负责更新缩圈时间计分板并在缩圈结束后执行后续操作
    private static void createShrinkingUpdateTask(
            MinecraftServer server, 
            int stage) {
            
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            // 本地递减计数，减少服务器交互
            if (cachedShrinkingValue > 0) {
                cachedShrinkingValue--;
                
                // 每秒更新计分板
                safeSubmit(server, () -> setScoreboardValue(server, ScoreboardConstants.SHRINKING_ENTRY, cachedShrinkingValue));
            } else {
                // 缩圈结束，取消任务
                taskManager.cancelTask(taskManager.shrinkUpdateTask);
                taskManager.clearShrinkUpdateTask();
                
                // 更新状态
                currentState = ZoneState.BETWEEN_STAGES;
                
                safeSubmit(server, () -> {
                    resetScoreboardValue(server, ScoreboardConstants.SHRINKING_ENTRY);
                    
                    // 缩圈结束后的处理
                    handleShrinkingComplete(server, stage);
                });
            }
        }, 1, 1, TimeUnit.SECONDS);
        
        taskManager.setShrinkUpdateTask(task);
    }

    //处理缩圈完成后的操作,决定是否进入下一阶段或结束整个缩圈流程
    private static void handleShrinkingComplete(MinecraftServer server, int stage) {
        int nextStage = stage + 1;
        int finalStage = ZoneConfig.getFinalStage();
        
        // 检查是否还有下一阶段
        if (nextStage < finalStage) {
            Random random = new Random();
            int randomDelay = random.nextInt(11) + 10; // 10-20秒的随机延迟
            
            // 获取当前缩圈后的安全区大小
            int currentZoneSize = ZoneConfig.getZoneSize(nextStage);

            safeSubmit(server, () -> {
                // 通过Airdrop类调度空投生成
                net.harrison.battleroyale.airdrop.Airdrop.scheduleAirdrop(
                    server, scheduler, zoneCenterX, zoneCenterZ, currentZoneSize);
            });

            // 创建延迟任务
            ScheduledFuture<?> task = scheduler.schedule(() -> {
                // 如果已被停止，不继续下一阶段
                if (!isRunning) {
                    return;
                }
                
                // 清理引用
                taskManager.clearDelayTask();
                
                // 递归调用启动下一阶段
                safeSubmit(server, () -> {
                    CommandSourceStack fakeSource = createFakeCommandSource(server);
                    startShrinking(fakeSource, nextStage);
                });
            }, randomDelay, TimeUnit.SECONDS);
            
            taskManager.setDelayTask(task);
            
        } else {
            // 所有阶段已完成
            server.getPlayerList().broadcastSystemMessage(
                Component.literal("§6缩圈已全部完成！已达到最终安全区！"),
                false
            );
            isRunning = false;
            currentState = ZoneState.IDLE;
        }
    }

    //创建一个模拟的命令源,用于递归调用startShrinking
    private static CommandSourceStack createFakeCommandSource(MinecraftServer server) {
        // 创建一个以保存的中心点为位置的命令源
        return server.createCommandSourceStack()
            .withPosition(new Vec3(zoneCenterX, 0, zoneCenterZ));
    }

    //停止缩圈系统
    public static void stopShrinking(CommandSourceStack source) {
        if (!isRunning) {
            source.sendFailure(Component.literal("§c缩圈系统未在运行！"));
            return;
        }

        // 取消所有任务并关闭调度器
        cancelAllTasksAndShutdown();
        isRunning = false;
        currentState = ZoneState.IDLE;
        
        // 清除计分板
        safeSubmit(source.getServer(), () -> {
            resetScoreboardValue(source.getServer(), ScoreboardConstants.SHRINK_IN_ENTRY);
            resetScoreboardValue(source.getServer(), ScoreboardConstants.SHRINKING_ENTRY);

        });


         // 将世界边界设置为最大值(59999968)，相当于无限大
         safeSubmit(source.getServer(), () -> {
             // 获取主世界
             ServerLevel overworld = source.getServer().getLevel(ServerLevel.OVERWORLD);
             if (overworld != null) {
                 // 保持边界中心不变，只改变大小
                 double centerX = overworld.getWorldBorder().getCenterX();
                 double centerZ = overworld.getWorldBorder().getCenterZ();
                 
                 // 设置边界到最大值
                 overworld.getWorldBorder().setCenter(centerX, centerZ);
                 overworld.getWorldBorder().setSize(59999968);
             }
         });
    }

    //服务器关闭时清理所有资源
    public static void cleanupAllWorlds() {
        cancelAllTasksAndShutdown();
        isRunning = false;
        currentState = ZoneState.IDLE;
    }
}