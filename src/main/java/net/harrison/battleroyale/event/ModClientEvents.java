package net.harrison.battleroyale.event;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.client.sound.UsingSoundManager;
import net.harrison.battleroyale.items.custom.AbstractUsableItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ModClientEvents {
    
    @SubscribeEvent
    public static void onItemUseStart(LivingEntityUseItemEvent.Start event) {
        if (event.getItem().getItem() instanceof AbstractUsableItem usableItem) {
            // 当开始使用物品时，播放持续声音
            UsingSoundManager.playUsingSound(
                    event.getEntity(), 
                    usableItem.getUsingSound(),
                    usableItem.volume(),
                    usableItem.pitch()
            );
        }
    }
    
    @SubscribeEvent
    public static void onItemUseStop(LivingEntityUseItemEvent.Stop event) {
        if (event.getItem().getItem() instanceof AbstractUsableItem) {
            // 当停止使用物品时，停止声音
            UsingSoundManager.stopUsingSound(event.getEntity());
        }
    }
    
    @SubscribeEvent
    public static void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
        if (event.getItem().getItem() instanceof AbstractUsableItem) {
            // 当完成使用物品时，停止声音
            UsingSoundManager.stopUsingSound(event.getEntity());
        }
    }
}
