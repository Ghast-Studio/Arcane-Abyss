package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.entity.projectile.BlackSlimeProjectileEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.util.Math.VectorUtils;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
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
        //return "RapidMultiShot";
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
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount + 90)));
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) - 90));
                    this.rotatedShootAmount++;
                }
            }
            default -> throw new IllegalStateException("Unexpected value: " + type);
        }
    }

    private void shootSkullAt(Vec3d spawn, Vec3d direction) {
        double g = direction.x - spawn.x;
        double h = direction.y - spawn.y - 4.0f;
        double i = direction.z - spawn.z;
        BlackSlimeProjectileEntity witherSkullEntity = new BlackSlimeProjectileEntity(this.blackSlimeEntity.getWorld(), this.blackSlimeEntity, g, h, i);
        witherSkullEntity.setOwner(this.blackSlimeEntity);
        witherSkullEntity.setPos(spawn.x, spawn.y + 4.0f, spawn.z);
        this.blackSlimeEntity.getWorld().spawnEntity(witherSkullEntity);
    }

    private void performSingleRotatedShot(float angle) {
        LivingEntity target = blackSlimeEntity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.blackSlimeEntity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 3.0f);
        spawn = VectorUtils.rotateVectorCC(spawn, this.blackSlimeEntity.getRotationVector(), angle);

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

        this.shootSkullAt(spawn.add(offset), direction);
    }
}

