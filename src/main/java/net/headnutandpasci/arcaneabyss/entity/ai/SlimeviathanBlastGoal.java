package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.entity.projectile.BlackSlimeProjectileEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.util.Math.VectorUtils;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class SlimeviathanBlastGoal extends Goal {
    private final SlimeviathanEntity slimeviathanEntity;

    @Nullable
    private String type;

    private int rotatedShootAmount = 0;

    public SlimeviathanBlastGoal(SlimeviathanEntity slimeviathanEntity) {
        this.slimeviathanEntity = slimeviathanEntity;
    }

    @Override
    public boolean canStart() {
        int playerInArea = this.slimeviathanEntity.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(this.slimeviathanEntity.getBlockPos()).expand(2), player -> !player.isInvulnerable()).size();
        return (slimeviathanEntity.isAttacking(SlimeviathanEntity.State.SHOOT_SLIME_BULLET)) && slimeviathanEntity.getTarget() != null && playerInArea == 0;
    }

    @Override
    public void start() {
        super.start();
        int DURATION = 200;
        slimeviathanEntity.setAttackTimer(DURATION);
        if (slimeviathanEntity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl moveControl) {
            moveControl.setDisabled(true);
        }

        this.type = this.rollType();
        System.out.println(this.type);
    }

    @Override
    public void stop() {
        super.stop();
        if (slimeviathanEntity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl moveControl) {
            moveControl.setDisabled(false);
        }
    }

    private String rollType() {
        WeightedRandomBag<String> bulletPatterns = new WeightedRandomBag<>();
        if (slimeviathanEntity.getState() == SlimeviathanEntity.State.SHOOT_SLIME_BULLET) {
            slimeviathanEntity.triggerRangeAttackAnimation();

            System.out.println(slimeviathanEntity.getPhase());
            if (slimeviathanEntity.getPhase() == 1) {
                bulletPatterns.addEntry("Single", 1);
                bulletPatterns.addEntry("MultiShot", 1);
            } else if (slimeviathanEntity.getPhase() == 2) {
                bulletPatterns.addEntry("RapidDouble", 1);
                bulletPatterns.addEntry("RapidMultiShot", 1);
            }
        }

        return bulletPatterns.getRandom();
        //return "RapidMultiShot";
    }

    int counter = 0;

    @Override
    public void tick() {
        if (type == null) this.type = this.rollType();
        if (type == null) return;
        if (slimeviathanEntity.getAttackTimer() == 0) {
            slimeviathanEntity.stopAttacking(100);
            this.rotatedShootAmount = 0;
            return;
        }

        switch (type) {
            case "Single" -> {
                if (slimeviathanEntity.getAttackTimer() % 5 == 0) performSingleShot();
            }
            case "RapidDouble" -> {
                if (slimeviathanEntity.getAttackTimer() % 2.5 == 0){
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount)));
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) + 180));
                    this.rotatedShootAmount++;
                }
            }
            case "MultiShot" -> {
                if (slimeviathanEntity.getAttackTimer() % 6 == 0) {
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) + 120));
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) + 240));
                    performSingleRotatedShot((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) + 360));
                    this.rotatedShootAmount++;
                }
            }
            case "RapidMultiShot" -> {
                if (slimeviathanEntity.getAttackTimer() % 2.5 == 0) {
                    performSingleRotatedShotOne((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount)));
                    performSingleRotatedShotOne((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) + 180));
                    this.rotatedShootAmount++;
                    performSingleRotatedShotTwo((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount)));
                    performSingleRotatedShotTwo((float) Math.toRadians(((360f / 10) * this.rotatedShootAmount) + 180));
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
        BlackSlimeProjectileEntity witherSkullEntity = new BlackSlimeProjectileEntity(this.slimeviathanEntity.getWorld(), this.slimeviathanEntity, g, h, i);
        witherSkullEntity.setOwner(this.slimeviathanEntity);
        witherSkullEntity.setPos(spawn.x, spawn.y + 4.0f, spawn.z);
        this.slimeviathanEntity.getWorld().spawnEntity(witherSkullEntity);
    }

    private void performSingleRotatedShot(float angle) {
        LivingEntity target = slimeviathanEntity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.slimeviathanEntity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 4.0f);
        spawn = VectorUtils.rotateVectorCC(spawn, this.slimeviathanEntity.getRotationVector(), angle);

        this.performSingleShot(spawn.add(0, 8, 0));
    }

    private void performSingleRotatedShotOne(float angle) {
        LivingEntity target = slimeviathanEntity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.slimeviathanEntity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 4.0f);
        spawn = VectorUtils.rotateVectorCC(spawn, this.slimeviathanEntity.getRotationVector(), angle);

        this.performSingleShot(spawn.add(-10, 8, 0));
    }


    private void performSingleRotatedShotTwo(float angle) {
        LivingEntity target = slimeviathanEntity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.slimeviathanEntity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 4.0f);
        spawn = VectorUtils.rotateVectorCC(spawn, this.slimeviathanEntity.getRotationVector(), angle);

        this.performSingleShot(spawn.add(10, 8, 0));
    }

    private void performSingleShot() {
        this.performSingleShot(new Vec3d(0, 0, 0));
    }

    private void performSingleShot(Vec3d offset) {
        LivingEntity target = slimeviathanEntity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.slimeviathanEntity.getPos();
        Vec3d direction = new Vec3d(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ());

        this.shootSkullAt(spawn.add(offset), direction);
    }
}

