package net.headnutandpasci.arcaneabyss.entity.slime.grey;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.particle.ParticleTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GreySlimeEntity extends ArcaneSlimeEntity {

    private static final int EFFECT_RADIUS = 10;
    private static final int EFFECT_DURATION = 5;
    private static final float PULSE_DAMAGE = 6.0f;
    private int pulseCooldownTimer = 60;
    private static final int NUM_PARTICLES = 50;

    public GreySlimeEntity(EntityType<? extends ArcaneSlimeEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributesGreySlime() {
        return ArcaneSlimeEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0D)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0f)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(0, new SwimGoal(this));
        this.goalSelector.add(1, new WanderAroundGoal(this, 1.0));
        this.targetSelector.add(0, new ActiveTargetGoal<>(this, ArcaneSlimeEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();

        if (this.world.isClient) {
            spawnParticleSphere();
        }

        if (!this.world.isClient && this.isAlive()) {
            applyResistanceEffect();
            handleEnergyPulse();
        }
    }

    private void handleEnergyPulse() {
        if (pulseCooldownTimer > 0) {
            pulseCooldownTimer--;
            return;
        }

        pulseCooldownTimer = 60;

        if (this.world instanceof ServerWorld) {

            ServerWorld serverWorld = (ServerWorld) this.world;

            double outerRadius = 10;

            Box searchBox = new Box(
                    this.getX() - outerRadius, this.getY() - outerRadius, this.getZ() - outerRadius,
                    this.getX() + outerRadius, this.getY() + outerRadius, this.getZ() + outerRadius
            );
            List<ServerPlayerEntity> players = serverWorld.getPlayers();

            List<PlayerEntity> playersInsideBox = new ArrayList<>();
            for (PlayerEntity player : players) {
                if (searchBox.contains(player.getPos())) {
                    playersInsideBox.add(player);
                }
            }
            if (playersInsideBox.isEmpty()) {
                return;
            }
            for (PlayerEntity player : playersInsideBox) {

                if (player.isCreative() || player.isSpectator()) {
                    continue;
                }
                float currentHealth = player.getHealth();
                player.setHealth(currentHealth - PULSE_DAMAGE);
                Vec3d summonPos = player.getPos();

                this.playSound(SoundEvents.ENTITY_IRON_GOLEM_DAMAGE, 30.0F, 1.0F);
                spawnParticlesAroundPlayer(serverWorld, summonPos);

                if (player.getHealth() <= 0.0f) {
                    continue;
                }
            }
        }
    }

    private void spawnParticlesAroundPlayer(ServerWorld serverWorld, Vec3d summonPos) {
        serverWorld.spawnParticles(
                ParticleTypes.DAMAGE_INDICATOR,
                summonPos.getX(),
                summonPos.getY() + 1.0,
                summonPos.getZ(),
                50,
                0.5D,
                0.5D,
                0.5D,
                0.0D
        );
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

    private void spawnParticleSphere() {
        double scaledRadius = EFFECT_RADIUS;
        for (int i = 0; i < NUM_PARTICLES; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.random() * Math.PI;

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
}
