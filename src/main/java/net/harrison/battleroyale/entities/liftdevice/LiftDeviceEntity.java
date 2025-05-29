package net.harrison.battleroyale.entities.liftdevice;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class LiftDeviceEntity extends Entity {
    public LiftDeviceEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
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

    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }
}
