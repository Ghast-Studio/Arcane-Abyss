package net.headnutandpasci.arcaneabyss.entity.custom;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class RedSlimeEntity extends HostileEntity implements RangedAttackMob {

    public float targetStretch;
    public float lastStretch;
    public float stretch;
    private boolean onGroundLastTick;

    public RedSlimeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new RedSlimeMoveControl(this);
    }

    public static DefaultAttributeContainer.Builder setAttributesRedSlime() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimmingGoal(this));
        this.goalSelector.add(2, new SlimeProjectileAttackGoal(this, 1.0, 20, 15.0F));
        this.goalSelector.add(3, new FaceTowardTargetGoal(this));
        this.goalSelector.add(4, new RandomLookGoal(this));
        this.goalSelector.add(5, new MoveGoal(this, 1.0));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, IronGolemEntity.class, true));
    }

    public boolean canUseRangedWeapon(RangedWeaponItem weapon) {
        return true;
    }

    @Override
    public boolean canSpawn(WorldView world) {
        return false;
    }

    public void attack(LivingEntity target, float pullProgress) {
        SnowballEntity snowballEntity = new SnowballEntity(this.getWorld(), this);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - snowballEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        snowballEntity.setVelocity(d, e + g * 0.20000000298023224, f, 1.6F, (float) (14 - this.getWorld().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.getWorld().spawnEntity(snowballEntity);
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.3F;
    }

    protected ParticleEffect getParticles() {
        return ParticleTypes.ITEM_SLIME;
    }

    @Override
    public void tick() {
        this.stretch += (this.targetStretch - this.stretch) * 0.5F;
        this.lastStretch = this.stretch;
        super.tick();
        if (this.isOnGround() && !this.onGroundLastTick) {
            int i = 2;

            for (int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * 6.2831855F;
                float g = this.random.nextFloat() * 0.5F + 0.5F;
                float h = MathHelper.sin(f) * (float) i * 0.5F * g;
                float k = MathHelper.cos(f) * (float) i * 0.5F * g;
                this.getWorld().addParticle(this.getParticles(), this.getX() + (double) h, this.getY(), this.getZ() + (double) k, 0.0, 0.0, 0.0);
            }

            this.playSound(SoundEvents.ENTITY_SLIME_SQUISH, this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            this.targetStretch = -0.5F;
        } else if (!this.isOnGround() && this.onGroundLastTick) {
            this.targetStretch = 1.0F;
        }

        this.onGroundLastTick = this.isOnGround();
        this.updateStretch();
    }

    public void pushAwayFrom(Entity entity) {
        super.pushAwayFrom(entity);
        if (entity instanceof IronGolemEntity) {
            this.damage((LivingEntity) entity);
        }
    }

    public void onPlayerCollision(PlayerEntity player) {
        this.damage(player);
    }

    protected float getDamageAmount() {
        return (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    protected void damage(LivingEntity target) {
        if (this.isAlive()) {
            if (this.squaredDistanceTo(target) < 0.6 * 2.0d * 0.6 * 2.0d && this.canSee(target) && target.damage(this.getDamageSources().mobAttack(this), this.getDamageAmount())) {
                this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.applyDamageEffects(this, target);
            }
        }

    }


    protected void updateStretch() {
        this.targetStretch *= 0.6F;
    }

    private static class RedSlimeMoveControl extends MoveControl {
        private float targetYaw;
        private int ticksUntilJump;
        private final RedSlimeEntity slime;
        private boolean jumpOften;

        public RedSlimeMoveControl(RedSlimeEntity slime) {
            super(slime);
            this.slime = slime;
            this.targetYaw = 180.0F * slime.getYaw() / 3.1415927F;
        }

        public void look(float targetYaw, boolean jumpOften) {
            this.targetYaw = targetYaw;
            this.jumpOften = jumpOften;
        }

        public void move(double speed) {
            this.speed = speed;
            this.state = State.MOVE_TO;
        }

        private float getJumpSoundPitch() {
            return ((this.slime.getRandom().nextFloat() - this.slime.getRandom().nextFloat()) * 0.2F + 1.0F) * 0.8F;
        }

        @Override
        public void tick() {
            this.entity.setYaw(this.wrapDegrees(this.entity.getYaw(), this.targetYaw, 90.0F));
            this.entity.headYaw = this.entity.getYaw();
            this.entity.bodyYaw = this.entity.getYaw();
            if (this.state != State.MOVE_TO) {
                this.entity.setForwardSpeed(0.0F);
            } else {
                this.state = State.WAIT;
                if (this.entity.isOnGround()) {
                    this.entity.setMovementSpeed((float) (this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                    if (this.ticksUntilJump-- <= 0) {
                        this.ticksUntilJump = this.slime.getRandom().nextInt(20) + 10;
                        if (this.jumpOften) {
                            this.ticksUntilJump /= 3;
                        }

                        this.slime.getJumpControl().setActive();
                        this.slime.playSound(SoundEvents.ENTITY_SLIME_JUMP, 0.8f, this.getJumpSoundPitch());
                    } else {
                        this.slime.sidewaysSpeed = 0.0F;
                        this.slime.forwardSpeed = 0.0F;
                        this.entity.setMovementSpeed(0.0F);
                    }
                } else {
                    this.entity.setMovementSpeed((float) (this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                }

            }
        }
    }

    static class SwimmingGoal extends Goal {
        private final RedSlimeEntity slime;

        public SwimmingGoal(RedSlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
            slime.getNavigation().setCanSwim(true);
        }

        public boolean canStart() {
            return (this.slime.isTouchingWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof RedSlimeEntity.RedSlimeMoveControl;
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8F) {
                this.slime.getJumpControl().setActive();
            }

            MoveControl var2 = this.slime.getMoveControl();
            if (var2 instanceof RedSlimeEntity.RedSlimeMoveControl slimeMoveControl) {
                slimeMoveControl.move(1.2);
            }

        }
    }

    static class FaceTowardTargetGoal extends Goal {
        private final RedSlimeEntity slime;
        private int ticksLeft;

        public FaceTowardTargetGoal(RedSlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Control.LOOK));
        }

        public boolean canStart() {
            LivingEntity livingEntity = this.slime.getTarget();
            if (livingEntity == null) {
                return false;
            } else {
                return this.slime.canTarget(livingEntity) && this.slime.getMoveControl() instanceof RedSlimeMoveControl;
            }
        }

        public void start() {
            this.ticksLeft = toGoalTicks(300);
            super.start();
        }

        public boolean shouldContinue() {
            LivingEntity livingEntity = this.slime.getTarget();
            if (livingEntity == null) {
                return false;
            } else if (!this.slime.canTarget(livingEntity)) {
                return false;
            } else {
                return --this.ticksLeft > 0;
            }
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        public void tick() {
            LivingEntity livingEntity = this.slime.getTarget();
            if (livingEntity != null) {
                this.slime.lookAtEntity(livingEntity, 10.0F, 10.0F);
            }

            MoveControl control = this.slime.getMoveControl();
            if (control instanceof RedSlimeEntity.RedSlimeMoveControl slimeMoveControl) {
                slimeMoveControl.look(this.slime.getYaw(), true);
            }

        }
    }

    static class RandomLookGoal extends Goal {
        private final RedSlimeEntity slime;
        private float targetYaw;
        private int timer;

        public RandomLookGoal(RedSlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Control.LOOK));
        }

        public boolean canStart() {
            return this.slime.getTarget() == null && (this.slime.isOnGround() || this.slime.isTouchingWater() || this.slime.isInLava() || this.slime.hasStatusEffect(StatusEffects.LEVITATION)) && this.slime.getMoveControl() instanceof RedSlimeEntity.RedSlimeMoveControl;
        }

        public void tick() {
            if (--this.timer <= 0) {
                this.timer = this.getTickCount(40 + this.slime.getRandom().nextInt(60));
                this.targetYaw = (float) this.slime.getRandom().nextInt(360);
            }

            MoveControl control = this.slime.getMoveControl();
            if (control instanceof RedSlimeEntity.RedSlimeMoveControl slimeMoveControl) {
                slimeMoveControl.look(this.targetYaw, false);
            }

        }
    }

    static class MoveGoal extends Goal {
        private final RedSlimeEntity slime;
        private final double speed;

        public MoveGoal(RedSlimeEntity slime, double speed) {
            this.slime = slime;
            this.speed = speed;
            this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
        }

        public boolean canStart() {
            return !this.slime.hasVehicle();
        }

        public void tick() {
            MoveControl control = this.slime.getMoveControl();
            if (control instanceof RedSlimeEntity.RedSlimeMoveControl slimeMoveControl) {
                slimeMoveControl.move(speed);
            }

        }
    }

    public static class SlimeProjectileAttackGoal extends Goal {
        private final MobEntity mob;
        private final RangedAttackMob owner;
        @Nullable
        private LivingEntity target;
        private int updateCountdownTicks;
        private final double mobSpeed;
        private int seenTargetTicks;
        private final int minIntervalTicks;
        private final int maxIntervalTicks;
        private final float maxShootRange;
        private final float squaredMaxShootRange;

        public SlimeProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int intervalTicks, float maxShootRange) {
            this(mob, mobSpeed, intervalTicks, intervalTicks, maxShootRange);
        }

        public SlimeProjectileAttackGoal(RangedAttackMob mob, double mobSpeed, int minIntervalTicks, int maxIntervalTicks, float maxShootRange) {
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
            double d = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
            boolean bl = this.mob.getVisibilityCache().canSee(this.target);
            if (bl) {
                ++this.seenTargetTicks;
            } else {
                this.seenTargetTicks = 0;
            }

            if (!(d > (double) this.squaredMaxShootRange) && this.seenTargetTicks >= 5) {
                this.mob.getNavigation().stop();
            } else {
                this.mob.getNavigation().startMovingTo(this.target, this.mobSpeed);
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
                this.updateCountdownTicks = MathHelper.floor(MathHelper.lerp(Math.sqrt(d) / (double) this.maxShootRange, (double) this.minIntervalTicks, (double) this.maxIntervalTicks));
            }

        }
    }

}
