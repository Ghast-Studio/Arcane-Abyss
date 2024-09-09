package net.headnutandpasci.arcaneabyss.entity.ai;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.DarkBlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.SlimePillarEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SlimeviathanSummonPillerGoal extends Goal {
    private static final ImmutableList<Direction> MOB_SUMMON_POS = ImmutableList.of(
            Direction.SOUTH,
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST
    );


    private final SlimeviathanEntity entity;
    private ArcaneSlimeEntity.ArcaneSlimeMoveControl moveControl;


    public SlimeviathanSummonPillerGoal(SlimeviathanEntity entity) {
        this.entity = entity;


    }


    @Override
    public boolean canStart() {
        if (entity.isAttacking(SlimeviathanEntity.State.PILLAR_SUMMON) && entity.getTarget() != null) {
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
        return entity.isAttacking(SlimeviathanEntity.State.PILLAR_SUMMON) && entity.getTarget() != null;
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
            double d = entity.getX();
            double e = entity.getY();
            double f = entity.getZ();
            ((ServerWorld) entity.getWorld()).spawnParticles(ParticleTypes.FLAME, d, e, f, 20, 3.0D, 3.0D, 3.0D, 0.0D);


            int s = 4;
            int k = 4;


            for (int i = 0; i < s; i++) {
                WeightedRandomBag<Integer> mobWeightBag = new WeightedRandomBag<>();
                mobWeightBag.addEntry(1, 1);

                Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
                BlockPos summonPos = entity.getBlockPos().offset(direction, 10);
                summonMob(mobWeightBag.getRandom(), summonPos);


            }
            for (int i = 0; i < k; i++) {
                WeightedRandomBag<Integer> mobWeightBag = new WeightedRandomBag<>();

                mobWeightBag.addEntry(2, 1);
                Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
                BlockPos summonPos = entity.getBlockPos().offset(direction, 5);
                summonMob(mobWeightBag.getRandom(), summonPos);


            }


        }


        if (entity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl moveControl) {
            moveControl.setDisabled(true);
        }


    }

    private boolean canSummonSlimes() {
        return this.entity.getSummonedPillarIds().isEmpty();
    }

    private void summonMob(int mobIndex, BlockPos summonPos) {
        World world = entity.getWorld();
        double d = summonPos.getX();
        double e = (double) summonPos.getY() + 1;
        double f = summonPos.getZ();
        ((ServerWorld) entity.getWorld()).spawnParticles(ParticleTypes.CLOUD, d, e, f, 10, 0.5D, 0.5D, 0.5D, 0.0D);
        ArcaneSlimeEntity slime = switch (mobIndex) {
            case 1 -> new SlimePillarEntity(ModEntities.SLIME_PILLAR, world);
            case 2 -> new DarkBlueSlimeEntity(ModEntities.DARK_BLUE_SLIME, world);
            default -> null;
        };

        if (slime == null) return;
        slime.teleport(summonPos.getX(), summonPos.getY(), summonPos.getZ());
        disableDrops(slime);
        world.spawnEntity(slime);

        this.entity.getSummonedPillarIds().add(slime.getId());
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
