package net.harrison.battleroyale.client.sound;

import net.harrison.battleroyale.sounds.ItemUsingSoundInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 管理物品使用过程中的声音实例
 * 这个类只在客户端运行
 */
@OnlyIn(Dist.CLIENT)
public class UsingSoundManager {
    private static final Map<UUID, ItemUsingSoundInstance> ACTIVE_SOUNDS = new HashMap<>();
    
    /**
     * 为指定实体播放物品使用声音
     * @param entity 使用物品的实体
     * @param soundEvent 要播放的声音
     * @param volume 音量
     * @param pitch 音调
     */
    public static void playUsingSound(LivingEntity entity, SoundEvent soundEvent, float volume, float pitch) {
        if (entity == null || soundEvent == null) return;
        
        UUID entityId = entity.getUUID();
        // 停止该实体的任何现有声音
        stopUsingSound(entity);
        
        // 创建新的声音实例并开始播放
        ItemUsingSoundInstance soundInstance = new ItemUsingSoundInstance(entity, soundEvent, volume, pitch);
        ACTIVE_SOUNDS.put(entityId, soundInstance);
        Minecraft.getInstance().getSoundManager().play(soundInstance);
    }
    
    /**
     * 停止指定实体的物品使用声音
     * @param entity 使用物品的实体
     */
    public static void stopUsingSound(LivingEntity entity) {
        if (entity == null) return;
        
        UUID entityId = entity.getUUID();
        if (ACTIVE_SOUNDS.containsKey(entityId)) {
            ItemUsingSoundInstance sound = ACTIVE_SOUNDS.get(entityId);
            sound.stopSound(); // 使用公共的stopSound方法
            ACTIVE_SOUNDS.remove(entityId);
        }
    }
}
