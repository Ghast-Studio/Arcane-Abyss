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

import java.util.EnumSet;

public abstract class ArcaneRangedSlime extends ArcaneSlimeEntity implements RangedAttackMob {

    public ArcaneRangedSlime(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public ArcaneRangedSlime(EntityType<? extends HostileEntity> entityType, World world, int size) {
        super(entityType, world, size);
    }

    public void attack(LivingEntity target, float pullProgress) {
        Vec3d forward = this.getRotationVector().multiply(2);
        double startX = this.getX() + forward.x;
        double startY = this.getBodyY(1);
        double startZ = this.getZ() + forward.z;
        double x = target.getX() - startX;
        double y = target.getBodyY(0.5) - startY;
        double z = target.getZ() - startZ;
        double sqrt = Math.sqrt(x * x + z * z);

        SlimeProjectile projectile = new SlimeProjectile(this.getWorld(), this, x, y + sqrt * 0.1, z);
        projectile.setItem(Items.SLIME_BALL.getDefaultStack());
        this.getWorld().spawnEntity(projectile);

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            spawnVerticalCircularParticlesFacingPlayer(serverWorld, startX, startY, startZ, target);
        }
    }

    private void spawnVerticalCircularParticlesFacingPlayer(ServerWorld world, double centerX, double centerY, double centerZ, LivingEntity target) {
        int particleCount = 20;
        double radius = 0.3;


        Vec3d directionToPlayer = target.getPos().subtract(centerX, centerY, centerZ).normalize();


        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d right = directionToPlayer.crossProduct(up).normalize();
        Vec3d vertical = right.crossProduct(directionToPlayer).normalize();

        for (int i = 0; i < particleCount; i++) {

            double angle = 2 * Math.PI * i / particleCount;


            double offsetX = radius * (Math.cos(angle) * right.x + Math.sin(angle) * vertical.x);
            double offsetY = radius * (Math.cos(angle) * right.y + Math.sin(angle) * vertical.y);
            double offsetZ = radius * (Math.cos(angle) * right.z + Math.sin(angle) * vertical.z);


            world.spawnParticles(
                    ParticleTypes.FLAME,
                    centerX + offsetX,
                    centerY + offsetY,
                    centerZ + offsetZ,
                    1,
                    0, 0, 0, 0
            );
        }
    }

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
            return this.canStart() || this.target.isAlive() && !this.mob.getNavigation().isIdle();
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

            System.out.println("distance to target: " + this.mob.squaredDistanceTo(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ()));
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
                if(this.mob.getRotationVec(1.0F).dotProduct(toTarget) <= 0)
                    return;

                this.mob.getNavigation().startMovingTo(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ(), this.mobSpeed);
            }
        }
    }

}
