package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.BlueSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
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

public class SlimeviathanSummonGoal extends Goal {
    private static final ImmutableList<Direction> MOB_SUMMON_POS = ImmutableList.of(
            Direction.SOUTH,
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST
    );

    private final SlimeviathanEntity bossSlime;

    public SlimeviathanSummonGoal(SlimeviathanEntity bossSlime) {
        this.bossSlime = bossSlime;
    }

    @Override
    public boolean canStart() {
        System.out.println("can summon: " + (bossSlime.isInState(ArcaneBossSlime.State.SUMMON) && bossSlime.hasTarget() && this.canSummonSlimes()));
        return (bossSlime.isInState(ArcaneBossSlime.State.SUMMON) && bossSlime.hasTarget() && this.canSummonSlimes());
    }

    @Override
    public boolean shouldContinue() {
        return bossSlime.isInState(ArcaneBossSlime.State.SUMMON) && bossSlime.hasTarget();
    }

    @Override
    public void start() {
        super.start();
        ServerWorld world = bossSlime.getWorld() instanceof ServerWorld ? ((ServerWorld) bossSlime.getWorld()) : null;
        if (world == null) {
            System.out.println("World is null");
            return;
        }

        double d = bossSlime.getX();
        double e = bossSlime.getY();
        double f = bossSlime.getZ();
        ((ServerWorld) bossSlime.getWorld()).spawnParticles(ParticleTypes.FLAME, d, e, f, 20, 3.0D, 3.0D, 3.0D, 0.0D);

        for (int i = 0; i < 2; i++) {
            if (bossSlime.getPhase() == 0) {
                Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
                BlockPos summonPos = bossSlime.getBlockPos().offset(direction, 5);
                ModEntities.DARK_BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                ModEntities.DARK_BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                ModEntities.DARK_RED_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                ModEntities.GREEN_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
            }
            if (bossSlime.getPhase() == 1) {
                Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
                BlockPos summonPos = bossSlime.getBlockPos().offset(direction, 5);
                ModEntities.DARK_BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                ModEntities.DARK_BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                ModEntities.DARK_BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                ModEntities.DARK_RED_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                ModEntities.DARK_RED_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                ModEntities.GREEN_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                ModEntities.GREEN_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
            }
        }

        bossSlime.stopAttacking(100);
    }

    private boolean canSummonSlimes() {
        return this.bossSlime.getSummonedMobIds().isEmpty();
    }

    private void summonMob(int mobIndex, BlockPos summonPos) {
        World world = bossSlime.getWorld();
        double d = summonPos.getX();
        double e = (double) summonPos.getY() + 1;
        double f = summonPos.getZ();
        ((ServerWorld) bossSlime.getWorld()).spawnParticles(ParticleTypes.CLOUD, d, e, f, 10, 0.5D, 0.5D, 0.5D, 0.0D);
        ArcaneSlimeEntity slime = switch (mobIndex) {
            case 1 -> new BlueSlimeEntity(ModEntities.DARK_BLUE_SLIME, world);
            case 2 -> new GreenSlimeEntity(ModEntities.GREEN_SLIME, world);
            case 3 -> new RedSlimeEntity(ModEntities.DARK_RED_SLIME, world);

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
