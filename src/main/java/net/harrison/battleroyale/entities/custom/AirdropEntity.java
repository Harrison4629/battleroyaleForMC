package net.harrison.battleroyale.entities.custom;

import net.harrison.battleroyale.Battleroyale;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;


public class AirdropEntity extends Entity implements Container, MenuProvider{


    private static final EntityDataAccessor<Boolean> HAS_LANDED = SynchedEntityData.defineId(AirdropEntity.class, EntityDataSerializers.BOOLEAN);
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);

    private static final double FALL_SPEED = -0.05D; // 负值表示向下，可以调整这个值来控制速度
    private static final double TERMINAL_VELOCITY = -0.5D; // 终端速度，防止无限加速


    public AirdropEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.blocksBuilding = true; // 实体是否阻挡建造，设置为true可能更符合空投的物理特性
        this.noPhysics = false; // 确保实体有物理行为
    }

    // 获取空投是否已落地状态的方法
    public boolean hasLanded() {
        return this.entityData.get(HAS_LANDED);
    }


    // 设置空投落地状态的方法
    public void setHasLanded(boolean landed) {
        this.entityData.set(HAS_LANDED, landed);
    }


    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);

            NetworkHooks.openScreen((net.minecraft.server.level.ServerPlayer) player,this,
                    buf -> buf.writeInt(this.getId()));


            return InteractionResult.SUCCESS;

        }
        return InteractionResult.PASS;
    }



    @Override
    public void tick() {
        super.tick();

        // 客户端粒子效果
        if (this.level.isClientSide) {
            if (!hasLanded()) {
                this.level.addParticle(ParticleTypes.CLOUD, this.getX() + this.random.nextDouble() * 0.5D - 0.25D, this.getY() + this.random.nextDouble() * 0.5D, this.getZ() + this.random.nextDouble() * 0.5D - 0.25D, 0.0D, 0.0D, 0.0D);
            }
        }

        // 核心逻辑：无论是下落中还是已落地，都需要持续检查是否在地面上
        // 关键点：不再依赖 hasLanded() 来完全阻止下落逻辑的执行
        Vec3 motion = this.getDeltaMovement();

        // 如果不在地面上，并且还没有达到终端速度，就继续下落
        if (!this.onGround) { // 检查是否在地面上，注意这里是 `this.onGround()` 而不是 `this.onGround`
            // 如果之前是落地状态，现在又不在地面上了，说明失去了支撑，重新开始下落
            if (hasLanded()) {
                setHasLanded(false); // 重置落地状态
                // 此时可以播放一些重新开始下落的音效或效果
            }

            // 增加向下的速度（重力模拟）
            motion = motion.add(0.0D, FALL_SPEED, 0.0D);

            // 限制下落速度，防止过快
            if (motion.y < TERMINAL_VELOCITY) {
                motion = new Vec3(motion.x, TERMINAL_VELOCITY, motion.z);
            }

        } else { // 如果在地面上
            // 如果之前不是落地状态，现在在地面上了，说明刚刚落地
            if (!hasLanded()) {
                this.setHasLanded(true); // 设置空投已落地
                // 可以在这里添加一些落地时的效果，比如音效或爆炸粒子
                if (!this.level.isClientSide) {
                    // 服务器端处理落地事件，例如生成拾取物或播放音效
                    // this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 1.0F, 1.0F);
                    // 可以生成一个箱子或掉落物
                    // this.level().setBlockAndUpdate(this.blockPosition(), Blocks.CHEST.defaultBlockState());
                    // this.remove(RemovalReason.DISCARDED);
                }
            }
            // 落地后速度归零
            motion = Vec3.ZERO;
        }

        this.setDeltaMovement(motion); // 设置新的速度
        this.move(MoverType.SELF, this.getDeltaMovement()); // 实际移动实体并处理碰撞

        // 注意：如果你希望它在落地后完全不动，即使受到推动也不动，可以考虑在这里添加额外的速度归零逻辑
        // 但通常实体在落地后仍可能受到一些微小的物理影响，这是正常的Minecraft行为。
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {

        return ContainerHelper.removeItem(this.items, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {

        ItemStack itemstack = this.items.get(index);
        if (itemstack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.items.set(index, ItemStack.EMPTY);
            return itemstack;
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {

        this.items.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public void setChanged() {

    }

    @Override
    public boolean stillValid(Player player) {
        return this.isAlive() && player.distanceToSqr(this) <= 64.0D;
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("entity." + Battleroyale.MODID + ".airdrop");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {

        return ChestMenu.threeRows(id,playerInventory,this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    // 实现Container接口方法
    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public boolean canCollideWith(Entity pEntity) {
        return true;
    }


    @Override
    public boolean hurt(DamageSource source, float pAmount) {
        if (source.is(DamageTypeTags.IS_PROJECTILE) && !this.level.isClientSide) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypeTags.IS_PROJECTILE) ||
                source.is(DamageTypeTags.IS_EXPLOSION) ||
                source.is(DamageTypeTags.IS_FIRE) ||
                source.is(DamageTypeTags.IS_FALL) ||
                source.is(DamageTypeTags.BYPASSES_ARMOR) ||
                super.isInvulnerableTo(source);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HAS_LANDED, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        // 从NBT中读取数据，当世界加载时调用
        this.setHasLanded(pCompound.getBoolean("HasLanded"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        // 将数据写入NBT，当世界保存时调用
        pCompound.putBoolean("HasLanded", this.hasLanded());
    }
}

