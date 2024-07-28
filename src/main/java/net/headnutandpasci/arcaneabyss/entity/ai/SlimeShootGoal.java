package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.MagmaBallProjectile;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundEvents;
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
                    performSingleShot(30.0f);
                }
            }
        }
        if (blackSlimeEntity.getAttackTimer() == 0) {
            blackSlimeEntity.stopAttacking(60);
        }
    }


    private void performSingleShot(float angle) {
        float offset = (float) Math.toRadians(angle);
        Vec3d vec3 = blackSlimeEntity.getRotationVec(0.0F);
        vec3 = vec3.rotateY(offset);
        blackSlimeEntity.getWorld().playSound(null, blackSlimeEntity.getX(), blackSlimeEntity.getY(), blackSlimeEntity.getZ(), SoundEvents.ENTITY_SKELETON_SHOOT, blackSlimeEntity.getSoundCategory(), 3.0F, 1.0F + (blackSlimeEntity.getRandom().nextFloat() - blackSlimeEntity.getRandom().nextFloat()) * 0.2F);
        LivingEntity target = blackSlimeEntity.getTarget();
        if(target == null) return;

        for (int i = 0; i < 8; i++) {
            vec3 = vec3.rotateY((float) Math.toRadians(45) * i);
            MagmaBallProjectile magmaBallEntity = new MagmaBallProjectile(this.blackSlimeEntity, this.blackSlimeEntity.getWorld());

            double dstX = target.getX() - this.blackSlimeEntity.getX();
            double dstY = target.getBodyY(0.3333333333333333) - magmaBallEntity.getY();
            double dstZ = target.getZ() - this.blackSlimeEntity.getZ();
            double distance = Math.sqrt(dstX * dstX + dstZ * dstZ);
            magmaBallEntity.setVelocity(dstX, dstY + (distance * 0.1), dstZ, 2.0f, (float) (14 - this.blackSlimeEntity.getWorld().getDifficulty().getId() * 4));
            magmaBallEntity.setPos(magmaBallEntity.getX() + vec3.x, magmaBallEntity.getY() + vec3.y, magmaBallEntity.getZ() + vec3.z);
            blackSlimeEntity.getWorld().spawnEntity(magmaBallEntity);
        }
    }
}

