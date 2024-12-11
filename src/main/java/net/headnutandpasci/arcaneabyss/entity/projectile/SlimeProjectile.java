package net.headnutandpasci.arcaneabyss.entity.projectile;

import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
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
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class SlimeProjectile extends ExplosiveProjectileEntity implements FlyingItemEntity {
    private static final TrackedData<ItemStack> ITEM;

    static {
        ITEM = DataTracker.registerData(SlimeProjectile.class, TrackedDataHandlerRegistry.ITEM_STACK);
    }

    public SlimeProjectile(EntityType<? extends ExplosiveProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    public SlimeProjectile(World world, LivingEntity owner) {
        super(ModEntities.SLIME_PROJECTILE, owner, 0, 0, 0, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(ITEM, Items.SLIME_BALL.getDefaultStack());
    }


    @Override
    public void tick() {
        super.tick();

        if (this.age >= 40) {
            this.discard();
        }
    }

    @Override
    public void handleStatus(byte status) {
        if (status == 3) {
            for (int i = 0; i < 8; ++i) {
                this.getWorld().addParticle(this.getParticleType(), this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() == this.getOwner())
            return;

        if (entityHitResult.getEntity() instanceof ArcaneSlimeEntity)
            return;

        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 4.0f);

        if (this.isBurning()) {
            entity.setOnFireFor(2);
        }
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
    protected ParticleEffect getParticleType() {
        return this.getStack().isOf(Items.MAGMA_CREAM) ? ParticleTypes.FLAME : ParticleTypes.ITEM_SLIME;
    }

    @Override
    protected boolean isBurning() {
        return this.getStack().isOf(Items.MAGMA_CREAM);
    }

    @Override
    public ItemStack getStack() {
        return this.getDataTracker().get(ITEM);
    }

    public void setItem(ItemStack stack) {
        this.getDataTracker().set(ITEM, stack);
    }
}


