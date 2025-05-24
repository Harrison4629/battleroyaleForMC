package net.harrison.battleroyale.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BandageItem extends AbstractUsableItem {
    private static final int USE_DURATION = 20;
    private static final int HEALING_AMOUNT = 4;
    private static final int COOLDOWN_TICKS = 40;

    public BandageItem(Properties properties) {
        super(properties, USE_DURATION, COOLDOWN_TICKS);
    }

    @Override
    protected void applyEffect(Player player, Level level) {
        // 回复4点生命值
        player.heal(HEALING_AMOUNT);
        
        // 显示成功使用消息
        player.displayClientMessage(Component.translatable("item.battleroyale.bandage.use_success")
                .withStyle(ChatFormatting.GREEN), true);
    }

    @Override
    protected String getFailTranslationKey() {
        return "item.battleroyale.bandage.ues_fail";
    }

    @Override
    protected String getTooltipTranslationKey() {
        return "item.battleroyale.bandage.tooltip";
    }

    @Override
    protected String getUseTooltipTranslationKey() {
        return "item.battleroyale.bandage.tooltip.use";
    }
}
