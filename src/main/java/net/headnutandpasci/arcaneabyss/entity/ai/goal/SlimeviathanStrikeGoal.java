package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.networking.MovementControlPacket;
import net.headnutandpasci.arcaneabyss.util.Util;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class SlimeviathanStrikeGoal extends Goal {
    private static final ImmutableList<Direction> MOB_SUMMON_POS = ImmutableList.of(
            Direction.SOUTH,
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST
    );

    private final SlimeviathanEntity entity;
    private final List<PlayerEntity> strikeTargets;
    private final List<Vec3d> strikePositions;
    private int particleTimer = 0;
    private int chargeUpTimer = 0; // Timer for the charge-up phase
    private boolean isCharging = false; // Tracks if the entity is charging up
    private boolean firstTrigger = false;

    public SlimeviathanStrikeGoal(SlimeviathanEntity entity) {
        this.entity = entity;
        this.strikeTargets = new ArrayList<>();
        this.strikePositions = new ArrayList<>();
    }

    @Override
    public boolean canStart() {
        return (entity.isInState(ArcaneBossSlime.State.STRIKE_SUMMON)) && entity.getTarget() != null;
    }

    @Override
    public void start() {
        ArcaneAbyss.LOGGER.info("SlimeviathanStrikeGoal started");
        super.start();
        isCharging = false;
        chargeUpTimer = 0;
        firstTrigger = false;
        this.strikeTargets.clear();
        this.strikePositions.clear();

        this.entity.playSound(SoundEvents.ENTITY_WITHER_HURT, 100.0F, 40.0F);
        if (this.entity.getPlayerNearby().isEmpty()) this.stop();
        if (this.entity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl moveControl) {
            moveControl.setDisabled(true);
        }

        ((ServerWorld) this.entity.getWorld()).spawnParticles(
                ParticleTypes.POOF,
                this.entity.getX(), this.entity.getY(), this.entity.getZ(),
                400, 5.0D, 0.0D, 5.0D, 0.0D
        );

        this.entity.getPlayerNearby().forEach(target -> {
            this.strikeTargets.add(target);
            Util.pushPlayer(this.entity, target, 10, 2.0f);
            MovementControlPacket.send(true, (ServerPlayerEntity) target);
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, StatusEffectInstance.INFINITE, 4, false, false));
            for (int i = 0; i < 50; i++) {
                double offsetX = (this.entity.getRandom().nextDouble() - 0.5) * 2.0;
                double offsetY = this.entity.getRandom().nextDouble() * 2.0;
                double offsetZ = (this.entity.getRandom().nextDouble() - 0.5) * 2.0;
                ((ServerWorld) target.getWorld()).spawnParticles(ParticleTypes.END_ROD, target.getX() + offsetX, target.getY() + offsetY, target.getZ() + offsetZ, 3, 0.5, 0.5, 0.5, 0.1); }
        });
        this.entity.setAttackTimer(20);
    }

    @Override
    public void stop() {
        super.stop();
        this.strikeTargets.forEach(target -> {
            this.entity.playSound(SoundEvents.BLOCK_ANCIENT_DEBRIS_BREAK, 3.0F, 1.0F);
            target.removeStatusEffect(StatusEffects.SLOWNESS);
            MovementControlPacket.send(false, (ServerPlayerEntity) target);
        });

        this.strikeTargets.clear();
        this.strikePositions.clear();
        this.particleTimer = 0;
        this.chargeUpTimer = 0;
        this.isCharging = false;
        this.firstTrigger = false;

        entity.stopAttacking(100);
    }

    @Override
    public void tick() {
        this.spawnParticles();

        if (!isCharging) {
            // Begin charging phase
            isCharging = true;
            chargeUpTimer = 30; // Adjust duration as needed
            this.entity.playSound(SoundEvents.ENTITY_WITHER_AMBIENT, 3.0F, 0.5F);

            // Initial particle burst to signify the charge-up start
            ((ServerWorld) this.entity.getWorld()).spawnParticles(
                    ParticleTypes.FLAME,
                    this.entity.getX(), this.entity.getY() + 1.0, this.entity.getZ(),
                    50, 1.0, 1.0, 1.0, 0.1
            );

        } else if (chargeUpTimer > 0) {
            // During charging phase, show particles
            chargeUpTimer--;
            ((ServerWorld) this.entity.getWorld()).spawnParticles(
                    ParticleTypes.ENCHANTED_HIT,
                    this.entity.getX(), this.entity.getY() + 1.0, this.entity.getZ(),
                    10, 0.5, 0.5, 0.5, 0.2
            );
        } else if (chargeUpTimer == 0 && !firstTrigger) {
            // Execute the attack after charging
            firstTrigger = true;
            this.shootStrike();
            this.entity.setAttackTimer(20); // Reset attack timer for cooldown
        } else if (this.entity.getAttackTimer() <= 2) {
            // Stop after the attack finishes
            this.stop();
        }
    }

    private void shootStrike() {
        this.particleTimer = 80;
        this.strikeTargets.forEach(target -> {
            this.strikePositions.add(target.getPos());
        });

        switch (entity.getPhase()) {
            case 0 -> this.strikeTargets.forEach(target -> {
                this.shootStrike(this.entity.getPos().add(0, 3, 0), target.getPos());
            });
            case 1 -> this.strikeTargets.forEach(target -> {
                for (int i = 0; i < 4; i++) {
                    Direction direction = MOB_SUMMON_POS.get(i);
                    Vec3d spawn = this.entity.getPos().add(0, 3, 0).add(direction.getOffsetX() * 5, 0, direction.getOffsetZ() * 5);
                    this.shootStrike(spawn, target.getPos());
                }
            });
            default -> ArcaneAbyss.LOGGER.error("Invalid phase: {}", entity.getPhase());
        }
    }

    private void spawnParticles() {
        World world = this.entity.getWorld();
        if (world instanceof ServerWorld serverWorld) {
            if (particleTimer > 0) {
                if (!this.strikePositions.isEmpty() && particleTimer % 4 == 0) {
                    this.strikePositions.forEach(pos -> {
                        ParticleEffect effect = ((this.particleTimer % 10) == 0) ?
                                new DustParticleEffect(new Vector3f(18000000, 0, 0), 1.0f) :
                                new DustParticleEffect(new Vector3f(18000000, 18000000, 18000000), 1.0f);
                        Util.spawnCircleParticles(serverWorld, pos, effect, 4, 100, true);
                    });
                }
                particleTimer--;
            }
        }
    }

    private void shootStrike(Vec3d spawn, Vec3d target) {
        Vec3d direction = target.subtract(spawn).normalize();

        double arcHeight = 25.0;
        double distance = spawn.distanceTo(target);
        double initialVelocity = Math.sqrt(2 * distance * 23.31 / (arcHeight * 2));

        initialVelocity *= 0.20;

        double initialYVelocity = initialVelocity * Math.sin(Math.atan2(arcHeight, distance));

        TntEntity entity = new TntEntity(this.entity.getWorld(), spawn.x, spawn.y, spawn.z, this.entity);
        this.entity.getWorld().spawnEntity(entity);

        entity.setFuse(40);
        entity.setVelocity(
                direction.x * initialVelocity,
                initialYVelocity,
                direction.z * initialVelocity
        );
        entity.velocityModified = true;
    }
}
