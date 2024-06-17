package net.headnutandpasci.arcaneabyss.entity.custom.slime;

import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.custom.RedSlimeMagmaBallEntity;
import net.headnutandpasci.arcaneabyss.entity.custom.slime.ArcaneSlimeEntity;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class RedSlimeEntity extends ArcaneSlimeEntity implements RangedAttackMob {

    public RedSlimeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new ArcaneSlimeMoveControl(this);
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

    public void attack(LivingEntity target, float pullProgress) {
        RedSlimeMagmaBallEntity magmaBallEntity = new RedSlimeMagmaBallEntity(this, this.getWorld());
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333) - magmaBallEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        magmaBallEntity.setVelocity(d, e + g * 0.20000000298023224, f, 1.6F, (float) (14 - this.getWorld().getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 0.4F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.getWorld().spawnEntity(magmaBallEntity);
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
