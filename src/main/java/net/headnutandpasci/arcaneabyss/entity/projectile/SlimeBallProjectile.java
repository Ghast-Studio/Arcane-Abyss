package net.headnutandpasci.arcaneabyss.entity.projectile;

import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SlimeBallProjectile extends ExplosiveProjectileEntity implements FlyingItemEntity {
    public SlimeBallProjectile(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public SlimeBallProjectile(LivingEntity owner, World world, Vec3d pos) {
        super(ModEntities.SLIME_BALL_PROJECTILE, world);
        this.setOwner(owner);
        this.updatePosition(pos.x, pos.y, pos.z);
    }

    @Override
    public void tick() {
        super.tick();

        if(this.age % 5 == 0) {
            if(this.getWorld() instanceof ServerWorld serverWorld)
                serverWorld.addParticle(ParticleTypes.ITEM_SLIME, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }

        if (this.age >= 100) {
            this.discard();
        }
    }

    private ParticleEffect getParticleParameters() {
        return ParticleTypes.ITEM_SLIME;
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

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 4.0f);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().sendEntityStatus(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    public boolean isOnFire() {
        return false;
    }

    @Override
    public ItemStack getStack() {
        return Items.SLIME_BALL.getDefaultStack();
    }
}


