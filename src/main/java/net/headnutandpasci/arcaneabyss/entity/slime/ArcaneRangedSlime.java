package net.headnutandpasci.arcaneabyss.entity.slime;

import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.projectile.SlimeBallProjectile;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
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
        SlimeBallProjectile magmaBallEntity = new SlimeBallProjectile(ModEntities.SLIME_BALL_PROJECTILE, this.getWorld());

        Vec3d forward = this.getRotationVector().multiply(1);
        double startX = this.getX() + forward.x;
        double startY = this.getBodyY(0.5);
        double startZ = this.getZ() + forward.z;
        magmaBallEntity.setPos(startX, startY, startZ);


        double d = target.getX() - magmaBallEntity.getX();
        double e = target.getBodyY(0.3333333333333333) - magmaBallEntity.getY();
        double f = target.getZ() - magmaBallEntity.getZ();
        double g = Math.sqrt(d * d + f * f);
        magmaBallEntity.setVelocity(d, e + g * 0.1, f, 1.35F, (float) (14 - this.getWorld().getDifficulty().getId() * 4));

        this.playSound(SoundEvents.ITEM_CROSSBOW_SHOOT, 0.10F, 0.3F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.getWorld().spawnEntity(magmaBallEntity);

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
        private final float squaredMaxShootRange;
        @Nullable
        private LivingEntity target;
        private int updateCountdownTicks;
        private int seenTargetTicks;

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
                this.squaredMaxShootRange = maxShootRange * maxShootRange;
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
            this.seenTargetTicks = 0;
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

            if (squaredMaxShootRange > this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ())) {

                double d = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
                boolean bl = this.mob.getVisibilityCache().canSee(this.target);
                if (bl) {
                    ++this.seenTargetTicks;
                } else {
                    this.seenTargetTicks = 0;
                }


                if (--this.updateCountdownTicks == 0) {
                    if (!bl) {
                        return;
                    }

                    float f = (float) Math.sqrt(d) / this.maxShootRange;
                    float g = MathHelper.clamp(f, 0.1F, 1.0F);
                    this.owner.attack(this.target, g);
                    this.updateCountdownTicks = MathHelper.floor(f * (float) (this.maxIntervalTicks - this.minIntervalTicks) + (float) this.minIntervalTicks);
                } else if (this.updateCountdownTicks < 0) {
                    this.updateCountdownTicks = MathHelper.floor(MathHelper.lerp(Math.sqrt(d) / (double) this.maxShootRange, this.minIntervalTicks, this.maxIntervalTicks));
                } else {
                    this.mob.getNavigation().startMovingTo(this.target, this.mobSpeed);
                }
            }
        }
    }

}
