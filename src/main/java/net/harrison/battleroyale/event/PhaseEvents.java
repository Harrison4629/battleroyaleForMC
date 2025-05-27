package net.harrison.battleroyale.event;

import net.harrison.battleroyale.Battleroyale;
import net.harrison.battleroyale.util.PhaseData;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = Battleroyale.MODID)
public class PhaseEvents {


    /**
     * 生成位移时的粒子效果
     */
    private static void spawnPhaseParticles(Player player) {
        for (int i = 0; i < 2; i++) {
            player.level.addParticle(
                    ParticleTypes.PORTAL,
                    player.getX() + (player.level.random.nextDouble() - 0.5) * 0.5,
                    player.getY() + 0.5 + player.level.random.nextDouble() * 0.5,
                    player.getZ() + (player.level.random.nextDouble() - 0.5) * 0.5,
                    0, 0.1, 0);
        }
    }
}
