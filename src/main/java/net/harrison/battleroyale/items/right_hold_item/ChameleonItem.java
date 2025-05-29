package net.harrison.battleroyale.items.right_hold_item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChameleonItem extends AbstractUsableItem {
    private static final int USE_DURATION = 10;
    private static final int INVISIBILITY_DURATION = 100; // 持续5秒
    private static final int COOLDOWN_TICKS = 140; // 7秒冷却
    
    // 存储隐身玩家及其隐身结束时间
    public static final Map<UUID, Long> INVISIBLE_PLAYERS = new ConcurrentHashMap<>();

    public ChameleonItem(Properties properties) {
        super(properties, USE_DURATION, COOLDOWN_TICKS);
    }

    @Override
    protected void applyEffect(Player player, Level level) {
        // 添加隐形效果
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, INVISIBILITY_DURATION, 0, false, false));
        
        // 记录玩家隐身状态及结束时间
        INVISIBLE_PLAYERS.put(player.getUUID(), System.currentTimeMillis() + (INVISIBILITY_DURATION * 50)); // 转换为毫秒
        
        // 显示成功使用消息
        player.displayClientMessage(Component.translatable("item.battleroyale.chameleon.use_success")
                .withStyle(ChatFormatting.GREEN), true);
    }

    @Override
    protected String getFailTranslationKey() {
        return "item.battleroyale.chameleon.use_fail";
    }

    @Override
    protected String getTooltipTranslationKey() {
        return "item.battleroyale.chameleon.tooltip";
    }

    @Override
    protected String getUseTooltipTranslationKey() {
        return "item.battleroyale.chameleon.tooltip.use";
    }
    
    @Override
    protected UseAnim getItemUseAnimation() {
        return UseAnim.BRUSH;
    }

    @Override
    public SoundEvent getUsingSound() {
        return SoundEvents.PHANTOM_FLAP;
    }

    @Override
    protected SoundEvent getFinishSound() {
        return SoundEvents.PHANTOM_FLAP;
    }
    
    @Override
    protected ParticleOptions getParticleType() {
        return ParticleTypes.SNOWFLAKE;
    }
}
