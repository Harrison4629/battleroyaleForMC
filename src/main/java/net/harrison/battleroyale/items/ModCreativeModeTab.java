package net.harrison.battleroyale.items;

import net.harrison.battleroyale.Battleroyale;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Battleroyale.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTab {
    public static CreativeModeTab BATTLEROYALEMOD;

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event) {
        BATTLEROYALEMOD = event.registerCreativeModeTab(new ResourceLocation(Battleroyale.MODID,"battleroyalemodtab"),
                builder -> builder.icon(() -> new ItemStack(ModItems.MEDKIT.get())).title(Component.translatable("itemGroup.battleroyalemodtab")).build());


    }
}
