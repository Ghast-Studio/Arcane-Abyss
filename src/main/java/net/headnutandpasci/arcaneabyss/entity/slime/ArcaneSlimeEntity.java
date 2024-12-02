package net.headnutandpasci.arcaneabyss.entity.slime;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

import java.util.EnumSet;

public abstract class ArcaneSlimeEntity extends HostileEntity {
    public float targetStretch;
    public float lastStretch;
    public float stretch;
    private boolean onGroundLastTick;

    public ArcaneSlimeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new ArcaneSlimeMoveControl(this);
    }

    protected ParticleEffect getParticles() {
        return ParticleTypes.ITEM_SLIME;
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        super.pushAwayFrom(entity);
        if (entity instanceof IronGolemEntity) {
            this.damage((LivingEntity) entity);
        }
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        this.damage(player);
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return 0.3F;
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

    @Override
    public boolean canSpawn(WorldView world) {
        return true;
    }

    protected float getDamageAmount() {
        return (float) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
    }

    protected void damage(LivingEntity target) {
        if (this.isAlive()) {
            if (this.squaredDistanceTo(target) < 0.6 * 2.0d * 0.6 * 2.0d && this.canSee(target) && target.damage(this.getDamageSources().mobAttack(this), this.getDamageAmount())) {
                this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
                this.applyDamageEffects(this, target);
            }
        }

    }

    @Override
    protected boolean isDisallowedInPeaceful() {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    protected void updateStretch() {
        this.targetStretch *= 0.6F;
    }

    /* AI Goals & Movement Controllers */

    protected SoundEvent getJumpSound() {
        return SoundEvents.ENTITY_SLIME_JUMP;
    }

    public static class ArcaneSlimeMoveControl extends MoveControl {
        private final ArcaneSlimeEntity slime;
        private float targetYaw;
        private int ticksUntilJump;
        private boolean jumpOften;
        private boolean disabled;

        public ArcaneSlimeMoveControl(ArcaneSlimeEntity slime) {
            super(slime);
            this.slime = slime;
            this.targetYaw = 180.0F * slime.getYaw() / 3.1415927F;
        }

        public void look(float targetYaw, boolean jumpOften) {
            this.targetYaw = targetYaw;
            this.jumpOften = jumpOften;
        }

        public void move(double speed) {
            if (this.disabled) return;

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
            } else if (!isDisabled()) {
                this.state = State.WAIT;
                if (this.entity.isOnGround()) {
                    this.entity.setMovementSpeed((float) (this.speed * this.entity.getAttributeValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)));
                    if (this.ticksUntilJump-- <= 0) {
                        this.ticksUntilJump = this.slime.getRandom().nextInt(20) + 10;
                        if (this.jumpOften) {
                            this.ticksUntilJump /= 3;
                        }

                        this.slime.getJumpControl().setActive();
                        SoundEvent jumpSound = this.slime.getJumpSound();

                        if (jumpSound != null) {
                            this.slime.playSound(this.slime.getJumpSound(), 0.8f, this.getJumpSoundPitch());
                        }

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

        public boolean isDisabled() {
            return disabled;
        }

        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }
    }

    protected static class SwimmingGoal extends Goal {
        private final ArcaneSlimeEntity slime;

        public SwimmingGoal(ArcaneSlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
            slime.getNavigation().setCanSwim(true);
        }

        public boolean canStart() {
            return (this.slime.isTouchingWater() || this.slime.isInLava()) && this.slime.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl;
        }

        public boolean shouldRunEveryTick() {
            return true;
        }

        public void tick() {
            if (this.slime.getRandom().nextFloat() < 0.8F) {
                this.slime.getJumpControl().setActive();
            }

            MoveControl var2 = this.slime.getMoveControl();
            if (var2 instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl slimeMoveControl) {
                slimeMoveControl.move(1.2);
            }

        }
    }

    protected static class FaceTowardTargetGoal extends Goal {
        private final ArcaneSlimeEntity slime;
        private int ticksLeft;

        public FaceTowardTargetGoal(ArcaneSlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Control.LOOK));
        }

        public boolean canStart() {
            LivingEntity livingEntity = this.slime.getTarget();
            if (livingEntity == null) {
                return false;
            } else {
                return this.slime.canTarget(livingEntity) && (this.slime.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl || this.slime.getMoveControl() instanceof ArcaneBossSlime.DisabledMoveControl);
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
            if (control instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl slimeMoveControl) {
                slimeMoveControl.look(this.slime.getYaw(), true);
            }

            if (control instanceof ArcaneBossSlime.DisabledMoveControl disabledMoveControl) {
                disabledMoveControl.look(this.slime.getYaw());
            }
        }
    }

    protected static class RandomLookGoal extends Goal {
        private final ArcaneSlimeEntity slime;
        private float targetYaw;
        private int timer;

        public RandomLookGoal(ArcaneSlimeEntity slime) {
            this.slime = slime;
            this.setControls(EnumSet.of(Control.LOOK));
        }

        public boolean canStart() {
            return this.slime.getTarget() == null && (this.slime.isOnGround() || this.slime.isTouchingWater() || this.slime.isInLava() || this.slime.hasStatusEffect(StatusEffects.LEVITATION)) && (this.slime.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl || this.slime.getMoveControl() instanceof ArcaneBossSlime.DisabledMoveControl);
        }

        public void tick() {
            if (--this.timer <= 0) {
                this.timer = this.getTickCount(40 + this.slime.getRandom().nextInt(60));
                this.targetYaw = (float) this.slime.getRandom().nextInt(360);
            }

            MoveControl control = this.slime.getMoveControl();
            if (control instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl slimeMoveControl) {
                slimeMoveControl.look(this.targetYaw, false);
            }

            if (control instanceof ArcaneBossSlime.DisabledMoveControl disabledMoveControl) {
                disabledMoveControl.look(this.slime.getYaw());
            }
        }
    }

    protected static class MoveGoal extends Goal {
        private final ArcaneSlimeEntity slime;
        private final double speed;

        public MoveGoal(ArcaneSlimeEntity slime, double speed) {
            this.slime = slime;
            this.speed = speed;
            this.setControls(EnumSet.of(Control.JUMP, Control.MOVE));
        }

        public boolean canStart() {
            return !this.slime.hasVehicle();
        }

        public void tick() {
            MoveControl control = this.slime.getMoveControl();
            if (control instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl slimeMoveControl) {
                slimeMoveControl.move(speed);
            }

        }
    }

    @Override
    protected Identifier getLootTableId() {
        Identifier lootTable = new Identifier("arcaneabyss", "entities/genericslimes");
        System.out.println("Loot table ID: " + lootTable);
        return lootTable;
    }
}
