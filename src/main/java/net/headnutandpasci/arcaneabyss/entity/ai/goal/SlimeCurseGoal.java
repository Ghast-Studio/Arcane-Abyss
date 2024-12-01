package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import java.util.List;

public class SlimeCurseGoal extends Goal {
    private final BlackSlimeEntity blackSlimeEntity;
    private List<PlayerEntity> targetPlayers;
    private int timer;

    public SlimeCurseGoal(BlackSlimeEntity blackSlimeEntity) {
        this.blackSlimeEntity = blackSlimeEntity;
    }

    @Override
    public boolean canStart() {
        World world = blackSlimeEntity.getWorld();
        targetPlayers = (List<PlayerEntity>) world.getPlayers();


        targetPlayers = targetPlayers.stream()
                .filter(player -> player.squaredDistanceTo(blackSlimeEntity) <= 20 * 20)
                .filter(player -> !player.isInvulnerable())
                .toList();

        return !targetPlayers.isEmpty();
    }



    @Override
    public void start() {
        timer = 100;
        if (blackSlimeEntity.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    net.minecraft.particle.ParticleTypes.ENCHANTED_HIT,
                    blackSlimeEntity.getX(),
                    blackSlimeEntity.getBodyY(0.5),
                    blackSlimeEntity.getZ(),
                    10,
                    0.5,
                    0.5,
                    0.5,
                    0.1
            );
        }
    }

    @Override
    public void tick() {
        if (!targetPlayers.isEmpty() && blackSlimeEntity.getWorld() instanceof ServerWorld serverWorld) {
            for (PlayerEntity targetPlayer : targetPlayers) {
                if (!hasLineOfSight(targetPlayer)) {
                    continue;
                }

                Vec3d from = blackSlimeEntity.getPos();
                Vec3d to = targetPlayer.getPos();

                double progress = 1.0 - (timer / 100.0);
                double x = from.x + (to.x - from.x) * progress;
                double y = from.y + (to.y - from.y) * progress;
                double z = from.z + (to.z - from.z) * progress;

                serverWorld.spawnParticles(
                        net.minecraft.particle.ParticleTypes.SOUL,
                        x, y + targetPlayer.getHeight() / 2.0, z,
                        15,
                        0.1, 0.1, 0.1,
                        0.0
                );
            }

            if (--timer <= 0) {
                applyEffect();
                stop();
            }
        }
    }

    private void applyEffect() {
        for (PlayerEntity targetPlayer : targetPlayers) {
            if (targetPlayer != null && targetPlayer.isAlive()) {
                targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 1000, 3));
                targetPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 50, 1));
            }
        }
    }

    private boolean hasLineOfSight(PlayerEntity player) {
        World world = blackSlimeEntity.getWorld();
        Vec3d from = blackSlimeEntity.getEyePos();
        Vec3d to = player.getEyePos();

        BlockHitResult hitResult = world.raycast(new RaycastContext(
                from,
                to,
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                blackSlimeEntity
        ));


        return hitResult.getType() == HitResult.Type.MISS || hitResult.getPos().distanceTo(to) < 0.1;
    }

    @Override
    public void stop() {
        targetPlayers = null;
    }

    @Override
    public boolean shouldContinue() {
        return timer > 0 && !targetPlayers.isEmpty() &&
                targetPlayers.stream().anyMatch(player -> player.isAlive() && hasLineOfSight(player));
    }
}
