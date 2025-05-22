package net.harrison.battleroyale.entities;

import net.harrison.battleroyale.Battleroyale;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class AirdropEntity extends Entity implements Container, MenuProvider {
    private static final EntityDataAccessor<Boolean> OPENED = SynchedEntityData.defineId(AirdropEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANIMATION_TIME = SynchedEntityData.defineId(AirdropEntity.class, EntityDataSerializers.INT);

    // 掉落速度
    private static final double FALL_SPEED = 0.05D;
    // 是否已经着陆
    private boolean hasLanded = false;
    
    // 空投战利品表
    public static final ResourceLocation AIRDROP_LOOT_TABLE = new ResourceLocation(Battleroyale.MODID, "chests/airdrop");
    
    // 容器相关
    private NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);
    private ResourceLocation lootTable;
    private long lootTableSeed;

    public AirdropEntity(EntityType<? extends AirdropEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
        this.noPhysics = false; // 确保应用物理碰撞
        this.setBoundingBox(this.makeBoundingBox()); // 确保边界盒正确设置
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OPENED, false);
        this.entityData.define(ANIMATION_TIME, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        this.setOpened(compound.getBoolean("Opened"));
        this.setAnimationTime(compound.getInt("AnimationTime"));
        this.hasLanded = compound.getBoolean("HasLanded");
        
        if (compound.contains("LootTable", 8)) {
            this.lootTable = new ResourceLocation(compound.getString("LootTable"));
            this.lootTableSeed = compound.getLong("LootTableSeed");
        } else {
            this.lootTable = null;
            ContainerHelper.loadAllItems(compound, this.items);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("Opened", this.isOpened());
        compound.putInt("AnimationTime", this.getAnimationTime());
        compound.putBoolean("HasLanded", this.hasLanded);
        
        if (this.lootTable != null) {
            compound.putString("LootTable", this.lootTable.toString());
            compound.putLong("LootTableSeed", this.lootTableSeed);
        } else {
            ContainerHelper.saveAllItems(compound, this.items);
        }
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();

        // 掉落逻辑
        if (!this.hasLanded) {
            // 检查下方是否有方块
            if (this.verticalCollision) {
                this.hasLanded = true;
                // 播放着陆音效
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.WOOD_FALL, SoundSource.BLOCKS, 1.0F, 1.0F);

                // 初始化战利品表
                if (this.lootTable == null) {
                    this.setLootTable(AIRDROP_LOOT_TABLE, this.random.nextLong());
                }
            } else {
                // 下落
                this.setDeltaMovement(0, -FALL_SPEED, 0);
                this.move(MoverType.SELF, this.getDeltaMovement());
            }
        }

        // 动画逻辑
        if (this.isOpened() && this.getAnimationTime() < 20) {
            this.setAnimationTime(this.getAnimationTime() + 1);
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (!this.level.isClientSide && this.hasLanded) {
            if (!this.isOpened()) {
                this.setOpened(true);
                // 播放开箱音效
                this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            
            // 打开容器界面
            NetworkHooks.openScreen((net.minecraft.server.level.ServerPlayer) player, this, 
                    buf -> buf.writeInt(this.getId()));
            
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 空投只能被创造模式玩家破坏
        if (source.getEntity() instanceof Player && ((Player) source.getEntity()).isCreative()) {
            this.remove(RemovalReason.KILLED);
            return true;
        }
        
        // 当投掷物击中时，将其反弹或消除，但不造成伤害
        // 播放反弹音效
        if (source.is(DamageTypeTags.IS_PROJECTILE) && !this.level.isClientSide) {
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.SHIELD_BLOCK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }


        // 对于爆炸伤害，也不造成伤害
        return false;
    }

    public boolean isOpened() {
        return this.entityData.get(OPENED);
    }

    public void setOpened(boolean opened) {
        this.entityData.set(OPENED, opened);
    }

    public int getAnimationTime() {
        return this.entityData.get(ANIMATION_TIME);
    }

    public void setAnimationTime(int time) {
        this.entityData.set(ANIMATION_TIME, time);
    }

    public boolean hasLanded() {
        return this.hasLanded;
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        // 确保玩家和其他实体无法穿过空投
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        // 使空投可以被碰撞
        return true;
    }
    
    @Override
    public boolean isPickable() {
        // 确保实体可以被弓箭等投掷物命中
        return true;
    }
    
    @Override
    public boolean isPushable() {
        // 防止被活塞推动
        return false;
    }
    
    @Override
    public boolean isPushedByFluid() {
        // 防止被流体推动
        return false;
    }
    
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        // 空投不会被以下伤害源损坏
        return source.is(DamageTypeTags.IS_PROJECTILE) ||
               source.is(DamageTypeTags.IS_EXPLOSION) ||
               source.is(DamageTypeTags.IS_FIRE) ||
               source.is(DamageTypeTags.IS_FALL) ||
               source.is(DamageTypeTags.BYPASSES_ARMOR) ||
               super.isInvulnerableTo(source);
    }

    @Override
    public float getStepHeight() {
        // 设置台阶高度，使实体能够越过小障碍
        return 1.0F;
    }

    @Override
    protected float getEyeHeight(Pose pose, EntityDimensions dimensions) {
        // 设置实体的眼睛高度
        return dimensions.height * 0.85F;
    }

    @Override
    public double getPassengersRidingOffset() {
        // 如果有实体骑乘空投，设置它们的垂直偏移量
        return 1.5D;
    }
    
    // 实现Container接口方法
    @Override
    public int getContainerSize() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int index) {
        this.unpackLootTable(null);
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int index, int count) {
        this.unpackLootTable(null);
        return ContainerHelper.removeItem(this.items, index, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        this.unpackLootTable(null);
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
        this.unpackLootTable(null);
        this.items.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public void setChanged() {
        // 容器内容改变时调用
    }

    @Override
    public boolean stillValid(Player player) {
        return this.isAlive() && player.distanceToSqr(this) <= 64.0D;
    }

    @Override
    public void clearContent() {
        this.unpackLootTable(null);
        this.items.clear();
    }
    
    // 实现MenuProvider接口方法
    @Override
    public Component getDisplayName() {
        return Component.translatable("entity." + Battleroyale.MODID + ".airdrop");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        this.unpackLootTable(player);
        return ChestMenu.threeRows(id, playerInventory, this);
    }
    
    // 战利品表相关方法
    public void setLootTable(ResourceLocation lootTableId, long seed) {
        this.lootTable = lootTableId;
        this.lootTableSeed = seed;
    }
    
    public void unpackLootTable(@Nullable Player player) {
        if (this.lootTable != null && this.level.getServer() != null) {
            LootTable loottable = this.level.getServer().getLootTables().get(this.lootTable);
            this.lootTable = null;
            
            LootContext.Builder builder = new LootContext.Builder((net.minecraft.server.level.ServerLevel)this.level)
                    .withParameter(LootContextParams.ORIGIN, this.position());
            
            if (player != null) {
                builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
            }
            
            loottable.fill(this, builder.create(LootContextParamSets.CHEST));
        }
    }
}

