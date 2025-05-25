package net.harrison.battleroyale.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 可使用物品的抽象基类
 * 处理通用的使用逻辑，如使用时间、动画、声音和粒子效果
 */
public abstract class AbstractUsableItem extends Item {
    private final int useDuration;
    private final int cooldownTicks;
    
    public AbstractUsableItem(Properties properties, int useDuration, int cooldownTicks) {
        super(properties);
        this.useDuration = useDuration;
        this.cooldownTicks = cooldownTicks;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // 开始使用物品
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }
    
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int timeUsed = getUseDuration(stack) - timeLeft;
            
            if (timeUsed < useDuration) {
                // 如果使用时间不够，则取消使用
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable(getFailTranslationKey())
                            .withStyle(ChatFormatting.RED), true);
                }

            }
        }
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {

            // 在服务器端播放音效
            if (!level.isClientSide) {
                // 应用物品效果
                applyEffect(player, level);

                // 播放完成使用声音
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        getFinishSound(), SoundSource.PLAYERS, volume(), pitch());

                // 非创造模式消耗物品
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                // 添加冷却时间
                player.getCooldowns().addCooldown(this, cooldownTicks);
            }
            
            // 在客户端产生粒子效果
            if (level.isClientSide) {
                spawnParticles(player, level);


            }
        }
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return getItemUseAnimation();
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return useDuration;
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable(getTooltipTranslationKey())
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable(getUseTooltipTranslationKey())
                .withStyle(ChatFormatting.BLUE));
    }


    //应用物品效果到玩家身上
    protected abstract void applyEffect(Player player, Level level);

    //获取物品使用失败时的翻译键
    protected abstract String getFailTranslationKey();

    //获取物品提示文本的翻译键
    protected abstract String getTooltipTranslationKey();

    //获取物品使用提示的翻译键
    protected abstract String getUseTooltipTranslationKey();


    
    //使用物品时和使用物品后的音量
    public float volume() {
        return 1F;
    }

    public float pitch() {
        return 1F;
    }

    //获取物品使用时的动画
    protected UseAnim getItemUseAnimation() {
        return UseAnim.BOW;
    }


    //获取使用物品过程中播放的声音
    public SoundEvent getUsingSound() {
        return SoundEvents.WOOL_PLACE;
    }
    
    //获取完成使用物品时播放的声音
    protected SoundEvent getFinishSound() {
        return SoundEvents.EXPERIENCE_ORB_PICKUP;
    }

    //获取使用物品时产生的粒子类型
    protected ParticleOptions getParticleType() {
        return ParticleTypes.HEART;
    }
    
    //产生粒子效果
    protected void spawnParticles(Player player, Level level) {
        Vec3 position = player.position();
        for (int i = 0; i < 16; i++) {
            double xOffset = (level.random.nextDouble() - 0.5) * 1.5;
            double yOffset = level.random.nextDouble() * 2.0;
            double zOffset = (level.random.nextDouble() - 0.5) * 1.5;
            
            level.addParticle(
                    getParticleType(),
                    position.x + xOffset,
                    position.y + yOffset,
                    position.z + zOffset,
                    0, 0.1, 0
            );
        }
    }
}
