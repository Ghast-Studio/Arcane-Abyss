package net.headnutandpasci.arcaneabyss.entity.ai;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.blue.SlimePillarEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.util.random.WeightedRandomBag;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import org.joml.Vector3f;

public class SlimeviathanSummonPillarGoal extends Goal {
    private static final ImmutableList<Direction> MOB_SUMMON_POS = ImmutableList.of(
            Direction.SOUTH,
            Direction.NORTH,
            Direction.EAST,
            Direction.WEST
    );

    private final SlimeviathanEntity entity;
    private int particleTimer = 0;
    private LivingEntity targetAtStrike;
    private Vec3d targetPosAtStrike;

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

        this.targetAtStrike = entity.getTarget();
        if (this.targetAtStrike == null) this.stop();


        this.particleTimer = 50;
        this.entity.setAttackTimer(40);


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

            Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
            BlockPos summonPos = entity.getBlockPos().offset(direction, 5);
            ModEntities.DARK_BLUE_SLIME.spawn(world, summonPos, SpawnReason.REINFORCEMENT);
        }
    }

    @Override
    public void stop() {
        if (entity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl control) {
            control.setDisabled(false);
        }
        this.targetAtStrike = null;
        this.targetPosAtStrike = null;
        this.particleTimer = 0;
    }

    private int cooldownTimer = 0;

    @Override
    public void tick() {
        ServerWorld world = entity.getWorld() instanceof ServerWorld ? ((ServerWorld) entity.getWorld()) : null;
        if (world == null) return;


        if (cooldownTimer > 0) {
            cooldownTimer--;
        }


        if (particleTimer > 0) {
            particleTimer--;

            if (targetPosAtStrike != null) {
                spawnCircleParticles(targetAtStrike.getWorld(), this.targetPosAtStrike, ParticleTypes.DRAGON_BREATH, 1.5, 30);

                if (particleTimer % 20 == 0) {
                    LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
                    if (lightning != null) {
                        lightning.refreshPositionAfterTeleport(targetPosAtStrike.x, targetPosAtStrike.y, targetPosAtStrike.z);
                        world.spawnEntity(lightning);
                    }
                }

            }


            if (particleTimer == 0) {
                targetPosAtStrike = null;
            }
        }


        if (cooldownTimer == 0 && particleTimer == 0) {

            particleTimer = 10;


            if (entity.getTarget() != null) {
                targetAtStrike = entity.getTarget();
                targetPosAtStrike = targetAtStrike.getPos();
            }


            cooldownTimer = 20;
        }
    }




    private boolean canSummonSlimes() {
        return this.entity.getSummonedPillarIds().isEmpty();
    }

    private void summonMob(int mobIndex, BlockPos summonPos) {
        ServerWorld world = entity.getWorld() instanceof ServerWorld ? ((ServerWorld) entity.getWorld()) : null;
        if (world == null) return;

        // Find the ground position for the summon
        BlockPos groundPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, summonPos);

        // Spawn initial particles at the ground position
        world.spawnParticles(ParticleTypes.CLOUD, groundPos.getX() + 0.5, groundPos.getY(), groundPos.getZ() + 0.5, 10, 0.5D, 0.5D, 0.5D, 0.0D);

        // Create the base slime at the ground position
        ArcaneSlimeEntity baseSlime = switch (mobIndex) {
            case 1 -> ModEntities.SLIME_PILLAR.spawn(world, groundPos, SpawnReason.REINFORCEMENT);
            default -> null;
        };

        if (baseSlime == null) return;

        // Disable drops for the base slime
        this.disableDrops(baseSlime);

        // Handle stacking the pillar slimes
        SlimePillarEntity previousSlime = (SlimePillarEntity) baseSlime;

        for (int i = 1; i < 3; i++) {
            BlockPos stackedPos = groundPos.up(i);
            SlimePillarEntity currentSlime = ModEntities.SLIME_PILLAR.spawn(world, stackedPos, SpawnReason.REINFORCEMENT);

            if (currentSlime != null) {
                this.disableDrops(currentSlime);

                // Link the slimes as parent-child
                currentSlime.setParent(previousSlime);
                previousSlime.setChild(currentSlime);


                this.entity.getSummonedPillarIds().add(currentSlime.getId());

                previousSlime = currentSlime;
            }
        }
    }


    public void spawnCircleParticles(World world, Vec3d center, ParticleEffect particle, double radius, int particleCount) {
        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double x = center.x + radius * Math.cos(angle);
            double z = center.z + radius * Math.sin(angle);
            double y = world.getTopY(Heightmap.Type.WORLD_SURFACE, (int) x, (int) z) + 0.2;

            if (world instanceof ServerWorld serverWorld) {
                serverWorld.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
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
