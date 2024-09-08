package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.entity.projectile.BlackSlimeProjectileEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.util.Math.VectorUtils;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.Timer;
import java.util.TimerTask;

public class SlimeviathanBlastGoal extends Goal {
    private final SlimeviathanEntity slimeviathanEntity;
    private final int DURATION = 126; // Must be even duration since bullet are shot at even ticks
    private String type;
    private final Timer timer = new Timer();
    public SlimeviathanBlastGoal(SlimeviathanEntity slimeviathanEntity) {
        this.slimeviathanEntity = slimeviathanEntity;
    }

    @Override
    public boolean canStart() {
        return (slimeviathanEntity.isAttacking(SlimeviathanEntity.State.SHOOT_SLIME_BULLET)) && slimeviathanEntity.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        slimeviathanEntity.setAttackTimer(DURATION);
        WeightedRandomBag<String> bulletPatterns = new WeightedRandomBag<>();
        if (slimeviathanEntity.getState() == SlimeviathanEntity.State.SHOOT_SLIME_BULLET) {
            slimeviathanEntity.triggerRangeAttackAnimation();

            if (slimeviathanEntity.getPhase() == 1) {
                bulletPatterns.addEntry("FourShotKill", 1);
            }
            if (slimeviathanEntity.getPhase() == 1) {
                bulletPatterns.addEntry("Deathplosion", 1);
            }
            if (slimeviathanEntity.getPhase() == 2) {
                bulletPatterns.addEntry("BuffedEightShotKill", 1);
            }
            if (slimeviathanEntity.getPhase() == 2) {
                bulletPatterns.addEntry("BuffedDeathplosion", 1);
            }

            if (slimeviathanEntity.getPhase() == 3) {
                bulletPatterns.addEntry("RapidSingle", 1);
            }
            if (slimeviathanEntity.getPhase() == 3) {
                bulletPatterns.addEntry("RapidMultiShot", 1);
            }

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

        if(this.type == null){
            return;
        }


        if (type.equals("FourShotKill")) {
            for (int i = 0; i < 10; i++) {
                if (slimeviathanEntity.getAttackTimer() == 100 - i * 10) { //Für zweite Phase
                    performFourShotKill();
                }
            }
        }

        if (slimeviathanEntity.getAttackTimer() == 0) {
            slimeviathanEntity.stopAttacking(0);
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



    private void performDeathplosion() {
        LivingEntity target = slimeviathanEntity.getTarget();
        if (target == null) return;



        Vec3d spawn = this.slimeviathanEntity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 3.0f);

        int bulletCount = 1;
        for (int i = 0; i < bulletCount; i++) {
            spawn = VectorUtils.rotateVectorCC(spawn, this.slimeviathanEntity.getRotationVector(), (float) Math.toRadians((double) 360 / bulletCount) * i);
            Vec3d direction = new Vec3d(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ());
            this.shootSkullAt(this.slimeviathanEntity.getPos().add(spawn), direction);

        }
    }

    /**private void performArcTimedShot(float angle) {
        LivingEntity target = blackSlimeEntity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.blackSlimeEntity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 3.0f);
        spawn = VectorUtils.rotateVectorCC(spawn, this.blackSlimeEntity.getRotationVector(), angle);

        Vec3d direction = new Vec3d(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ());
        this.shootSkullAt(this.blackSlimeEntity.getPos().add(spawn), direction);
    }
    **/

    private void performFourShotKill() {
        LivingEntity target = slimeviathanEntity.getTarget();
        if (target == null) return;


        Vec3d spawn = this.slimeviathanEntity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 3.0f);

        int bulletCount = 2;
        for (int i = 0; i < bulletCount; i++) {
            spawn = VectorUtils.rotateVectorCC(spawn, this.slimeviathanEntity.getRotationVector(), (float) Math.toRadians((double) 360 / bulletCount) * i);
            Vec3d direction = new Vec3d(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ());
            this.shootSkullAt(this.slimeviathanEntity.getPos().add(spawn), direction);
        }
    }
}

