package net.harrison.battleroyale.config;

/**
 * 大逃杀模式的缩圈系统配置类
 * 存储所有与缩圈相关的常量，便于统一管理和修改
 */
public class ZoneConfig {
    // 圈大小设置（从大到小）
    public static final int ZONE_SIZE_1 = 300; // 第一圈大小
    public static final int ZONE_SIZE_2 = 200; // 第二圈大小
    public static final int ZONE_SIZE_3 = 120; // 第三圈大小
    public static final int ZONE_SIZE_4 = 60;  // 第四圈大小
    public static final int ZONE_SIZE_5 = 30;  // 第五圈大小
    public static final int ZONE_SIZE_6 = 5;  // 第六圈大小（最终圈）
    
    // 各阶段倒计时时间（秒）
    public static final int ZONE_WARNING_TIME_1 = 60; // 第一阶段警告时间
    public static final int ZONE_WARNING_TIME_2 = 50; // 第二阶段警告时间
    public static final int ZONE_WARNING_TIME_3 = 40; // 第三阶段警告时间
    public static final int ZONE_WARNING_TIME_4 = 30;  // 第四阶段警告时间
    public static final int ZONE_WARNING_TIME_5 = 20;  // 第五阶段警告时间
    
    // 各阶段缩圈持续时间（秒）
    public static final int ZONE_SHRINK_TIME_1 = 90; // 第一阶段缩圈持续时间
    public static final int ZONE_SHRINK_TIME_2 = 75;  // 第二阶段缩圈持续时间
    public static final int ZONE_SHRINK_TIME_3 = 60;  // 第三阶段缩圈持续时间
    public static final int ZONE_SHRINK_TIME_4 = 45;  // 第四阶段缩圈持续时间
    public static final int ZONE_SHRINK_TIME_5 = 30;  // 第五阶段缩圈持续时间
    
    /**
     * 获取指定阶段的圈大小
     * @param stage 阶段（1-6）
     * @return 圈大小（方块）
     */
    public static int getZoneSize(int stage) {
        switch (stage) {
            case 1: return ZONE_SIZE_1;
            case 2: return ZONE_SIZE_2;
            case 3: return ZONE_SIZE_3;
            case 4: return ZONE_SIZE_4;
            case 5: return ZONE_SIZE_5;
            case 6: return ZONE_SIZE_6;
            default: return ZONE_SIZE_1;
        }
    }
    
    /**
     * 获取指定阶段的警告时间
     * @param stage 阶段（1-5）
     * @return 警告时间（秒）
     */
    public static int getWarningTime(int stage) {
        switch (stage) {
            case 1: return ZONE_WARNING_TIME_1;
            case 2: return ZONE_WARNING_TIME_2;
            case 3: return ZONE_WARNING_TIME_3;
            case 4: return ZONE_WARNING_TIME_4;
            case 5: return ZONE_WARNING_TIME_5;
            default: return ZONE_WARNING_TIME_1;
        }
    }
    
    /**
     * 获取指定阶段的缩圈持续时间
     * @param stage 阶段（1-5）
     * @return 缩圈持续时间（秒）
     */
    public static int getShrinkTime(int stage) {
        switch (stage) {
            case 1: return ZONE_SHRINK_TIME_1;
            case 2: return ZONE_SHRINK_TIME_2;
            case 3: return ZONE_SHRINK_TIME_3;
            case 4: return ZONE_SHRINK_TIME_4;
            case 5: return ZONE_SHRINK_TIME_5;
            default: return ZONE_SHRINK_TIME_1;
        }
    }
}
