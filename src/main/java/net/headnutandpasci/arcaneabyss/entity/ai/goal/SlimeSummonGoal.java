package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.BlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.green.GreenSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.red.RedSlimeEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class SlimeSummonGoal extends Goal {
    private static final ImmutableList<Direction> MOB_SUMMON_POS = ImmutableList.of(
            Direction.SOUTH,
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST
    );

    private final BlackSlimeEntity bossSlime;

    public SlimeSummonGoal(BlackSlimeEntity bossSlime) {
        this.bossSlime = bossSlime;
    }

    @Override
    public boolean canStart() {
        if (bossSlime.isInState(ArcaneBossSlime.State.SUMMON) && bossSlime.hasTarget()) {
            return this.canSummonSlimes();
        }
        return false;
    }

    @Override
    public boolean shouldContinue() {
        return bossSlime.isInState(ArcaneBossSlime.State.SUMMON) && bossSlime.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        bossSlime.setAttackTimer(100);
    }

    @Override
    public void tick() {
        ServerWorld world = bossSlime.getWorld() instanceof ServerWorld ? ((ServerWorld) bossSlime.getWorld()) : null;
        if (bossSlime.getAttackTimer() == 100) {
            bossSlime.setAttackTimer(99);
            /*blackSlimeEntity.playSound(blackSlimeEntity.getScreechSound(), 2.0F, 1.0F);*/
            double d = bossSlime.getX();
            double e = bossSlime.getY();
            double f = bossSlime.getZ();
            ((ServerWorld) bossSlime.getWorld()).spawnParticles(ParticleTypes.FLAME, d, e, f, 20, 3.0D, 3.0D, 3.0D, 0.0D);


            for (int i = 0; i < 2; i++) {
                if (bossSlime.getPhase() == 1) {
                    Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
                    BlockPos summonPos = bossSlime.getBlockPos().offset(direction, 5);
                    ModEntities.BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                    ModEntities.BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                    ModEntities.RED_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);

                }
                if (bossSlime.getPhase() == 2) {
                    Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
                    BlockPos summonPos = bossSlime.getBlockPos().offset(direction, 5);
                    ModEntities.BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                    ModEntities.BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                    ModEntities.RED_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                    ModEntities.RED_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                    ModEntities.GREEN_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);

                }
            }
        }


        if (bossSlime.getAttackTimer() == 0) {
            bossSlime.stopAttacking(0);
        }
    }

    private boolean canSummonSlimes() {
        System.out.println("Can Summon Slimes");
        System.out.println("is empty: " + this.bossSlime.getSummonedMobIds().isEmpty());
        return this.bossSlime.getSummonedMobIds().isEmpty();
    }

    private void summonMob(int mobIndex, BlockPos summonPos) {
        World world = bossSlime.getWorld();
        double d = summonPos.getX();
        double e = (double) summonPos.getY() + 1;
        double f = summonPos.getZ();
        ((ServerWorld) bossSlime.getWorld()).spawnParticles(ParticleTypes.CLOUD, d, e, f, 10, 0.5D, 0.5D, 0.5D, 0.0D);
        ArcaneSlimeEntity slime = switch (mobIndex) {
            case 1 -> new BlueSlimeEntity(ModEntities.BLUE_SLIME, world);
            case 2 -> new GreenSlimeEntity(ModEntities.GREEN_SLIME, world);
            case 3 -> new RedSlimeEntity(ModEntities.RED_SLIME, world);
            default -> null;
        };

        if (slime == null) return;
        slime.teleport(summonPos.getX(), summonPos.getY(), summonPos.getZ());
        disableDrops(slime);
        world.spawnEntity(slime);

        this.bossSlime.getSummonedMobIds().add(slime.getId());
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
