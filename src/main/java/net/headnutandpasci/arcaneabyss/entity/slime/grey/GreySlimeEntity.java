package net.headnutandpasci.arcaneabyss.entity.slime.grey;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;


import java.util.List;

public class GreySlimeEntity extends ArcaneSlimeEntity {

    private static final int EFFECT_RADIUS = 10;
    private static final int EFFECT_DURATION = 5;
    private static final int NUM_PARTICLES = 100;

    public GreySlimeEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributesGreySlime() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 4.0f)
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, 2.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 9)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimmingGoal(this));
        this.goalSelector.add(2, new FaceTowardTargetGoal(this));
        this.goalSelector.add(3, new RandomLookGoal(this));
        this.goalSelector.add(4, new MoveGoal(this, 1.0));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.world.isClient) {
            spawnParticleSphere();
        }

        if (!this.world.isClient && this.isAlive()) {
            applyResistanceEffect();
        }
    }

    private void spawnParticleSphere() {
        double scaledRadius = EFFECT_RADIUS; // Scale particles' effect radius proportionally
        for (int i = 0; i < NUM_PARTICLES; i++) {
            double theta = Math.random() * 2 * Math.PI; // Random azimuthal angle
            double phi = Math.random() * Math.PI; // Random polar angle

            // Scale particle offset
            double xOffset = scaledRadius * Math.sin(phi) * Math.cos(theta);
            double yOffset = scaledRadius * Math.sin(phi) * Math.sin(theta);
            double zOffset = scaledRadius * Math.cos(phi);

            this.world.addParticle(
                    ParticleTypes.AMBIENT_ENTITY_EFFECT,
                    this.getX() + xOffset,
                    this.getY() + yOffset,
                    this.getZ() + zOffset,
                    0,
                    0,
                    0
            );
        }
    }

    private void applyResistanceEffect() {
        if (this.world instanceof ServerWorld) {
            List<ArcaneSlimeEntity> nearbySlimes = this.world.getEntitiesByClass(
                    ArcaneSlimeEntity.class,
                    this.getBoundingBox().expand(EFFECT_RADIUS),
                    entity -> entity != this && entity.isAlive()
            );

            for (ArcaneSlimeEntity slime : nearbySlimes) {
                slime.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, EFFECT_DURATION, 4, true, false));
            }
        }
    }
}
