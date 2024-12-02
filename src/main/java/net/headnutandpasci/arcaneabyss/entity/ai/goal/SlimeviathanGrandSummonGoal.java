package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
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

public class SlimeviathanGrandSummonGoal extends Goal {
    private static final ImmutableList<Direction> MOB_SUMMON_POS = ImmutableList.of(
            Direction.SOUTH,
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST
    );

    private final SlimeviathanEntity entity;

    public SlimeviathanGrandSummonGoal(SlimeviathanEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        if (entity.isAttacking(SlimeviathanEntity.State.SUMMON) && entity.getTarget() != null) {
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
        return entity.isAttacking(SlimeviathanEntity.State.SUMMON) && entity.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        entity.setAttackTimer(100);

    }

    @Override
    public void tick() {
            ServerWorld world = entity.getWorld() instanceof ServerWorld ? ((ServerWorld) entity.getWorld()) : null;
            if (entity.getAttackTimer() == 100) {
                entity.setAttackTimer(99);
                /*blackSlimeEntity.playSound(blackSlimeEntity.getScreechSound(), 2.0F, 1.0F);*/
                double d = entity.getX();
                double e = entity.getY();
                double f = entity.getZ();
                ((ServerWorld) entity.getWorld()).spawnParticles(ParticleTypes.FLAME, d, e, f, 20, 3.0D, 3.0D, 3.0D, 0.0D);


                for (int i = 0; i < 2; i++) {
                    if(entity.getPhase() == 1) {
                        Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
                        BlockPos summonPos = entity.getBlockPos().offset(direction, 5);
                        ModEntities.DARK_BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                        ModEntities.DARK_BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                        ModEntities.DARK_RED_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                        ModEntities.GREEN_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                    }
                    if(entity.getPhase() == 2) {
                        Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
                        BlockPos summonPos = entity.getBlockPos().offset(direction, 5);
                        ModEntities.DARK_BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                        ModEntities.DARK_BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                        ModEntities.DARK_BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                        ModEntities.DARK_RED_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                        ModEntities.DARK_RED_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                        ModEntities.GREEN_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                        ModEntities.GREEN_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
                    }
                }


            }


        if (entity.getAttackTimer() == 0) {
            entity.stopAttacking(0);
        }
    }

    private boolean canSummonSlimes() {
        return this.entity.getSummonedMobIds().isEmpty();
    }

    private void summonMob(int mobIndex, BlockPos summonPos) {
        World world = entity.getWorld();
        double d = summonPos.getX();
        double e = (double) summonPos.getY() + 1;
        double f = summonPos.getZ();
        ((ServerWorld) entity.getWorld()).spawnParticles(ParticleTypes.CLOUD, d, e, f, 10, 0.5D, 0.5D, 0.5D, 0.0D);
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
