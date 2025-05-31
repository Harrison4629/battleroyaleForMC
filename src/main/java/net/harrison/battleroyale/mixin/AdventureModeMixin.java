package net.harrison.battleroyale.mixin;

import net.harrison.battleroyale.items.spawn_egg.LiftDeviceSpawnEgg;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class AdventureModeMixin {
    @Shadow
    private GameType gameModeForPlayer;

    @Shadow
    protected ServerPlayer player;

    /**
     * 直接在useItemOn方法中处理我们的刷怪蛋
     * 这是最直接的方法，绕过所有权限检查
     */
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void handleSpawnEggUse(ServerPlayer serverPlayer, Level level, ItemStack itemStack,
                                   net.minecraft.world.InteractionHand hand, BlockHitResult blockHitResult,
                                   CallbackInfoReturnable<InteractionResult> cir) {

        // 只处理我们的刷怪蛋，且玩家在冒险模式
        if (itemStack.getItem() instanceof LiftDeviceSpawnEgg &&
                this.gameModeForPlayer == GameType.ADVENTURE) {

            // 直接调用物品的useOn方法，完全绕过游戏模式检查
            UseOnContext useOnContext =
                    new UseOnContext(serverPlayer, hand, blockHitResult);

            InteractionResult result = itemStack.getItem().useOn(useOnContext);
            cir.setReturnValue(result);
        }
    }
}
