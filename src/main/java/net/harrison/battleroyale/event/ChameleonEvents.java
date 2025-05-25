package net.harrison.battleroyale.event;

import net.harrison.battleroyale.items.custom.ChameleonItem;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber
public class ChameleonEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.player.level.isClientSide) {
            // 定期清理过期的隐身记录
            if (event.player.tickCount % 200 == 0) { // 每10秒左右清理一次
                cleanupExpiredInvisibility();
            }
        }
    }
    
    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        if (!event.getEntity().level.isClientSide() && event.getEntity() instanceof Player player) {

            // 如果玩家处于隐身状态并且受到伤害
            if (ChameleonItem.INVISIBLE_PLAYERS.containsKey(player.getUUID())) {
                // 移除隐身效果
                player.removeEffect(MobEffects.INVISIBILITY);
                // 从跟踪表中移除玩家
                ChameleonItem.INVISIBLE_PLAYERS.remove(player.getUUID());
            }
        }
    }
    
    private static void cleanupExpiredInvisibility() {
        long currentTime = System.currentTimeMillis();

        ChameleonItem.INVISIBLE_PLAYERS.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }
}
