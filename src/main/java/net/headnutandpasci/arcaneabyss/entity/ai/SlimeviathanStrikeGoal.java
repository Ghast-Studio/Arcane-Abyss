package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class SlimeviathanStrikeGoal extends Goal {
    private final SlimeviathanEntity slimeviathanEntity;
    private int particleTimer = 0;
    private LivingEntity targetAtStrike;
    private Vec3d targetPosAtStrike;

    public SlimeviathanStrikeGoal(SlimeviathanEntity slimeviathanEntity) {
        this.slimeviathanEntity = slimeviathanEntity;
    }

    @Override
    public boolean canStart() {
        return (slimeviathanEntity.isAttacking(SlimeviathanEntity.State.STRIKE_SUMMON)) && slimeviathanEntity.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        this.targetAtStrike = slimeviathanEntity.getTarget();
        if (this.targetAtStrike == null) this.stop();

        System.out.println(this.slimeviathanEntity.getMoveControl());
        if (this.slimeviathanEntity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl moveControl) {
            moveControl.setDisabled(true);

        }

        this.slimeviathanEntity.setAttackTimer(40);
        this.targetPosAtStrike = targetAtStrike.getPos();
        this.particleTimer = 50;
    }

    @Override
    public void stop() {
        super.stop();
        this.targetAtStrike = null;
        this.targetPosAtStrike = null;
        this.particleTimer = 0;

        if (this.slimeviathanEntity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl moveControl) {
            moveControl.setDisabled(false);
        }
    }

    @Override
    public void tick() {
        if (particleTimer > 0) {
            if (this.targetAtStrike != null && particleTimer % 5 == 0) {
                ParticleEffect effect = ((this.particleTimer % 10) == 0) ?
                        new DustParticleEffect(new Vector3f(18000000, 0, 0), 1.0f) :
                        new DustParticleEffect(new Vector3f(18000000, 18000000, 18000000), 1.0f);
                spawnCircleParticles(targetAtStrike.getWorld(), this.targetPosAtStrike, effect, 4, 200);
            }

            particleTimer--;
        }
        if(slimeviathanEntity.getPhase() == 1) {
            if (slimeviathanEntity.getAttackTimer() == 0) {
                if (targetAtStrike != null) {
                    this.shootStrike(this.slimeviathanEntity.getPos().add(0, 3, 0), targetPosAtStrike);
                    slimeviathanEntity.setInvulTimer(60);
                    slimeviathanEntity.stopAttacking(100);
                }
            }
        }
        if(slimeviathanEntity.getPhase() == 2) {
            if (slimeviathanEntity.getAttackTimer() == 0) {
                if (targetAtStrike != null) {
                    this.shootStrike(this.slimeviathanEntity.getPos().add(0, 3, 0), targetPosAtStrike);
                    this.shootStrike(this.slimeviathanEntity.getPos().add(3, 3, 8), targetPosAtStrike);
                    this.shootStrike(this.slimeviathanEntity.getPos().add(-6, 3, -8), targetPosAtStrike);
                    this.shootStrike(this.slimeviathanEntity.getPos().add(9, 4, -12), targetPosAtStrike);
                    slimeviathanEntity.setInvulTimer(60);
                    slimeviathanEntity.stopAttacking(33);
                }
            }
        }
    }

    public void spawnCircleParticles(World world, Vec3d center, ParticleEffect particle, double radius, int particleCount) {
        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            double y = world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) x, (int) z) + 0.2;

            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
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

        //BlueSlimeEntity entity = ModEntities.BLUE_SLIME.spawn((ServerWorld) this.slimeviathanEntity.getWorld(), BlockPos.ofFloored(spawn), SpawnReason.REINFORCEMENT);
        TntEntity entity = new TntEntity(this.slimeviathanEntity.getWorld(), spawn.x, spawn.y, spawn.z, this.slimeviathanEntity);
        this.slimeviathanEntity.getWorld().spawnEntity(entity);

        entity.setFuse(40);
        entity.setVelocity(
                direction.x * initialVelocity,
                initialYVelocity,
                direction.z * initialVelocity
        );
    }


}
