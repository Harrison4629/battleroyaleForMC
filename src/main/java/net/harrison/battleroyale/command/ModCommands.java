package net.harrison.battleroyale.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.harrison.battleroyale.airdrop.Airdrop;
import net.harrison.battleroyale.zone.ZoneManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // 基础设置命令
        dispatcher.register(Commands.literal("brsettings").executes( (context) -> {
                    context.getSource().sendSuccess(Component.literal("§6大逃杀模组设置菜单"), true);
                    return 1;
                }
        ));

        // 缩圈系统命令
        dispatcher.register(Commands.literal("brzone")
            .requires(source -> source.hasPermission(2)) // 需要权限等级2
            .then(Commands.literal("start")
                .then(Commands.argument("stage", IntegerArgumentType.integer(1, 5))
                    .executes(context -> {
                        int stage = IntegerArgumentType.getInteger(context, "stage");
                        ZoneManager.startShrinking(context.getSource(), stage);
                        return 1;
                    })
                )
                .executes(context -> {
                    context.getSource().sendSuccess(Component.literal("§c请指定缩圈阶段 (1-5)"), false);
                    return 0;
                })
            )
            .then(Commands.literal("stop")
                .executes(context -> {
                    ZoneManager.stopShrinking(context.getSource());
                    Airdrop.clearAllAirdrops(context.getSource().getServer());
                    return 1;
                })
            )
            .then(Commands.literal("help")
                .executes(context -> {
                    context.getSource().sendSuccess(Component.literal("""
                            §6大逃杀缩圈系统帮助：
                            §e/brzone start <阶段> §7- 开始指定阶段的缩圈 (1-5)
                            §e/brzone stop §7- 停止当前缩圈进程
                            §e/brzone vanilla §7- 使用原版数据包缩圈功能
                            §e/brzone info §7- 显示当前缩圈系统状态"""), true);
                    return 1;
                })
            )
            .then(Commands.literal("info")
                .executes(context -> {
                    context.getSource().sendSuccess(Component.literal(
                            "§6大逃杀缩圈配置：\n" +
                            "§7- 第1圈大小: §e" + net.harrison.battleroyale.config.ZoneConfig.ZONE_SIZE_1 + " 方块\n" +
                            "§7- 第2圈大小: §e" + net.harrison.battleroyale.config.ZoneConfig.ZONE_SIZE_2 + " 方块\n" +
                            "§7- 第3圈大小: §e" + net.harrison.battleroyale.config.ZoneConfig.ZONE_SIZE_3 + " 方块\n" +
                            "§7- 第4圈大小: §e" + net.harrison.battleroyale.config.ZoneConfig.ZONE_SIZE_4 + " 方块\n" +
                            "§7- 第5圈大小: §e" + net.harrison.battleroyale.config.ZoneConfig.ZONE_SIZE_5 + " 方块\n" +
                            "§7- 第6圈大小: §e" + net.harrison.battleroyale.config.ZoneConfig.ZONE_SIZE_6 + " 方块"), true);
                    return 1;
                })
            )
            .executes(context -> {
                context.getSource().sendSuccess(Component.literal("§6大逃杀缩圈系统\n" +
                        "§7请使用 §e/brzone help §7查看命令帮助"), true);
                return 1;
            })
        );
    }
}
