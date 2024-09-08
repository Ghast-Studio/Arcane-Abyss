package net.headnutandpasci.arcaneabyss.entity.projectile;

import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SlimeviathanProjectileEntity extends ExplosiveProjectileEntity {

    public SlimeviathanProjectileEntity(EntityType<? extends SlimeviathanProjectileEntity> entityType, World world) {
        super((EntityType<? extends SlimeviathanProjectileEntity>)entityType, world);
    }

    public SlimeviathanProjectileEntity(World world, LivingEntity owner, double directionX, double directionY, double directionZ) {
        super(ModEntities.SLIMEVIATHAN_PROJECTLE, owner, directionX, directionY, directionZ, world);
    }




    private ParticleEffect getParticleParameters() {
        return ParticleTypes.ASH;
    }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 3) {
            ParticleEffect particleEffect = this.getParticleParameters();

            for (int i = 0; i < 8; ++i) {
                this.getWorld().addParticle(particleEffect, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }


    public boolean collidesWith(Entity entity) {

        if (entity instanceof SlimeviathanProjectileEntity) {
            return false;
        }
        return super.collidesWith(entity);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();


        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 13.0f);



    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 5F, false, World.ExplosionSourceType.MOB);
            this.getWorld().sendEntityStatus(this, (byte) 3);
            this.discard();
        }

    }
    public void setSlowVelocity(Vec3d velocity) {
        this.setVelocity(velocity.multiply(0.5));
    }

    @Override
    public boolean isOnFire() {
        return false;
    }
}

