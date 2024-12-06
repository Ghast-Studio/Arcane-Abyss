package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import net.headnutandpasci.arcaneabyss.entity.projectile.SlimeBallProjectile;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
import net.headnutandpasci.arcaneabyss.util.Math.VectorUtils;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SlimeShootGoal extends Goal {
    private final ArcaneBossSlime bossSlime;
    @Nullable
    private String type;

    private int rotatedShootAmount = 0;

    public SlimeShootGoal(ArcaneBossSlime bossSlime) {
        this.bossSlime = bossSlime;
    }

    @Override
    public boolean canStart() {
        if (bossSlime.isInState(ArcaneBossSlime.State.SHOOT_SLIME_BULLET)) {
            if (!(bossSlime.hasTarget() && !bossSlime.getPlayerNearby().isEmpty())) {
                this.bossSlime.setState(ArcaneBossSlime.State.IDLE);
                return false;
            }
        }

        return bossSlime.isInState(ArcaneBossSlime.State.SHOOT_SLIME_BULLET) && bossSlime.hasTarget() && !bossSlime.getPlayerNearby().isEmpty();
    }

    @Override
    public void start() {
        super.start();
        this.type = this.rollType();
        bossSlime.setAttackTimer(200);
    }

    @Override
    public void stop() {
        super.stop();
        this.bossSlime.stopAttacking(100);
    }

    private String rollType() {
        WeightedRandomBag<String> bulletPatterns = new WeightedRandomBag<>();
        if (bossSlime.isInState(ArcaneBossSlime.State.SHOOT_SLIME_BULLET)) {
            if (bossSlime.isInPhase(0)) {
                bulletPatterns.addEntry("Single", 1);
                bulletPatterns.addEntry("MultiShot", 1);
            } else if (bossSlime.isInPhase(1)) {
                bulletPatterns.addEntry("RapidSingle", 1);
                bulletPatterns.addEntry("RapidMultiShot", 1);
            }
        }

        String type = bulletPatterns.getRandom();
        if (type == null) return "Single";

        return bulletPatterns.getRandom();
    }

    @Override
    public void tick() {
        if (bossSlime.getAttackTimer() == 0) {
            bossSlime.stopAttacking(100);
            this.rotatedShootAmount = 0;
            return;
        }

        if (this.type == null) return;

        switch (type) {
            case "Single" -> {
                if (bossSlime.getAttackTimer() % 20 == 0) performSingleShot();
            }
            case "RapidSingle" -> {
                if (bossSlime.getAttackTimer() % 10 == 0) performSingleShot();
            }
            case "MultiShot" -> {
                if (bossSlime.getAttackTimer() % 20 == 0) {
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount)));
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) + 180));
                }
            }
            case "RapidMultiShot" -> {
                if (bossSlime.getAttackTimer() % 10 == 0) {
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount + 120)));
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) + 240));
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) + 360));
                    this.rotatedShootAmount++;
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    private void shootSkullAt(Vec3d offset, LivingEntity target) {
        ServerWorld world = bossSlime.getWorld() instanceof ServerWorld ? ((ServerWorld) bossSlime.getWorld()) : null;
        if (world == null) return;

        Vec3d forward = this.bossSlime.getRotationVector().multiply(1);
        double startX = this.bossSlime.getX() + offset.getX() + forward.x;
        double startY = this.bossSlime.getBodyY(1) + offset.getY();
        double startZ = this.bossSlime.getZ() + offset.getZ() + forward.z;

        SlimeBallProjectile projectile = new SlimeBallProjectile(this.bossSlime, world, new Vec3d(startX, startY, startZ));

        double d = target.getX() - projectile.getX();
        double e = target.getBodyY(0.3333333333333333) - projectile.getY();
        double f = target.getZ() - projectile.getZ();
        double g = Math.sqrt(d * d + f * f);
        projectile.setVelocity(d, e + g * 0.1, f, 1.35F, (float) (14 - this.bossSlime.getWorld().getDifficulty().getId() * 4));

        this.bossSlime.getWorld().spawnEntity(projectile);
        spawnParticleCircleAroundProjectile(projectile, world);
    }

    private void performSingleRotatedShot(float angle) {
        LivingEntity target = bossSlime.getTarget();
        if (target == null) return;

        Vec3d spawn = this.bossSlime.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 3.0f);
        spawn = VectorUtils.rotateVectorCC(spawn, this.bossSlime.getRotationVector(), angle);

        this.performSingleShot(spawn.add(0, 3, 0));
    }

    private void performSingleShot() {
        this.performSingleShot(new Vec3d(0, 3, 0));
    }

    private void performSingleShot(Vec3d offset) {
        LivingEntity target = bossSlime.getTarget();
        if (target == null) return;

        Vec3d spawn = this.bossSlime.getPos();

        if (this.bossSlime.getWorld() instanceof ServerWorld serverWorld) {
            spawnParticleCircle(serverWorld, BlockPos.ofFloored(spawn).withY((int) this.bossSlime.getY()), ParticleTypes.FLAME, 1, 50);
        }

        this.shootSkullAt(offset, target);
    }

    public void spawnParticleCircle(ServerWorld world, BlockPos center, ParticleEffect particle, double radius, int particleCount) {
        double centerX = center.getX();
        double centerY = center.getY();
        double centerZ = center.getZ();

        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);

            world.spawnParticles(particle, x, centerY, z, 1, 0, 0, 0, 0);
        }
    }

    private void spawnParticleCircleAroundProjectile(SlimeBallProjectile projectile, ServerWorld world) {
        if (projectile == null) return;

        LivingEntity target = bossSlime.getTarget();
        if (target == null) return;


        Vec3d projectilePos = projectile.getPos();
        Vec3d targetPos = target.getPos();
        Vec3d normal = targetPos.subtract(projectilePos).normalize();

        // Update these vectors to make particle spawn positions smoother
        Vec3d up = new Vec3d(0, 1, 0);
        Vec3d right = normal.crossProduct(up).normalize();
        Vec3d forward = right.crossProduct(normal).normalize();

        int particleCount = 12;
        double radius = 0.85;

        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;

            // Smoothly add to the position for better effect
            Vec3d circlePos = projectilePos.add(
                    right.multiply(radius * Math.cos(angle))
                            .add(forward.multiply(radius * Math.sin(angle)))
            );

            // Spawn particles with a smooth fade effect
            world.spawnParticles(
                    ParticleTypes.SMALL_FLAME,
                    circlePos.x, circlePos.y, circlePos.z,
                    20,
                    0, 0, 0,
                    0.01
            );
        }
    }
}
