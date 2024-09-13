package net.headnutandpasci.arcaneabyss.entity.ai;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SlimeviathanSummonPillarGoal extends Goal {
    private static final ImmutableList<Direction> MOB_SUMMON_POS = ImmutableList.of(
            Direction.SOUTH,
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST
    );

    private final SlimeviathanEntity entity;

    public SlimeviathanSummonPillarGoal(SlimeviathanEntity entity) {
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
    public boolean canStop() {
        System.out.println(entity.getSummonedPillarIds().isEmpty());
        return entity.getSummonedPillarIds().isEmpty();
    }

    @Override
    public void start() {
        entity.setAttackTimer(100);

        ServerWorld world = entity.getWorld() instanceof ServerWorld ? ((ServerWorld) entity.getWorld()) : null;
        if (world != null) {
            world.spawnParticles(ParticleTypes.FLAME, entity.getX(), entity.getY(), entity.getZ(), 20, 3.0D, 3.0D, 3.0D, 0.0D);
        }

        if (entity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl control) {
            control.setDisabled(true);
        }

        for (int i = 0; i < 4; i++) {
            WeightedRandomBag<Integer> mobWeightBag = new WeightedRandomBag<>();
            mobWeightBag.addEntry(1, 1);

            Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
            BlockPos summonPos = entity.getBlockPos().offset(direction, 10);
            summonMob(mobWeightBag.getRandom(), summonPos);
        }

        for (int i = 0; i < 4; i++) {
            WeightedRandomBag<Integer> mobWeightBag = new WeightedRandomBag<>();

            mobWeightBag.addEntry(2, 1);
            Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
            BlockPos summonPos = entity.getBlockPos().offset(direction, 5);
            summonMob(mobWeightBag.getRandom(), summonPos);
        }
    }

    @Override
    public void stop() {
        if (entity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl control) {
            control.setDisabled(false);
        }
    }

    @Override
    public void tick() {

    }

    private boolean canSummonSlimes() {
        return this.entity.getSummonedPillarIds().isEmpty();
    }

    private void summonMob(int mobIndex, BlockPos summonPos) {
        ServerWorld world = entity.getWorld() instanceof ServerWorld ? ((ServerWorld) entity.getWorld()) : null;
        if (world == null) return;

        world.spawnParticles(ParticleTypes.CLOUD, entity.getX(), entity.getY(), entity.getZ(), 10, 0.5D, 0.5D, 0.5D, 0.0D);
        ArcaneSlimeEntity slime = switch (mobIndex) {
            case 1 -> ModEntities.SLIME_PILLAR.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
            case 2 -> ModEntities.DARK_BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
            default -> null;
        };
        if (slime == null) return;

        this.disableDrops(slime);
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
