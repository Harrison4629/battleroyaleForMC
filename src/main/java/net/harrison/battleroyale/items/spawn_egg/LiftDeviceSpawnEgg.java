package net.harrison.battleroyale.items.spawn_egg;

import net.harrison.battleroyale.entities.liftdevice.LiftDeviceEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class LiftDeviceSpawnEgg extends Item {
    private final Supplier<EntityType<?>> typeSupplier;

    public LiftDeviceSpawnEgg(RegistryObject<EntityType<LiftDeviceEntity>> type, Properties props) {
        super(props);
        this.typeSupplier = type::get;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }

        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        // 在点击位置上方生成实体
        BlockPos spawnPos = pos.above();
        EntityType<?> entityType = typeSupplier.get();

        Entity entity = entityType.create(level);
        if (entity != null) {
            entity.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
            level.addFreshEntity(entity);

            // 在创造模式下不消耗物品
            if (player == null || !player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag isAdvanced) {
        super.appendHoverText(stack, level, tooltip, isAdvanced);
        tooltip.add(Component.translatable("item.battleroyale.lift_device_spawn_egg.tooltip")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.battleroyale.lift_device_spawn_egg.tooltip.use")
                .withStyle(ChatFormatting.BLUE));
    }
}
