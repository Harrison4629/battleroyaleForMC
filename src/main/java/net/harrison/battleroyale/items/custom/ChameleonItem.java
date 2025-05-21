package net.harrison.battleroyale.items.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChameleonItem extends Item {
    private static final int USE_DURATION = 10;
    private static final int INVISIBILITY_DURATION = 100; // 持续5秒
    
    // 存储隐身玩家及其隐身结束时间
    public static final Map<UUID, Long> INVISIBLE_PLAYERS = new ConcurrentHashMap<>();

    public ChameleonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        
        // 播放使用声音
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PHANTOM_FLAP, SoundSource.PLAYERS, 0.8F, 1.0F);
        
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
                    player.displayClientMessage(Component.translatable("item.battleroyale.chameleon.heal_fail")
                            .withStyle(ChatFormatting.RED), true);
                }
            }
        }
    }
    
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            
            if (!level.isClientSide) {
                // 添加隐形效果
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, INVISIBILITY_DURATION, 0, false, false));
                
                // 记录玩家隐身状态及结束时间
                INVISIBLE_PLAYERS.put(player.getUUID(), System.currentTimeMillis() + (INVISIBILITY_DURATION * 50)); // 转换为毫秒
                
                // 显示成功使用消息
                player.displayClientMessage(Component.translatable("item.battleroyale.chameleon.heal_success")
                        .withStyle(ChatFormatting.GREEN), true);

                // 非创造模式消耗物品
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                //冷却时间
                player.getCooldowns().addCooldown(this, 140); // 7秒冷却
            }
            
            // 在玩家周围产生药剂粒子效果
            if (level.isClientSide) {
                Vec3 position = player.position();
                for (int i = 0; i < 16; i++) {
                    double xOffset = (level.random.nextDouble() - 0.5) * 1.5;
                    double yOffset = level.random.nextDouble() * 2.0;
                    double zOffset = (level.random.nextDouble() - 0.5) * 1.5;
                    
                    level.addParticle(
                            net.minecraft.core.particles.ParticleTypes.SNOWFLAKE,
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
        return UseAnim.BRUSH; // 使用扫动动画
    }
    
    @Override
    public int getUseDuration(ItemStack stack) {
        return USE_DURATION; // 设置为使用时间
    }
    
    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.battleroyale.chameleon.tooltip")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.battleroyale.chameleon.tooltip.use")
                .withStyle(ChatFormatting.BLUE));
    }
}
