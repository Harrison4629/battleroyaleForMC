package net.harrison.battleroyale.entities.liftdevice;

import net.harrison.battleroyale.event.FallDamageEvent;
import net.harrison.battleroyale.networking.ModMessages;
import net.harrison.battleroyale.networking.packet.LiftS2CPacket;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class LiftDeviceEntity extends Entity {
    private static final EntityDataAccessor<Float> HEALTH = SynchedEntityData.defineId(LiftDeviceEntity.class, EntityDataSerializers.FLOAT);
    private static final float MAX_HEALTH = 20.0F;
    private static final int LAST_TIME = 200;

    public LiftDeviceEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setHealth(MAX_HEALTH);
    }


    @Override
    public void tick() {
        super.tick();

        if (this.tickCount >= LAST_TIME) {
            this.remove(RemovalReason.KILLED);
            return;
        }

        // 客户端粒子效果
        if (this.level.isClientSide) {
            this.level.addParticle(ParticleTypes.SMOKE, this.getX() + this.random.nextDouble() * 0.5D - 0.15D,
                    this.getY() +0.3D,
                    this.getZ() + this.random.nextDouble() * 0.5D - 0.15D,
                    0.0D, 0.5D, 0.0D);
        }

        if (this.level instanceof ServerLevel level) {
            for (ServerPlayer player : level.players())
            {

                Vec3 distance = player.position().vectorTo(this.position());
                Vec3 verdistance = new Vec3(distance.x, 0, distance.z);


                if (distance.y>=-5 && distance.y<=0 && verdistance.length()<=0.8 && player.getDeltaMovement().y < 1.2) {
                    Vec3 speed = player.getDeltaMovement();
                    Vec3 delta = new Vec3(1.8 * speed.x, 1.2, 1.8 * speed.z);
                    player.setDeltaMovement(delta);
                    ModMessages.sendToPlayer(new LiftS2CPacket(delta), player);
                    FallDamageEvent.setFallDamageImmunity(player.getUUID(), true);

                }
            }
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
