package net.harrison.battleroyale.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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

import javax.annotation.Nullable;
import java.util.List;

public class ArmorPlateItem1 extends Item {
    private static final int USE_DURATION = 30;
    private static final int ARMOR_VALUE = 8;

    public ArmorPlateItem1(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        // 播放使用声音
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WOOL_PLACE, SoundSource.PLAYERS, 0.8F, 1.0F);
        
        // 开始使用物品
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemStack);
    }
    
    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            int timeUsed = getUseDuration(stack) - timeLeft;
            
            if (timeUsed < USE_DURATION) {
                // 如果使用时间不够，则取消使用
                if (!level.isClientSide) {
                    player.displayClientMessage(Component.translatable("item.battleroyale.armor_plate_1.heal_fail")
                            .withStyle(ChatFormatting.RED), true);
                }
            }
        }
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            
            // 给玩家“护甲”值
            if (!level.isClientSide) {
                // 增加8点护甲
                player.setAbsorptionAmount(ARMOR_VALUE);

                // 播放装备声音
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ARMOR_EQUIP_CHAIN, SoundSource.PLAYERS, 0.5F, 1.0F);
                
                // 非创造模式消耗物品
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                //冷却时间
                player.getCooldowns().addCooldown(this, 80);
            }
            
            // 在玩家周围产生恢复粒子效果
            if (level.isClientSide) {
                Vec3 position = player.position();
                for (int i = 0; i < 16; i++) {
                    double xOffset = (level.random.nextDouble() - 0.5) * 1.5;
                    double yOffset = level.random.nextDouble() * 2.0;
                    double zOffset = (level.random.nextDouble() - 0.5) * 1.5;
                    
                    level.addParticle(
                            net.minecraft.core.particles.ParticleTypes.CRIT,
                            position.x + xOffset,
                            position.y + yOffset,
                            position.z + zOffset,
                            0, 0.1, 0
                    );
                }
            }
        }
        
        return stack;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BRUSH; // 使用动画
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION;
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.battleroyale.armor_plate_1.tooltip")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.battleroyale.armor_plate_1.tooltip.use")
                .withStyle(ChatFormatting.BLUE));
    }
}
