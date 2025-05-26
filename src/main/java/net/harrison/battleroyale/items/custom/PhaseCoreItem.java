package net.harrison.battleroyale.items.custom;

import net.harrison.battleroyale.util.PhaseTracker;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class PhaseCoreItem extends AbstractUsableItem{
    private static final int USE_DURATION = 10;
    private static final int COOLDOWN_TICKS = 100; // 5秒冷却
    private static final int TRACE_BACK_TIME = 100; // 5秒后传送回去
    private static final float PHASE_SPEED = 0.3f; // 移动速度

    public PhaseCoreItem(Properties properties) {
        super(properties, USE_DURATION, COOLDOWN_TICKS);
    }

    @Override
    protected void applyEffect(Player player, Level level) {
        if (!level.isClientSide) {
            // 开始位移
            PhaseTracker.startPhasing(player, TRACE_BACK_TIME, PHASE_SPEED);
            
            // 显示提示信息
            player.displayClientMessage(
                    Component.translatable("item.battleroyale.phase_core.use_success"),
                    true);
            
            // 立即执行一次位移更新，确保效果立即生效
            if (player instanceof ServerPlayer serverPlayer) {
                PhaseTracker.updatePhasing(serverPlayer);
            }
        }
    }

    @Override
    protected String getFailTranslationKey() {
        return "item.battleroyale.phase_core.use_fail";
    }

    @Override
    protected String getTooltipTranslationKey() {
        return "item.battleroyale.phase_core.tooltip";
    }

    @Override
    protected String getUseTooltipTranslationKey() {
        return "item.battleroyale.phase_core.tooltip.use";
    }
    
    @Override
    protected SoundEvent getFinishSound() {
        return SoundEvents.ENDERMAN_TELEPORT;
    }
    
    @Override
    public SoundEvent getUsingSound() {
        return SoundEvents.PORTAL_AMBIENT;
    }
    
    @Override
    protected UseAnim getItemUseAnimation() {
        return UseAnim.CROSSBOW;
    }
    
    @Override
    protected ParticleOptions getParticleType() {
        return ParticleTypes.PORTAL;
    }
}
