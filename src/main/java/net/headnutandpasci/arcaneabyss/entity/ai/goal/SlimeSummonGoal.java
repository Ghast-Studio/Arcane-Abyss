package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class SlimeSummonGoal extends Goal {
    private static final ImmutableList<Direction> MOB_SUMMON_POS = ImmutableList.of(Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST);

    private final BlackSlimeEntity bossSlime;

    public SlimeSummonGoal(BlackSlimeEntity bossSlime) {
        this.bossSlime = bossSlime;
    }

    @Override
    public boolean canStart() {
        return (bossSlime.isInState(ArcaneBossSlime.State.SUMMON) && bossSlime.hasTarget() && this.canSummonSlimes());
    }

    @Override
    public boolean canStop() {
        return this.bossSlime.getSummonedMobIds().isEmpty();
    }

    @Override
    public boolean shouldContinue() {
        return bossSlime.isInState(ArcaneBossSlime.State.SUMMON) && bossSlime.hasTarget() && !this.bossSlime.getSummonedMobIds().isEmpty();
    }

    @Override
    public void start() {
        super.start();
        ServerWorld world = bossSlime.getWorld() instanceof ServerWorld ? ((ServerWorld) bossSlime.getWorld()) : null;
        this.bossSlime.playSound(SoundEvents.ENTITY_PIGLIN_ANGRY, 500.0F, 240.0F);
        if (world == null) {
            ArcaneAbyss.LOGGER.warn("[SlimeSummonGoal] World is null");
            return;
        }

        for (int i = 0; i < 2; i++) {
            if (bossSlime.getPhase() == 0) {
                Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
                BlockPos summonPos = bossSlime.getBlockPos().offset(direction, 5);
                this.summonMob(ModEntities.BLUE_SLIME, world, summonPos);
                this.summonMob(ModEntities.BLUE_SLIME, world, summonPos);
                this.summonMob(ModEntities.RED_SLIME, world, summonPos);
                this.summonMob(ModEntities.GREEN_SLIME, world, summonPos);
            }
            if (bossSlime.getPhase() == 1) {
                Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
                BlockPos summonPos = bossSlime.getBlockPos().offset(direction, 5);
                this.summonMob(ModEntities.BLUE_SLIME, world, summonPos);
                this.summonMob(ModEntities.BLUE_SLIME, world, summonPos);
                this.summonMob(ModEntities.BLUE_SLIME, world, summonPos);
                this.summonMob(ModEntities.RED_SLIME, world, summonPos);
                this.summonMob(ModEntities.RED_SLIME, world, summonPos);
                this.summonMob(ModEntities.RED_SLIME, world, summonPos);
                this.summonMob(ModEntities.GREEN_SLIME, world, summonPos);
                this.summonMob(ModEntities.GREEN_SLIME, world, summonPos);

            }
        }
    }

    @Override
    public void stop() {
        bossSlime.stopAttacking(100);
        super.stop();
    }

    private boolean canSummonSlimes() {
        return this.bossSlime.getSummonedMobIds().isEmpty();
    }

    private void summonMob(EntityType<? extends ArcaneSlimeEntity> slimeType, ServerWorld world, BlockPos summonPos) {
        world.spawnParticles(ParticleTypes.GLOW, summonPos.getX(), summonPos.getY(), summonPos.getZ(), 300, 0.5D, 0.5D, 0.5D, 0.0D);
        ArcaneSlimeEntity slime = slimeType.spawn(world, summonPos, SpawnReason.REINFORCEMENT);

        if (slime == null) return;
        slime.setPosition(summonPos.toCenterPos());
        this.disableDrops(slime);

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
