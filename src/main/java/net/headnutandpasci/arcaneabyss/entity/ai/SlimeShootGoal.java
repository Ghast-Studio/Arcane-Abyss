package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.projectile.BlackSlimeProjectileEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.util.Math.VectorUtils;
import net.headnutandpasci.arcaneabyss.util.random.WeightedBag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;

public class SlimeShootGoal extends Goal {
    private final BlackSlimeEntity entity;
    private final int DURATION = 126; // Must be even duration since bullet are shot at even ticks

    @Nullable
    private BulletType selectedBulletType;

    private final EnumMap<BulletType, Integer> bulletTypePhases;

    public SlimeShootGoal(BlackSlimeEntity entity, EnumMap<BulletType, Integer> bulletTypePhases) {
        this.entity = entity;
        this.bulletTypePhases = bulletTypePhases;
    }

    @Override
    public boolean canStart() {
        return (entity.isAttacking(BlackSlimeEntity.State.SHOOT_SLIME_BULLET)) && entity.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        entity.setAttackTimer(DURATION);
        WeightedBag<BulletType> bag = new WeightedBag<>();

        if (entity.isInState(BlackSlimeEntity.State.SHOOT_SLIME_BULLET)) {
            this.getBulletTypePhases().forEach((bulletType, phase) -> {
                if (entity.isInPhase(phase)) bag.addEntry(bulletType, bulletType.getChance());
            });
        }

        this.setSelectedBulletType(bag.getRandom());
    }

    @Override
    public void tick() {
        if(this.getSelectedBulletType() == null) return;

        switch (this.getSelectedBulletType()) {
            case SINGLE -> {
                for (int i = 0; i < 10; i++) {
                    if (entity.getAttackTimer() == 150 - i * 15) {
                        performSingleShot();
                    }
                }
            }
            case RAPID_SINGLE -> {
                for (int i = 0; i < 20; i++) {
                    if (entity.getAttackTimer() == 100 - i * 3) {
                        performSingleShot();
                    }
                }
            }
            case MULTI -> {
                for (int i = 0; i < 10; i++) {
                    if (entity.getAttackTimer() == 100 - i * 15) {
                        performMultiShot();
                    }
                }
            }
            case RAPID_MULTI -> {
                for (int i = 0; i < 20; i++) {
                    if (entity.getAttackTimer() == 100 - i * 3) {
                        performMultiShot();
                    }
                }
            }
            default -> ArcaneAbyss.LOGGER.error("Invalid bullet type selected");
        }

        if (entity.getAttackTimer() == 0) {
            entity.stopAttacking(0);
        }
    }

    private void shootSkullAt(Vec3d spawn, Vec3d direction) {
        double dstX = direction.x - spawn.x;
        double dstY = direction.y - spawn.y - 4.0f;
        double dstZ = direction.z - spawn.z;

        BlackSlimeProjectileEntity witherSkullEntity = new BlackSlimeProjectileEntity(this.entity.getWorld(), this.entity, dstX, dstY, dstZ);
        witherSkullEntity.setOwner(this.entity);
        witherSkullEntity.setPos(spawn.x, spawn.y + 4.0f, spawn.z);
        this.entity.getWorld().spawnEntity(witherSkullEntity);
    }

    private void performMultiShot() {
        LivingEntity target = entity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.entity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 3.0f);

        int bulletCount = 2;
        for (int i = 0; i < bulletCount; i++) {
            spawn = VectorUtils.rotateVectorCC(spawn, this.entity.getRotationVector(), (float) Math.toRadians((double) 360 / bulletCount) * i);
            Vec3d direction = new Vec3d(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ());
            this.shootSkullAt(this.entity.getPos().add(spawn), direction);

        }
    }

    private void performArcTimedShot(float angle) {
        LivingEntity target = entity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.entity.getRotationVector();
        spawn = VectorUtils.addRight(spawn, 3.0f);
        spawn = VectorUtils.rotateVectorCC(spawn, this.entity.getRotationVector(), angle);

        Vec3d direction = new Vec3d(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ());
        this.shootSkullAt(this.entity.getPos().add(spawn), direction);
    }

    private void performSingleShot() {
        LivingEntity target = entity.getTarget();
        if (target == null) return;

        Vec3d spawn = this.entity.getPos();
        Vec3d direction = new Vec3d(target.getX(), target.getY() + (double) target.getStandingEyeHeight() * 0.5, target.getZ());

        this.shootSkullAt(spawn, direction);
    }

    public void setSelectedBulletType(BulletType selectedBulletType) {
        this.selectedBulletType = selectedBulletType;
    }

    public BulletType getSelectedBulletType() {
        return selectedBulletType;
    }

    public EnumMap<BulletType, Integer> getBulletTypePhases() {
        return bulletTypePhases;
    }

    public enum BulletType {
        SINGLE(0),
        MULTI(0),
        RAPID_SINGLE(0),
        RAPID_MULTI(0);

        private final int chance;

        BulletType(int chance) {
            this.chance = chance;
        }

        public int getChance() {
            return chance;
        }
    }
}

