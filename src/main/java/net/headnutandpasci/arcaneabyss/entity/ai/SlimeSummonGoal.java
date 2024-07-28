package net.headnutandpasci.arcaneabyss.entity.ai;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.BlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.green.GreenSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeEntity;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class SlimeSummonGoal extends Goal {
    private static final ImmutableList<Direction> MOB_SUMMON_POS = ImmutableList.of(
            Direction.SOUTH,
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST
    );

    private final BlackSlimeEntity blackSlimeEntity;
    private final List<HostileEntity> mobList;
    private int maxSummonLimit;

    public SlimeSummonGoal(BlackSlimeEntity blackSlimeEntity) {
        this.blackSlimeEntity = blackSlimeEntity;
        this.mobList = new ArrayList<>();
    }

    @Override
    public boolean canStart() {
        if (blackSlimeEntity.isAttacking(BlackSlimeEntity.State.SUMMON) && blackSlimeEntity.getTarget() != null) {
            if (withinSummonLimit()) {
                return true;
            } else {
                blackSlimeEntity.stopAttacking(0);
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return blackSlimeEntity.isAttacking(BlackSlimeEntity.State.SUMMON) && blackSlimeEntity.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        blackSlimeEntity.setAttackTimer(100);
    }

    @Override
    public void tick() {
        if (blackSlimeEntity.getAttackTimer() == 100) {
            /*blackSlimeEntity.playSound(blackSlimeEntity.getScreechSound(), 2.0F, 1.0F);*/
            int summonCount = Math.max(0, maxSummonLimit);
            double d = blackSlimeEntity.getX();
            double e = blackSlimeEntity.getY();
            double f = blackSlimeEntity.getZ();
            ((ServerWorld) blackSlimeEntity.getWorld()).spawnParticles(ParticleTypes.FLAME, d, e, f, 20, 3.0D, 3.0D, 3.0D, 0.0D);
            for (int i = 0; i < summonCount; i++) {
                WeightedRandomBag<Integer> mobWeightBag = new WeightedRandomBag<>();
                mobWeightBag.addEntry(1, 2);
                mobWeightBag.addEntry(2, 1.5);
                mobWeightBag.addEntry(3, 1);
                summonMob(mobWeightBag.getRandom(), blackSlimeEntity.getBlockPos().offset(MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1))));
            }
        }
        if (blackSlimeEntity.getAttackTimer() == 0) {
            blackSlimeEntity.stopAttacking(60);
        }
    }

    private boolean withinSummonLimit() {
        List<HostileEntity> mobList = blackSlimeEntity.getWorld().getEntitiesByClass(HostileEntity.class, blackSlimeEntity.getBoundingBox().expand(35.0D / 2), (mob) -> true);
        maxSummonLimit = Math.min(2 + 4 * 2, 12);
        return mobList.size() < maxSummonLimit;
    }

    private void summonMob(int mobIndex, BlockPos summonPos) {
        World world = blackSlimeEntity.getWorld();
        double d = summonPos.getX();
        double e = (double) summonPos.getY() + 1;
        double f = summonPos.getZ();
        ((ServerWorld) blackSlimeEntity.getWorld()).spawnParticles(ParticleTypes.CLOUD, d, e, f, 10, 0.5D, 0.5D, 0.5D, 0.0D);
        switch (mobIndex) {
            case 1: {
                BlueSlimeEntity blue = new BlueSlimeEntity(ModEntities.BLUE_SLIME, world);
                blue.teleport(summonPos.getX(), summonPos.getY(), summonPos.getZ());
                disableDrops(blue);
                world.spawnEntity(blue);
                break;
            }
            case 2: {
                GreenSlimeEntity blue = new GreenSlimeEntity(ModEntities.GREEN_SLIME, world);
                blue.teleport(summonPos.getX(), summonPos.getY(), summonPos.getZ());
                disableDrops(blue);
                world.spawnEntity(blue);
                break;
            }
            case 3: {
                RedSlimeEntity red = new RedSlimeEntity(ModEntities.RED_SLIME, world);
                red.teleport(summonPos.getX(), summonPos.getY(), summonPos.getZ());
                disableDrops(red);
                world.spawnEntity(red);
                break;
            }
        }
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
