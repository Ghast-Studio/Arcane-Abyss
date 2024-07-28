package net.headnutandpasci.arcaneabyss.entity.ai;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.BlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.green.GreenSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeEntity;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SlimeSummonGoal extends Goal {
    private static final ImmutableList<Direction> MOB_SUMMON_POS = ImmutableList.of(
            Direction.SOUTH,
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST
    );

    private final BlackSlimeEntity entity;
    private final CopyOnWriteArrayList<Integer> aliveMobIds;
    private int maxSummonLimit;

    public SlimeSummonGoal(BlackSlimeEntity entity) {
        this.entity = entity;
        this.aliveMobIds = new CopyOnWriteArrayList<>();
    }

    @Override
    public boolean canStart() {
        if (entity.isAttacking(BlackSlimeEntity.State.SUMMON) && entity.getTarget() != null) {
            if (canSummonSlimes()) {
                return true;
            } else {
                entity.stopAttacking(0);
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return entity.isAttacking(BlackSlimeEntity.State.SUMMON) && entity.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        entity.setAttackTimer(100);
    }

    @Override
    public void tick() {
        if (entity.getAttackTimer() == 100) {
            entity.setAttackTimer(99);
            /*blackSlimeEntity.playSound(blackSlimeEntity.getScreechSound(), 2.0F, 1.0F);*/
            int summonCount = Math.max(0, maxSummonLimit);
            double d = entity.getX();
            double e = entity.getY();
            double f = entity.getZ();
            ((ServerWorld) entity.getWorld()).spawnParticles(ParticleTypes.FLAME, d, e, f, 20, 3.0D, 3.0D, 3.0D, 0.0D);
            for (int i = 0; i < 5; i++) {
                WeightedRandomBag<Integer> mobWeightBag = new WeightedRandomBag<>();
                mobWeightBag.addEntry(1, 1);
                mobWeightBag.addEntry(2, 1);
                mobWeightBag.addEntry(3, 1);
                Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
                BlockPos summonPos = entity.getBlockPos().offset(direction, 5);
                summonMob(mobWeightBag.getRandom(), summonPos);
            }
        }

        if (entity.getAttackTimer() == 0) {
            entity.stopAttacking(60);
        }
    }

    private boolean canSummonSlimes() {
        List<ArcaneSlimeEntity> entitiesInBox = entity.getWorld().getEntitiesByClass(ArcaneSlimeEntity.class, entity.getBoundingBox().expand(35.0D / 2), (mob) -> true);
        maxSummonLimit = 10;
        return (entitiesInBox.size() < maxSummonLimit) && this.aliveMobIds.isEmpty();
    }

    private void summonMob(int mobIndex, BlockPos summonPos) {
        World world = entity.getWorld();
        double d = summonPos.getX();
        double e = (double) summonPos.getY() + 1;
        double f = summonPos.getZ();
        ((ServerWorld) entity.getWorld()).spawnParticles(ParticleTypes.CLOUD, d, e, f, 10, 0.5D, 0.5D, 0.5D, 0.0D);
        ArcaneSlimeEntity slime = null;
        switch (mobIndex) {
            case 1: {
                slime = new BlueSlimeEntity(ModEntities.BLUE_SLIME, world);
                slime.teleport(summonPos.getX(), summonPos.getY(), summonPos.getZ());
                disableDrops(slime);
                world.spawnEntity(slime);
                break;
            }
            case 2: {
                slime = new GreenSlimeEntity(ModEntities.GREEN_SLIME, world);
                slime.teleport(summonPos.getX(), summonPos.getY(), summonPos.getZ());
                disableDrops(slime);
                world.spawnEntity(slime);
                break;
            }
            case 3: {
                slime = new RedSlimeEntity(ModEntities.RED_SLIME, world);
                slime.teleport(summonPos.getX(), summonPos.getY(), summonPos.getZ());
                disableDrops(slime);
                world.spawnEntity(slime);
                break;
            }
        }

        assert slime != null;
        this.entity.getSummonedMobIds().add(slime.getId());
    }

    private void disableDrops(HostileEntity entity) {
        entity.setEquipmentDropChance(EquipmentSlot.HEAD, 0.0F);
        entity.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
        entity.setEquipmentDropChance(EquipmentSlot.LEGS, 0.0F);
        entity.setEquipmentDropChance(EquipmentSlot.FEET, 0.0F);
        entity.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0F);
        entity.setEquipmentDropChance(EquipmentSlot.OFFHAND, 0.0F);
    }
}
