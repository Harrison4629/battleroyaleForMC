package net.harrison.battleroyale.items.rightHoldItem;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class RegenerationSyringeItem extends AbstractUsableItem {
    private static final int USE_DURATION = 100; // 使用时间5秒
    private static final int REGEN_DURATION = 400; // 持续20秒
    private static final int COOLDOWN_TICKS = 200; // 10秒冷却

    public RegenerationSyringeItem(Properties properties) {
        super(properties, USE_DURATION, COOLDOWN_TICKS);
    }

    @Override
    protected void applyEffect(Player player, Level level) {
        // 添加再生效果 - 40秒持续时间
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, REGEN_DURATION, 1, false, false));
        
        // 显示成功使用消息
        player.displayClientMessage(Component.translatable("item.battleroyale.regeneration_syringe.use_success")
                .withStyle(ChatFormatting.YELLOW), true);
    }

    @Override
    protected String getFailTranslationKey() {
        return "item.battleroyale.regeneration_syringe.use_fail";
    }

    @Override
    protected String getTooltipTranslationKey() {
        return "item.battleroyale.regeneration_syringe.tooltip";
    }

    @Override
    protected String getUseTooltipTranslationKey() {
        return "item.battleroyale.regeneration_syringe.tooltip.use";
    }
    
    @Override
    protected SoundEvent getFinishSound() {
        return SoundEvents.PLAYER_HURT_SWEET_BERRY_BUSH;
    }
    
    @Override
    public SoundEvent getUsingSound() {
        return SoundEvents.DISPENSER_DISPENSE;
    }

    @Override
    public float volume() {
        return 0.1F;
    }

    @Override
    protected ParticleOptions getParticleType() {
        return ParticleTypes.EFFECT;
    }
}
