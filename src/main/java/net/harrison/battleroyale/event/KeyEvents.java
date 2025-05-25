package net.harrison.battleroyale.event;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.util.KeyBinding;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.Minecart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.swing.text.JTextComponent;

public class KeyEvents {
    @Mod.EventBusSubscriber(modid = Battleroyale.MODID, value = Dist.CLIENT)
    public static class KeyBindingEvents {
        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.STOP_PHASING_KEY);

        }


        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if(KeyBinding.STOP_PHASING_KEY.consumeClick()) {


            }


        }
    }



}
