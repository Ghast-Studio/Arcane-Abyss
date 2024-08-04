package net.headnutandpasci.arcaneabyss.entity.ai;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.util.random.WeightedBag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.List;
import java.util.stream.IntStream;

public class SlimeSummonGoal extends Goal {
    private static final ImmutableList<Direction> MOB_SUMMON_POS = ImmutableList.of(
            Direction.SOUTH,
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST
    );

    private final BlackSlimeEntity entity;
    private final List<EntityType<? extends ArcaneSlimeEntity>> slimesToSummon;

    public SlimeSummonGoal(BlackSlimeEntity entity, List<EntityType<? extends ArcaneSlimeEntity>> slimesToSummon) {
        this.entity = entity;
        this.slimesToSummon = slimesToSummon;
    }

    @Override
    public boolean canStart() {
        if (entity.isAttacking(BlackSlimeEntity.State.SUMMON) && entity.getTarget() != null) {
            if (this.canSummonSlimes()) {
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
        if (!(this.entity.getWorld() instanceof ServerWorld world)) return;

        if (entity.getAttackTimer() == 100) {
            entity.setAttackTimer(99);
            world.spawnParticles(ParticleTypes.FLAME,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    20,
                    3.0D,
                    3.0D,
                    3.0D,
                    0.0D);

            int amount = entity.getPhase() == 1 ? 5 : 10;
            IntStream.range(0, amount).forEach(i -> {
                WeightedBag<EntityType<? extends ArcaneSlimeEntity>> bag = new WeightedBag<>();
                this.slimesToSummon.forEach(type -> bag.addEntry(type, 1));
                Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
                BlockPos summonPos = entity.getBlockPos().offset(direction, 5);
                summonMob(bag.getRandom(), summonPos, world);
            });
        }

        if (entity.getAttackTimer() == 0) {
            entity.stopAttacking(0);
        }
    }

    private boolean canSummonSlimes() {
        return this.entity.getSummonedMobIds().isEmpty();
    }

    private void summonMob(EntityType<? extends ArcaneSlimeEntity> type, BlockPos pos, ServerWorld world) {
        pos = pos.add(0, 1, 0);
        world.spawnParticles(ParticleTypes.CLOUD, pos.getX(), pos.getY(), pos.getZ(), 10, 0.5D, 0.5D, 0.5D, 0.0D);
        ArcaneSlimeEntity slime = type.spawn(world, pos, SpawnReason.MOB_SUMMONED);

        if (slime == null) return;
        slime.teleport(pos.getX(), pos.getY(), pos.getZ());
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
