package net.headnutandpasci.arcaneabyss.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.misc.YallaEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.*;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public class YallaBrain {
    public static Brain<?> create(Brain<YallaEntity> brain) {
        YallaBrain.addCoreActivities(brain);
        YallaBrain.addCombatActivities(brain);
        YallaBrain.addIdleActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<YallaEntity> brain) {
        brain.setTaskList(Activity.CORE, 0, ImmutableList.of(
                new StayAboveWaterTask(0.8F),
                new LookAroundTask(45, 90),
                new WanderAroundTask(),
                new TemptationCooldownTask(MemoryModuleType.LIKED_NOTEBLOCK_COOLDOWN_TICKS)));
    }

    private static void addCombatActivities(Brain<YallaEntity> brain) {
        brain.setTaskList(Activity.FIGHT, 10, ImmutableList.of(
                        ForgetAttackTargetTask.create(),
                        RangedApproachTask.create(1.5F),
                        MeleeAttackTask.create(5)),
                MemoryModuleType.ATTACK_TARGET);
    }

    private static void addIdleActivities(Brain<YallaEntity> brain) {
        brain.setTaskList(Activity.IDLE, 10, ImmutableList.of(
                UpdateAttackTargetTask.create(YallaBrain::findNearestHostile),
                LookAtMobTask.create(8.0F),
                WalkTowardsLookTargetTask.create(YallaBrain::getLikedLookTarget, livingEntity -> true, 8, 15, 2.25f),
                new RandomTask<>(ImmutableList.of(
                        Pair.of(StrollTask.createSolidTargeting(1.0F), 2),
                        Pair.of(GoTowardsLookTargetTask.create(1.0F, 3), 2),
                        Pair.of(new WaitTask(30, 60), 1)))
        ));
    }

    public static void updateActivities(YallaEntity yalla) {
        Brain<YallaEntity> brain = yalla.getBrain();
        yalla.getBrain().resetPossibleActivities(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
        yalla.setAttacking(brain.hasMemoryModule(MemoryModuleType.ATTACK_TARGET));
    }

    private static Optional<LivingEntity> findNearestHostile(LivingEntity entity) {
        return entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS)
                .flatMap(mobs -> mobs.stream(EntityPredicates.VALID_LIVING_ENTITY::test)
                        .filter(mob -> !mob.getType().getSpawnGroup().isPeaceful())
                        .min(Comparator.comparingDouble(entity::squaredDistanceTo)));
    }

    private static Optional<LivingEntity> findNearbyFriendly(LivingEntity entity) {
        return entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.VISIBLE_MOBS)
                .flatMap(mobs -> mobs.stream(EntityPredicates.VALID_LIVING_ENTITY::test)
                        .filter(mob -> mob.getType().getSpawnGroup().isPeaceful())
                        .filter(mob -> mob.getType() != ModEntities.YALLA)
                        .filter(mob -> mob.getType() != EntityType.PLAYER)
                        .findAny());
    }

    private static Optional<LookTarget> getLikedLookTarget(LivingEntity yalla) {
        return YallaBrain.getLikedPlayer(yalla).map((player) -> new EntityLookTarget(player, true));
    }

    private static Optional<LookTarget> getFriendlyLookTarget(LivingEntity yalla) {
        return YallaBrain.findNearbyFriendly(yalla).map((friendly) -> new EntityLookTarget(friendly, true));
    }


    public static Optional<ServerPlayerEntity> getLikedPlayer(LivingEntity yalla) {
        World world = yalla.getWorld();
        if (!world.isClient() && world instanceof ServerWorld serverWorld) {
            Optional<UUID> optional = yalla.getBrain().getOptionalRegisteredMemory(MemoryModuleType.LIKED_PLAYER);
            if (optional.isPresent()) {
                Entity entity = serverWorld.getEntity(optional.get());
                if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                    if ((serverPlayerEntity.interactionManager.isSurvivalLike() || serverPlayerEntity.interactionManager.isCreative()) && serverPlayerEntity.isInRange(yalla, 64.0)) {
                        return Optional.of(serverPlayerEntity);
                    }
                }

                return Optional.empty();
            }
        }

        return Optional.empty();
    }
}