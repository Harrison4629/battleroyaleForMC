package net.harrison.battleroyale.items.custom.armorplate;

import net.harrison.battleroyale.items.custom.AbstractUsableItem;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class ArmorPlateItem3 extends AbstractUsableItem {
    private static final int USE_DURATION = 30;
    private static final int ABSORPTION_VALUE = 3;
    private static final int COOLDOWN_TICKS = 40;

    public ArmorPlateItem3(Properties properties) {
        super(properties, USE_DURATION, COOLDOWN_TICKS);
    }

    @Override
    protected void applyEffect(Player player, Level level) {
        player.removeEffect(MobEffects.ABSORPTION);
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, MobEffectInstance.INFINITE_DURATION, ABSORPTION_VALUE, false, false));

    }

    @Override
    protected String getFailTranslationKey() {
        return "item.battleroyale.armor_plate_3.use_fail";
    }

    @Override
    protected String getTooltipTranslationKey() {
        return "item.battleroyale.armor_plate_3.tooltip";
    }

    @Override
    protected String getUseTooltipTranslationKey() {
        return "item.battleroyale.armor_plate_3.tooltip.use";
    }

    @Override
    public SoundEvent getUsingSound() {
        return SoundEvents.STONE_PLACE;
    }

    @Override
    protected SoundEvent getFinishSound() {
        return SoundEvents.ARMOR_EQUIP_DIAMOND;
    }

    @Override
    protected UseAnim getItemUseAnimation() {
        return UseAnim.BRUSH;
    }

    @Override
    protected ParticleOptions getParticleType() {
        return ParticleTypes.ELECTRIC_SPARK;
    }
}
