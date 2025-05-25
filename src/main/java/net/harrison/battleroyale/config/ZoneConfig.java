package net.harrison.battleroyale.config;

/**
 * 大逃杀模式的缩圈系统配置类
 * 存储所有与缩圈相关的常量，便于统一管理和修改
 */
public class ZoneConfig {
    // 圈大小设置（从大到小）
    public static final int ZONE_SIZE_1 = 600; // 第一圈大小
    public static final int ZONE_SIZE_2 = 300; // 第二圈大小
    public static final int ZONE_SIZE_3 = 150; // 第三圈大小
    public static final int ZONE_SIZE_4 = 70;  // 第四圈大小
    public static final int ZONE_SIZE_5 = 30;  // 第五圈大小
    public static final int ZONE_SIZE_6 = 5;  // 第六圈大小（最终圈）
    
    // 各阶段倒计时时间（秒）
    public static final int ZONE_WARNING_TIME_1 = 50; // 第一阶段警告时间
    public static final int ZONE_WARNING_TIME_2 = 40; // 第二阶段警告时间
    public static final int ZONE_WARNING_TIME_3 = 40; // 第三阶段警告时间
    public static final int ZONE_WARNING_TIME_4 = 30;  // 第四阶段警告时间
    public static final int ZONE_WARNING_TIME_5 = 20;  // 第五阶段警告时间
    
    // 各阶段缩圈持续时间（秒）
    public static final int ZONE_SHRINK_TIME_1 = 90; // 第一阶段缩圈持续时间
    public static final int ZONE_SHRINK_TIME_2 = 75;  // 第二阶段缩圈持续时间
    public static final int ZONE_SHRINK_TIME_3 = 60;  // 第三阶段缩圈持续时间
    public static final int ZONE_SHRINK_TIME_4 = 45;  // 第四阶段缩圈持续时间
    public static final int ZONE_SHRINK_TIME_5 = 30;  // 第五阶段缩圈持续时间

    //获取指定阶段的圈大小
    public static int getZoneSize(int stage) {
        return switch (stage) {
            case 1 -> ZONE_SIZE_1;
            case 2 -> ZONE_SIZE_2;
            case 3 -> ZONE_SIZE_3;
            case 4 -> ZONE_SIZE_4;
            case 5 -> ZONE_SIZE_5;
            case 6 -> ZONE_SIZE_6;
            default -> ZONE_SIZE_1;
        };
    }

    //获取指定阶段的警告时间
    public static int getWarningTime(int stage) {
        return switch (stage) {
            case 1 -> ZONE_WARNING_TIME_1;
            case 2 -> ZONE_WARNING_TIME_2;
            case 3 -> ZONE_WARNING_TIME_3;
            case 4 -> ZONE_WARNING_TIME_4;
            case 5 -> ZONE_WARNING_TIME_5;
            default -> ZONE_WARNING_TIME_1;
        };
    }

    //获取指定阶段的缩圈持续时间
    public static int getShrinkTime(int stage) {
        return switch (stage) {
            case 1 -> ZONE_SHRINK_TIME_1;
            case 2 -> ZONE_SHRINK_TIME_2;
            case 3 -> ZONE_SHRINK_TIME_3;
            case 4 -> ZONE_SHRINK_TIME_4;
            case 5 -> ZONE_SHRINK_TIME_5;
            default -> ZONE_SHRINK_TIME_1;
        };
    }

    //获取配置的最大阶段数
    public static int getMaxStage() {
        return 5; // 当前配置支持5个阶段，最终圈是第6个
    }

    //获取配置的最终圈阶段数
    public static int getFinalStage() {
        return getMaxStage() + 1; // 最终圈阶段
    }
}
