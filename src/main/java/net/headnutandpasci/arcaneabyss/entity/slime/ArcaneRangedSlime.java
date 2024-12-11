package net.headnutandpasci.arcaneabyss.entity.slime;

import net.headnutandpasci.arcaneabyss.entity.projectile.SlimeProjectile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import net.headnutandpasci.arcaneabyss.util.Util;

import java.util.EnumSet;

public abstract class ArcaneRangedSlime extends ArcaneSlimeEntity implements RangedAttackMob {

    public ArcaneRangedSlime(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public ArcaneRangedSlime(EntityType<? extends HostileEntity> entityType, World world, int size) {
        super(entityType, world, size);
    }

    public void attack(LivingEntity target, float pullProgress) {
        Vec3d forward = this.getRotationVector().multiply(this.getForwardDistance());

        double startX = this.getX() + forward.x;
        double startY = this.getBodyY(0.3);
        double startZ = this.getZ() + forward.z;

        double velocityX = target.getX() - startX;
        double velocityY = target.getBodyY(0.3) - startY;
        double velocityZ = target.getZ() - startZ;

        SlimeProjectile projectile = new SlimeProjectile(this.getWorld(), this);
        projectile.setPos(startX, startY, startZ);
        projectile.setVelocity(velocityX, velocityY, velocityZ, 1.6F, (float) (14 - this.getWorld().getDifficulty().getId() * 4));
        projectile.setItem(Items.SLIME_BALL.getDefaultStack());
        this.getWorld().spawnEntity(projectile);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            Util.spawnVerticalCircularParticlesFacingPlayer(serverWorld, startX, startY, startZ, target, ParticleTypes.ITEM_SLIME, 0.3);
        }
    }

    public abstract double getForwardDistance();

    public static class ProjectileAttackGoal extends Goal {
        private final MobEntity mob;
        private final RangedAttackMob owner;
        private final double mobSpeed;
        private final int minIntervalTicks;
        private final int maxIntervalTicks;
        private final float maxShootRange;
        @Nullable
        private LivingEntity target;
        private int updateCountdownTicks;

        public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int intervalTicks, float maxShootRange) {
            this(mob, mobSpeed, intervalTicks, intervalTicks, maxShootRange);
        }

        public ProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int minIntervalTicks, int maxIntervalTicks, float maxShootRange) {
            this.updateCountdownTicks = -1;
            if (!(mob instanceof LivingEntity)) {
                throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
            } else {
                this.owner = mob;
                this.mob = (MobEntity) mob;
                this.mobSpeed = mobSpeed;
                this.minIntervalTicks = minIntervalTicks;
                this.maxIntervalTicks = maxIntervalTicks;
                this.maxShootRange = maxShootRange;
                this.setControls(EnumSet.of(Control.MOVE));
            }
        }

        public boolean canStart() {
            LivingEntity livingEntity = this.mob.getTarget();
            if (livingEntity != null && livingEntity.isAlive()) {
                this.target = livingEntity;
                return true;
            } else {
                return false;
            }
        }

        public boolean shouldContinue() {
            return this.canStart() || (this.target != null && this.target.isAlive()) && !this.mob.getNavigation().isIdle();
        }

        public void stop() {
            this.target = null;
            this.updateCountdownTicks = -1;
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        public void tick() {
            if (this.target == null) return;
            if (this.target.isInvulnerable()) {
                return;
            }

            Vec3d targetPos = this.target.getPos();
            Vec3d direction = targetPos.subtract(this.mob.getPos()).normalize();
            Vec3d targetPosition = targetPos.subtract(direction.multiply(this.maxShootRange));
            Vec3d toTarget = targetPosition.subtract(this.mob.getPos());

            if (this.mob.getWorld() instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(ParticleTypes.FLAME, targetPosition.getX(), targetPosition.getY(), targetPosition.getZ(), 1, 0, 0, 0, 0);
            }

            if (this.mob.squaredDistanceTo(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ()) < 5 || this.mob.getRotationVec(1.0F).dotProduct(toTarget) <= 0) {
                double distance = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean bl = this.mob.getVisibilityCache().canSee(this.target);

                if (--this.updateCountdownTicks == 0) {
                    if (!bl) {
                        return;
                    }

                    float f = (float) Math.sqrt(distance) / this.maxShootRange;
                    float g = MathHelper.clamp(f, 0.1F, 1.0F);
                    this.owner.attack(this.target, g);
                    this.updateCountdownTicks = MathHelper.floor(f * (float) (this.maxIntervalTicks - this.minIntervalTicks) + (float) this.minIntervalTicks);
                } else if (this.updateCountdownTicks < 0) {
                    this.updateCountdownTicks = MathHelper.floor(MathHelper.lerp(Math.sqrt(distance) / (double) this.maxShootRange, this.minIntervalTicks, this.maxIntervalTicks));
                }
            } else {
                if (this.mob.getRotationVec(1.0F).dotProduct(toTarget) <= 0)
                    return;

                this.mob.getNavigation().startMovingTo(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ(), this.mobSpeed);
            }
        }
    }

}
