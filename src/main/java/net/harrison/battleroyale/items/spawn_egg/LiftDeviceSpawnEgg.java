package net.harrison.battleroyale.items.spawn_egg;

import net.harrison.battleroyale.entities.liftdevice.LiftDeviceEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.RegistryObject;
import java.util.function.Supplier;

public class LiftDeviceSpawnEgg extends Item {
    private final Supplier<EntityType<?>> typeSupplier;
    private final int backgroundColor;
    private final int highlightColor;

    public LiftDeviceSpawnEgg(RegistryObject<EntityType<LiftDeviceEntity>> type, int backgroundColor, int highlightColor, Properties props) {
        super(props);
        this.typeSupplier = type::get;
        this.backgroundColor = backgroundColor;
        this.highlightColor = highlightColor;
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

    //@Override
    //public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    //    ItemStack stack = player.getItemInHand(hand);
//
    //    // 在玩家看向的位置生成实体
    //    BlockHitResult hitResult = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
    //    if (hitResult.getType() != HitResult.Type.BLOCK) {
    //        return InteractionResultHolder.pass(stack);
    //    }
//
    //    if (!(level instanceof ServerLevel)) {
    //        return InteractionResultHolder.success(stack);
    //    }
//
    //    if (hitResult.getType() == HitResult.Type.BLOCK) {
    //        UseOnContext context = new UseOnContext(player, hand, hitResult);
    //        InteractionResult result = this.useOn(context);
    //        return new InteractionResultHolder<>(result, stack);
    //    }
//
    //    return InteractionResultHolder.pass(stack);
    //}

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getHighlightColor() {
        return highlightColor;
    }
}
