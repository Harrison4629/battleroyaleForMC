package net.harrison.battleroyale.event;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "battleroyale", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FallDamageEvent {

    private static final Map<UUID, Boolean> FALL_DAMAGE_IMMUNITY = new HashMap<>();

    public static boolean isImmuneToFallDamage(UUID playerId) {
        return FALL_DAMAGE_IMMUNITY.containsKey(playerId) && FALL_DAMAGE_IMMUNITY.get(playerId);
    }

    public static void setFallDamageImmunity(UUID playerId, boolean immune) {
        FALL_DAMAGE_IMMUNITY.put(playerId, immune);
    }

    @SubscribeEvent
    public static void onPlayerHurt(LivingHurtEvent event) {
        DamageSource source = event.getSource();
        if (source.is(DamageTypes.FALL) && !event.getEntity().level.isClientSide() && event.getEntity() instanceof Player player) {
            if (isImmuneToFallDamage(player.getUUID())) {
                event.setCanceled(true);
                setFallDamageImmunity(player.getUUID(), false);
            }
        }
    }

}
