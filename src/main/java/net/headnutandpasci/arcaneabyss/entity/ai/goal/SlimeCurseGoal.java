package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class SlimeCurseGoal extends Goal {
    private final ArcaneBossSlime bossSlime;
    private List<PlayerEntity> hitPlayers;
    private int timer;

    public SlimeCurseGoal(ArcaneBossSlime bossSlime) {
        this.bossSlime = bossSlime;
    }

    @Override
    public boolean canStart() {
        return bossSlime.isInState(ArcaneBossSlime.State.CURSE) && bossSlime.hasTarget();
    }

    @Override
    public void start() {
        timer = 100;
        this.bossSlime.playSound(SoundEvents.ENTITY_WARDEN_ROAR, 200.0F, 4.0F);
        hitPlayers = new ArrayList<>();
        if (bossSlime.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    ParticleTypes.GLOW,
                    bossSlime.getX(),
                    bossSlime.getBodyY(0.5),
                    bossSlime.getZ(),
                    1000,
                    0.5,
                    0.5,
                    0.5,
                    0.1
            );
        }
    }


    @Override
    public void tick() {
        if (!this.bossSlime.getPlayerNearby().isEmpty() && bossSlime.getWorld() instanceof ServerWorld serverWorld) {
            for (PlayerEntity targetPlayer : this.bossSlime.getPlayerNearby()) {
                // TODO: test if this build in method works
                if (!bossSlime.canSee(targetPlayer)) {
                    continue;
                }

                Vec3d from = bossSlime.getPos();
                Vec3d to = targetPlayer.getPos();

                double progress = 1.0 - (timer / 100.0);
                double x = from.x + (to.x - from.x) * progress;
                double y = from.y + (to.y - from.y) * progress;
                double z = from.z + (to.z - from.z) * progress;

                serverWorld.spawnParticles(
                        net.minecraft.particle.ParticleTypes.SOUL,
                        x, y + targetPlayer.getHeight() / 2.0, z,
                        10,
                        0.1, 0.1, 0.1,
                        0.0
                );


                Vec3d projectilePos = new Vec3d(x, y, z);
                if (projectilePos.distanceTo(targetPlayer.getPos()) < 1.0 && !hitPlayers.contains(targetPlayer)) {
                    hitPlayers.add(targetPlayer);
                }
            }

            timer = timer - 3;
            if (timer <= 0) {
                this.applyEffect();
                this.stop();
            }
        }
    }

    private void applyEffect() {
        for (PlayerEntity hitPlayer : hitPlayers) {
            if (hitPlayer != null && hitPlayer.isAlive()) {
                hitPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 3));
                hitPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 50, 1));
            }
        }
    }

    @Override
    public void stop() {
        hitPlayers = null;

        this.bossSlime.stopAttacking(100);
    }

    @Override
    public boolean shouldContinue() {
        List<ServerPlayerEntity> playerNearby = this.bossSlime.getPlayerNearby();
        return timer > 0 && !playerNearby.isEmpty();
    }
}
