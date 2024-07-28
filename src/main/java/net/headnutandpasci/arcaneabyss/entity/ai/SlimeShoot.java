package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.entity.projectile.BlackSlimeProjectile;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class SlimeShoot extends Goal {


        private final BlackSlimeEntity blackSlimeEntity;
        private final int DURATION = 126; // Must be even duration since bullet are shot at even ticks
        private String type;

        public SlimeShoot(BlackSlimeEntity blackSlimeEntity) {
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
            switch (type) {
                case "Single" -> {
                    for (int i = 0; i < 5; i++) {
                        if (blackSlimeEntity.getAttackTimer() == 100 - i * 20) { performSingleShot(); }
                    }
                }

            }
            if (blackSlimeEntity.getAttackTimer() == 0) {
                blackSlimeEntity.stopAttacking(60);
            }
        }

        /** this.getWorld().spawnEntity(magmaBallEntity); **/

        private void performSingleShot(float angle) {
            float offset = (float) Math.toRadians(angle);
            Vec3d vec3 = blackSlimeEntity.getViewVector(1.0F);
            vec3 = vec3.yRot(offset);
            blackSlimeEntity.getWorld().playSound(null, blackSlimeEntity.getX(), blackSlimeEntity.getY(), blackSlimeEntity.getZ(), SoundEvents.ENTITY_WITHER_SHOOT, blackSlimeEntity.getSoundCategory(), 3.0F, 1.0F + (blackSlimeEntity.getRandom().nextFloat() - blackSlimeEntity.getRandom().nextFloat()) * 0.2F);
            for (int i = 0; i < 8; i++) {
                vec3 = vec3.yRot((float) Math.toRadians(45)* i);
                BlackSlimeProjectile blackSlimeProjectile = new BlackSlimeProjectile(blackSlimeEntity, vec3.x, vec3.y,vec3.z);
                blackSlimeProjectile.setPos(blackSlimeProjectile.getX() + vec3.x, blackSlimeProjectile.getY(0.5) + vec3.y, blackSlimeProjectile.getZ() + vec3.z);
                blackSlimeEntity.getWorld().addFreshEntity(blackSlimeProjectile);
            }
        }

        private void vecFromCenterToFrontOfFace(float angle) {
            double viewDistance = 2.0F;
            Vec3d viewVector = blackSlimeEntity.getViewVector(1.0F);
            if (angle != 0.0F) {
                float offset = (float) Math.toRadians(angle);
                viewVector = viewVector.yRot(offset);
            }
            double d0 = viewVector.x * viewDistance;
            double d1 = viewVector.y * viewDistance;
            double d2 = viewVector.z * viewDistance;
            BlackSlimeProjectile blackSlimeProjectile = new BlackSlimeProjectile(blackSlimeEntity, d0, d1, d2);
            blackSlimeProjectile.setPos(blackSlimeProjectile.getX() + d0, blackSlimeProjectile.getY(0.5) + d1, blackSlimeProjectile.getZ() + d2);
            blackSlimeEntity.getWorld().addFreshEntity(blackSlimeProjectile);
        }
    }

