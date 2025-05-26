package net.harrison.battleroyale.event;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.util.PhaseTracker;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModServerEvents {

    /**
     * 服务器世界刻事件处理 - 更新所有正在位移的玩家
     * @param event 世界刻事件
     */
    @SubscribeEvent
    public static void onServerLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && !event.level.isClientSide) {
            // 在服务端世界每tick结束时更新所有位移中的玩家
            PhaseTracker.updateAllPhasingPlayers((ServerLevel) event.level);
        }
    }
}
