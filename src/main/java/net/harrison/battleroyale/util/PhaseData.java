package net.harrison.battleroyale.util;

import net.minecraft.world.phys.Vec3;

/**
 * 位移魔核使用的数据类，存储原始位置和计时器
 */
public class PhaseData {
    private final Vec3 originalPosition;
    private final int startTime;
    private final int duration;
    private final Vec3 direction;
    private final float moveSpeed;
    
    public PhaseData(Vec3 originalPosition, Vec3 direction, int startTime, int duration, float moveSpeed) {
        this.originalPosition = originalPosition;
        this.direction = direction;
        this.startTime = startTime;
        this.duration = duration;
        this.moveSpeed = moveSpeed;
    }
    
    public Vec3 getOriginalPosition() {
        return originalPosition;
    }
    
    public int getStartTime() {
        return startTime;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public Vec3 getDirection() {
        return direction;
    }
    
    public float getMoveSpeed() {
        return moveSpeed;
    }
    
    /**
     * 计算当前移动进度
     * @param currentTime 当前时间
     * @return 0.0-1.0之间的进度值
     */
    public float getProgress(int currentTime) {
        int elapsed = currentTime - startTime;
        if (elapsed <= 0) return 0.0f;
        if (elapsed >= duration) return 1.0f;
        
        // 简单的线性插值
        return (float) elapsed / duration;
    }
    
    /**
     * 判断位移是否已经结束
     * @param currentTime 当前时间
     * @return 是否结束
     */
    public boolean isFinished(int currentTime) {
        return currentTime >= (startTime + duration);
    }
}
