package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.util.Math.VectorUtils;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.math.Vec3d;

public class SlimeShootGoal extends Goal {
    private final BlackSlimeEntity blackSlimeEntity;
    private final int DURATION = 126; // Must be even duration since bullet are shot at even ticks
    private String type;

    public SlimeShootGoal(BlackSlimeEntity blackSlimeEntity) {
        this.blackSlimeEntity = blackSlimeEntity;
    }

    @Override
    public boolean canStart() {
        return (blackSlimeEntity.isAttacking(BlackSlimeEntity.State.SHOOT_SLIME_BULLET)) && blackSlimeEntity.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        blackSlimeEntity.setAttackTimer(DURATION);
        WeightedRandomBag<String> bulletPatterns = new WeightedRandomBag<>();
        if (blackSlimeEntity.getState() == BlackSlimeEntity.State.SHOOT_SLIME_BULLET) {
            blackSlimeEntity.triggerRangeAttackAnimation();
            bulletPatterns.addEntry("Single", 1);

            /**if (blackSlimeEntity.getPhase() == 1) {
             bulletPatterns.addEntry("Single", 1);
             bulletPatterns.addEntry("Arc", 1);
             } else if (blackSlimeEntity.getPhase() == 2) {
             bulletPatterns.addEntry("Rapid", 1);
             bulletPatterns.addEntry("Strong Arc", 1);
             }
             } else {
             blackSlimeEntity.triggerRangeBurstAttackAnimation();
             /**if (blackSlimeEntity.getPhase() == 1) {
             bulletPatterns.addEntry("Burst", 1);
             } else if (blackSlimeEntity.getPhase() == 2) {
             bulletPatterns.addEntry("Strong Burst", 1);
             }**/
        }
        type = bulletPatterns.getRandom();
    }

    @Override
    public void tick() {
        if (type.equals("Single")) {
            for (int i = 0; i < 5; i++) {
                if (blackSlimeEntity.getAttackTimer() == 100 - i * 20) {
                    performArcShot();
                }
            }
        } else if (type.equals("Arc")) {
            for (int i = 0; i < 5; i++) {
                if (blackSlimeEntity.getAttackTimer() == 100 - i * 20) {
                    performArcShot();
                }
            }
        }

        if (blackSlimeEntity.getAttackTimer() == 0) {
            blackSlimeEntity.stopAttacking(60);
        }
    }

    private void shootSkullAt(Vec3d spawn, Vec3d direction) {
        double g = direction.x - spawn.x;
        double h = direction.y - spawn.y - 4.0f;
        double i = direction.z - spawn.z;
        WitherSkullEntity witherSkullEntity = new WitherSkullEntity(this.blackSlimeEntity.getWorld(), this.blackSlimeEntity, g, h, i);
        witherSkullEntity.setOwner(this.blackSlimeEntity);
        witherSkullEntity.setPos(spawn.x, spawn.y + 4.0f, spawn.z);
        this.blackSlimeEntity.getWorld().spawnEntity(witherSkullEntity);
    }

    private void performArcShot() {
        LivingEntity target = blackSlimeEntity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.blackSlimeEntity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 3.0f);

        int bulletCount = 8;
        for (int i = 0; i < bulletCount; i++) {
            spawn = VectorUtils.rotateVectorCC(spawn, this.blackSlimeEntity.getRotationVector(), (float) Math.toRadians((double) 360 / bulletCount) * i);
            Vec3d direction = new Vec3d(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ());
            this.shootSkullAt(this.blackSlimeEntity.getPos().add(spawn), direction);
        }
    }

    private void performArcTimedShot(float angle) {
        LivingEntity target = blackSlimeEntity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.blackSlimeEntity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 3.0f);
        spawn = VectorUtils.rotateVectorCC(spawn, this.blackSlimeEntity.getRotationVector(), angle);

        Vec3d direction = new Vec3d(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ());
        this.shootSkullAt(this.blackSlimeEntity.getPos().add(spawn), direction);
    }

    private void performSingleShot() {
        LivingEntity target = blackSlimeEntity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.blackSlimeEntity.getPos();
        Vec3d direction = new Vec3d(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ());

        this.shootSkullAt(spawn, direction);
    }
}

