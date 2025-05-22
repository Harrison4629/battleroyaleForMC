package net.harrison.battleroyale.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class MedkitItem extends AbstractUsableItem {
    private static final int USE_DURATION = 60; // 3秒 (60ticks)
    private static final int HEALING_AMOUNT = 12;
    private static final int COOLDOWN_TICKS = 100;

    public MedkitItem(Properties properties) {
        super(properties, USE_DURATION, COOLDOWN_TICKS);
    }

    @Override
    protected void applyEffect(Player player, Level level) {
        // 回复12点生命值
        player.heal(HEALING_AMOUNT);
        
        // 显示成功使用消息
        player.displayClientMessage(Component.translatable("item.battleroyale.medkit.heal_success")
                .withStyle(ChatFormatting.GREEN), true);
    }

    @Override
    protected String getFailTranslationKey() {
        return "item.battleroyale.medkit.heal_fail";
    }

    @Override
    protected String getTooltipTranslationKey() {
        return "item.battleroyale.medkit.tooltip";
    }

    @Override
    protected String getUseTooltipTranslationKey() {
        return "item.battleroyale.medkit.tooltip.use";
    }
}
