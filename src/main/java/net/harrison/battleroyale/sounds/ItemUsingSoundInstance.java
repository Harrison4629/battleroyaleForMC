package net.harrison.battleroyale.sounds;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class ItemUsingSoundInstance extends AbstractTickableSoundInstance {
    private final LivingEntity entity;
    private int ticksUsingItem;
    private boolean started = false;

    public ItemUsingSoundInstance(LivingEntity entity, SoundEvent soundEvent, float volume, float pitch) {
        super(soundEvent, SoundSource.PLAYERS, SoundInstance.createUnseededRandom());
        this.entity = entity;
        this.volume = volume;
        this.pitch = pitch;
        this.looping = true;
        this.delay = 0;
        this.relative = false;
        this.ticksUsingItem = 0;
    }

    /**
     * 公共方法用于停止声音播放
     * 由于父类中的stop()是protected的，所以需要这个公共方法
     */
    public void stopSound() {
        this.stop();
    }

    @Override
    public void tick() {
        if (entity != null && entity.isAlive()) {
            if (entity.isUsingItem()) {
                // 更新声音位置为实体当前位置
                this.x = (float) entity.getX();
                this.y = (float) entity.getY();
                this.z = (float) entity.getZ();
                
                if (!started) {
                    started = true;
                }
                
                // 可以根据使用时间变化声音特性
                ticksUsingItem++;
                // 例如：随时间略微降低音调
                if (ticksUsingItem % 20 == 0) {
                    this.pitch = Math.max(0.8f, this.pitch - 0.01f);
                }
            } else {
                // 实体不再使用物品，停止声音
                this.stop();
            }
        } else {
            // 实体不存在或已死亡，停止声音
            this.stop();
        }
    }
}
