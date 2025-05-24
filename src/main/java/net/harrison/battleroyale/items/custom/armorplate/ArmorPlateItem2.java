package net.harrison.battleroyale.items.custom.armorplate;

import net.harrison.battleroyale.items.custom.AbstractUsableItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class ArmorPlateItem2 extends AbstractUsableItem {
    private static final int USE_DURATION = 25;
    private static final int ABSORPTION_VALUE = 2;
    private static final int COOLDOWN_TICKS = 40;

    public ArmorPlateItem2(Properties properties) {
        super(properties, USE_DURATION, COOLDOWN_TICKS);
    }

    @Override
    protected void applyEffect(Player player, Level level) {
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, MobEffectInstance.INFINITE_DURATION, ABSORPTION_VALUE, false, false));

    }

    @Override
    protected String getFailTranslationKey() {
        return "item.battleroyale.armor_plate_2.ues_fail";
    }

    @Override
    protected String getTooltipTranslationKey() {
        return "item.battleroyale.armor_plate_2.tooltip";
    }

    @Override
    protected String getUseTooltipTranslationKey() {
        return "item.battleroyale.armor_plate_2.tooltip.use";
    }

    @Override
    public SoundEvent getUsingSound() {
        return SoundEvents.STONE_PLACE;
    }

    @Override
    protected SoundEvent getFinishSound() {
        return SoundEvents.ARMOR_EQUIP_IRON;
    }

    @Override
    protected UseAnim getItemUseAnimation() {
        return UseAnim.BRUSH;
    }
}
