package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import net.headnutandpasci.arcaneabyss.entity.projectile.BlackSlimeProjectileEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.util.Math.VectorUtils;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SlimeShootGoal extends Goal {
    private final BlackSlimeEntity blackSlimeEntity;
    @Nullable
    private String type;

    private int rotatedShootAmount = 0;

    public SlimeShootGoal(BlackSlimeEntity blackSlimeEntity) {
        this.blackSlimeEntity = blackSlimeEntity;
    }

    @Override
    public boolean canStart() {
        int playerInArea = this.blackSlimeEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(this.blackSlimeEntity.getBlockPos()).expand(7), player -> !player.isInvulnerable()).size();
        return (blackSlimeEntity.isAttacking(BlackSlimeEntity.State.SHOOT_SLIME_BULLET)) && blackSlimeEntity.getTarget() != null && playerInArea == 0;
    }

    @Override
    public void start() {
        super.start();
        int DURATION = 200;
        blackSlimeEntity.setAttackTimer(DURATION);
        if (blackSlimeEntity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl moveControl) {
            moveControl.setDisabled(true);
        }

        this.type = this.rollType();
        System.out.println(this.type);
    }

    @Override
    public void stop() {
        super.stop();
        if (blackSlimeEntity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl moveControl) {
            moveControl.setDisabled(false);
        }
    }

    private String rollType() {
        WeightedRandomBag<String> bulletPatterns = new WeightedRandomBag<>();
        if (blackSlimeEntity.getState() == BlackSlimeEntity.State.SHOOT_SLIME_BULLET) {
            blackSlimeEntity.triggerRangeAttackAnimation();

            System.out.println(blackSlimeEntity.getPhase());
            if (blackSlimeEntity.getPhase() == 1) {
                bulletPatterns.addEntry("Single", 1);
                bulletPatterns.addEntry("MultiShot", 1);
            } else if (blackSlimeEntity.getPhase() == 2) {
                bulletPatterns.addEntry("RapidSingle", 1);
                bulletPatterns.addEntry("RapidMultiShot", 1);
            }
        }

        return bulletPatterns.getRandom();
    }

    @Override
    public void tick() {
        if (type == null) this.type = this.rollType();
        if (type == null) return;
        if (blackSlimeEntity.getAttackTimer() == 0) {
            blackSlimeEntity.stopAttacking(100);
            this.rotatedShootAmount = 0;
            return;
        }

        switch (type) {
            case "Single" -> {
                if (blackSlimeEntity.getAttackTimer() % 20 == 0) performSingleShot();
            }
            case "RapidSingle" -> {
                if (blackSlimeEntity.getAttackTimer() % 10 == 0) performSingleShot();
            }
            case "MultiShot" -> {
                if (blackSlimeEntity.getAttackTimer() % 20 == 0) {
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount)));
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) + 180));
                }
            }
            case "RapidMultiShot" -> {
                if (blackSlimeEntity.getAttackTimer() % 10 == 0) {
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount + 120)));
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) + 240));
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) + 360));
                    this.rotatedShootAmount++;
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    private void shootSkullAt(Vec3d spawn, Vec3d direction) {
        ServerWorld world = blackSlimeEntity.getWorld() instanceof ServerWorld ? ((ServerWorld) blackSlimeEntity.getWorld()) : null;
        if (world == null) return; // Ensure the world is a ServerWorld instance

        double g = direction.x - spawn.x;
        double h = direction.y - spawn.y - 4.0f;
        double i = direction.z - spawn.z;

        // Create and spawn the projectile
        BlackSlimeProjectileEntity witherSkullEntity = new BlackSlimeProjectileEntity(this.blackSlimeEntity.getWorld(), this.blackSlimeEntity, g, h, i);
        witherSkullEntity.setOwner(this.blackSlimeEntity);
        witherSkullEntity.setPos(spawn.x, spawn.y + 4.0f, spawn.z);
        this.blackSlimeEntity.getWorld().spawnEntity(witherSkullEntity);

        // Add particle circle effect around the projectile
        spawnParticleCircleAroundProjectile(witherSkullEntity, world);
    }

    private void performSingleRotatedShot(float angle) {
        LivingEntity target = blackSlimeEntity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.blackSlimeEntity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 3.0f);
        spawn = VectorUtils.rotateVectorCC(spawn, this.blackSlimeEntity.getRotationVector(), angle);

        double playerX = target.getX();
        double playerZ = target.getZ();
        double playerY = blackSlimeEntity.getY();


        ServerWorld world = target.getWorld() instanceof ServerWorld ? (ServerWorld) target.getWorld() : null;
        if (world != null) {

            BlockPos groundPos = new BlockPos((int) playerX, (int) playerY, (int) playerZ);

            spawnParticleCircle(world, groundPos, ParticleTypes.GLOW, 1, 50);

        }

        this.performSingleShot(spawn.add(0, 3, 0));
    }

    private void performSingleShot() {
        this.performSingleShot(new Vec3d(0, 0, 0));
    }

    private void performSingleShot(Vec3d offset) {
        LivingEntity target = blackSlimeEntity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.blackSlimeEntity.getPos();
        Vec3d direction = new Vec3d(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ());

        double playerX = target.getX();
        double playerZ = target.getZ();
        double playerY = blackSlimeEntity.getY();


        ServerWorld world = target.getWorld() instanceof ServerWorld ? (ServerWorld) target.getWorld() : null;
        if (world != null) {

            BlockPos groundPos = new BlockPos((int) playerX, (int) playerY, (int) playerZ);

            spawnParticleCircle(world, groundPos, ParticleTypes.GLOW, 1, 50);

        }

        this.shootSkullAt(spawn.add(offset), direction);
    }

    public void spawnParticleCircle(ServerWorld world, BlockPos center, ParticleEffect particle, double radius, int particleCount) {

        double centerX = center.getX();
        double centerY = center.getY();
        double centerZ = center.getZ();

        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);
            double y = centerY;

            world.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    private void spawnParticleCircleAroundProjectile(BlackSlimeProjectileEntity projectile, ServerWorld world) {
        if (projectile == null) return;

        LivingEntity target = blackSlimeEntity.getTarget();
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
