package net.harrison.battleroyale.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

public class AirdropEntity extends Entity {
    private static final EntityDataAccessor<Boolean> OPENED = SynchedEntityData.defineId(AirdropEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> ANIMATION_TIME = SynchedEntityData.defineId(AirdropEntity.class, EntityDataSerializers.INT);
    
    // 掉落速度
    private static final double FALL_SPEED = 0.05D;
    // 是否已经着陆
    private boolean hasLanded = false;

    public AirdropEntity(EntityType<? extends AirdropEntity> entityType, Level level) {
        super(entityType, level);
        this.blocksBuilding = true;
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
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        compound.putBoolean("Opened", this.isOpened());
        compound.putInt("AnimationTime", this.getAnimationTime());
        compound.putBoolean("HasLanded", this.hasLanded);
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
                        SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
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
        if (!this.level.isClientSide && !this.isOpened() && this.hasLanded) {
            this.setOpened(true);
            // 播放开箱音效
            this.level.playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            
            // 这里可以生成物品掉落
            // TODO: 实现物品掉落逻辑
            
            return InteractionResult.SUCCESS;
        }
        
        return InteractionResult.PASS;
    }
    
    @Override
    public boolean hurt(DamageSource source, float amount) {
        // 空投不能被普通方式破坏
        if (source.getEntity() instanceof Player && ((Player) source.getEntity()).isCreative()) {
            this.remove(RemovalReason.KILLED);
            return true;
        }
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
}
