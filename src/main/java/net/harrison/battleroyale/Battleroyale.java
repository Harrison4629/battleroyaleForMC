package net.harrison.battleroyale;

import com.mojang.logging.LogUtils;
import net.harrison.battleroyale.command.ModCommands;
import net.harrison.battleroyale.items.ModCreativeModeTab;
import net.harrison.battleroyale.items.ModItems;
import net.harrison.battleroyale.zone.ZoneManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import net.harrison.battleroyale.entities.ModEntities;
@Mod(Battleroyale.MODID)
public class Battleroyale {

    public static final String MODID = "battleroyale";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Battleroyale() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.ITEMS.register(modEventBus);
        ModEntities.register(modEventBus);



        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        // 确保服务器启动时计分板已创建
        event.getServer().executeBlocking(() -> {
            // 检查计分板是否存在，如不存在则创建
            if (event.getServer().getScoreboard().getObjective("zone") == null) {
                    event.getServer().getCommands().performPrefixedCommand(
                        event.getServer().createCommandSourceStack().withSuppressedOutput(),
                        "scoreboard objectives add zone dummy \"§eBattleRoyale\""
                    );
                    event.getServer().getCommands().performPrefixedCommand(
                            event.getServer().createCommandSourceStack().withSuppressedOutput(),
                        "scoreboard objectives setdisplay sidebar zone"
                    );
                event.getServer().getCommands().performPrefixedCommand(
                        event.getServer().createCommandSourceStack().withSuppressedOutput(),
                        "scoreboard players set -------- zone 0"
                );
                event.getServer().getCommands().performPrefixedCommand(
                        event.getServer().createCommandSourceStack().withSuppressedOutput(),
                        "worldborder damage buffer 0"
                );
                event.getServer().getCommands().performPrefixedCommand(
                        event.getServer().createCommandSourceStack().withSuppressedOutput(),
                        "worldborder damage amount 0.1"
                );
            }

        });
    }
    
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        // 服务器关闭时清理资源
        LOGGER.info("清理BattleRoyale缩圈系统资源...");
        ZoneManager.cleanupAllWorlds();
    }


    //创造模式背包模组物品在此添加
    @SubscribeEvent
    public void addCreative(CreativeModeTabEvent.BuildContents event) {
        if(event.getTab() == ModCreativeModeTab.BATTLEROYALEMOD) {
            event.accept(ModItems.MEDKIT.get());
            event.accept(ModItems.BANDAGE.get());
            event.accept(ModItems.REGENERATION_SYRINGE.get());
            event.accept(ModItems.CHAMELEON.get());
            event.accept(ModItems.ARMOR_PLATE_1.get());
            event.accept(ModItems.ARMOR_PLATE_2.get());
            event.accept(ModItems.ARMOR_PLATE_3.get());
            event.accept(ModItems.ARMOR_PLATE_4.get());
        }
    }
}
