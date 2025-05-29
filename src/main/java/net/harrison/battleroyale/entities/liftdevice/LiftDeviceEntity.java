package net.harrison.battleroyale.entities.liftdevice;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LiftDeviceEntity extends Entity {
    private static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(LiftDeviceEntity.class, EntityDataSerializers.FLOAT);
    private static final float MAX_HEALTH = 40.0F;

    public LiftDeviceEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setHealth(MAX_HEALTH);
    }


    @Override
    public void tick() {
        super.tick();

        // 客户端粒子效果
        if (this.level.isClientSide) {
            this.level.addParticle(ParticleTypes.CLOUD, this.getX() + this.random.nextDouble() * 0.3D - 0.15D,
                    this.getY() +0.6D + this.random.nextDouble() * 0.3D,
                    this.getZ() + this.random.nextDouble() * 0.3D - 0.15D,
                    0.0D, 0.01D, 0.0D);
            this.level.addParticle(ParticleTypes.CLOUD, this.getX() + this.random.nextDouble() * 0.3D - 0.15D,
                    this.getY() +1.1D + this.random.nextDouble() * 0.2D,
                    this.getZ() + this.random.nextDouble() * 0.3D - 0.15D,
                    0.0D, 0.05D, 0.0D);
            this.level.addParticle(ParticleTypes.CLOUD, this.getX() + this.random.nextDouble() * 0.2D - 0.15D,
                    this.getY() +1.6D + this.random.nextDouble() * 0.3D,
                    this.getZ() + this.random.nextDouble() * 0.2D - 0.15D,
                    0.0D, 0.1D, 0.0D);

        }
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
    public boolean canCollideWith(Entity entity) {
        return true;
    }

    public float getHealth() {
        return this.entityData.get(HEALTH);
    }

    public void setHealth(float health) {
        this.entityData.set(HEALTH, Math.max(0.0F, Math.min(health, MAX_HEALTH)));
        if (health <= 0.0F && !this.isRemoved()) {
            this.remove(RemovalReason.KILLED);
        }
    }

    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }

        float currentHealth = this.getHealth();
        this.setHealth(currentHealth - amount);
        return true;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(HEALTH, MAX_HEALTH);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.contains("Health")) {
            this.setHealth(pCompound.getFloat("Health"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        pCompound.putFloat("Health", this.getHealth());
    }
}
