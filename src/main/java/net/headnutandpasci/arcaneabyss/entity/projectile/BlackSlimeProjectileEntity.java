package net.headnutandpasci.arcaneabyss.entity.projectile;

import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BlackSlimeProjectileEntity extends ExplosiveProjectileEntity {

    @Nullable
    private final BlackSlimeEntity slime;

    public BlackSlimeProjectileEntity(EntityType<? extends BlackSlimeProjectileEntity> entityType, World world) {
        super(entityType, world);
        this.slime = null;
    }

    public BlackSlimeProjectileEntity(World world, LivingEntity owner, double directionX, double directionY, double directionZ) {
        super(ModEntities.BLACK_SLIME_PROJECTILE, owner, directionX, directionY, directionZ, world);
        this.slime = (BlackSlimeEntity) owner;
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

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();

        if (this.slime == null) {
            entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 8.0f);
            return;
        }
        int damage;

        if (entity instanceof PlayerEntity player && player.isBlocking()) {
            player.disableShield(true);
            damage = (int) (this.slime.getAttackDamage() * 1F);
        } else {
            damage = (int) (this.slime.getAttackDamage() * 2F);
        }

        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), damage);
        entity.setFireTicks(100);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) {
            this.getWorld().createExplosion(this, this.getX(), this.getY(), this.getZ(), 2F, false, World.ExplosionSourceType.MOB);
            this.getWorld().sendEntityStatus(this, (byte) 3);
            this.discard();
        }

    }

    @Override
    public boolean isOnFire() {
        return false;
    }
}

