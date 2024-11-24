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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.Optional;

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
    private boolean hasSpawnedEffect = false;
    private int cooldownTimer = 0;

    public SlimeviathanSummonPillarGoal(SlimeviathanEntity entity) {
        this.entity = entity;
    }

    @Override
    public boolean canStart() {
        if (entity.isAttacking(SlimeviathanEntity.State.PILLAR_SUMMON) && entity.getTarget() != null) {
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
        return entity.isAttacking(SlimeviathanEntity.State.PILLAR_SUMMON) && entity.getTarget() != null;
    }

    @Override
    public boolean canStop() {
        System.out.println(entity.getSummonedPillarIds().isEmpty());
        return entity.getSummonedPillarIds().isEmpty();
    }

    @Override
    public void start() {
        this.entity.setAttackTimer(100);
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
            Optional<Vec3d> summonPos = this.generateMobSpawnPos(entity.getPos(), 10, direction);
            if (summonPos.isEmpty()) continue;

            summonMob(mobWeightBag.getRandom(), BlockPos.ofFloored(summonPos.get()));
            ModEntities.DARK_BLUE_SLIME.spawn(world, BlockPos.ofFloored(summonPos.get()), SpawnReason.REINFORCEMENT);
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


                LivingEntity targetAtStrike = entity.getTarget();
                if (targetAtStrike instanceof PlayerEntity) {
                    spawnParticleEffectOnceAtPlayer(targetAtStrike);

                }


                if (particleTimer % 20 == 0) {
                    LightningEntity lightning = EntityType.LIGHTNING_BOLT.create(world);
                    if (lightning != null) {
                        lightning.refreshPositionAfterTeleport(targetPosAtStrike.x, targetPosAtStrike.y, targetPosAtStrike.z);
                        world.spawnEntity(lightning);
                        world.spawnEntity(lightning);
                        resetEffectFlag();
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

        // Adjust summon position to the ground level where the boss is
        BlockPos groundPos = findSolidGround(world, summonPos, entity.getBlockPos().getY());

        // If no valid ground position is found, skip summoning
        if (groundPos == null) return;

        // Spawn initial particles at the adjusted ground position
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

        for (int i = 1; i < 4; i++) {
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

    private Optional<Vec3d> generateMobSpawnPos(Vec3d targetPos, int distance, Direction direction) {
        RaycastContext raycastContext = new RaycastContext(targetPos, targetPos.offset(direction, distance), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, entity);
        BlockHitResult blockHitResult = this.entity.getWorld().raycast(raycastContext);

        if (blockHitResult.getType() == BlockHitResult.Type.BLOCK) {
            return Optional.empty();
        }

        return Optional.of(targetPos.offset(direction, distance));
    }

    private BlockPos findSolidGround(ServerWorld world, BlockPos targetPos, int bossYLevel) {
        // Create a mutable copy of the target position
        BlockPos.Mutable mutablePos = targetPos.mutableCopy();
        mutablePos.setY(bossYLevel);  // Set Y to the desired level (bossYLevel or another level)

        // Store the current Y value (before updating)
        int previousY = mutablePos.getY();

        // Search for solid ground below the target position
        for (int y = bossYLevel; y >= world.getBottomY(); y--) {
            mutablePos.setY(y);  // Update Y level for each iteration

            // If we found a solid block, log the previous Y value
            if (world.getBlockState(mutablePos).isSolidBlock(world, mutablePos)) {
                int x = mutablePos.getX();
                int yFound = mutablePos.getY();  // This is the current Y when the solid block is found
                int z = mutablePos.getZ();

                // Log the current position and the previous Y value
                System.out.println("Found solid ground at - X: " + x + ", Y: " + yFound + ", Z: " + z);
                System.out.println("Previous Y was: " + previousY);

                return mutablePos.toImmutable();
            }
        }

        return null;  // If no solid ground found, return null
    }

    public void spawnCircleParticles(ServerWorld world, BlockPos center, ParticleEffect particle, double radius, int particleCount) {

        double centerX = center.getX();
        double centerY = center.getY();
        double centerZ = center.getZ();

        // Spawn particles in a circle around the given center position
        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);
            double y = centerY;  // Keep the particle at the ground level's Y coordinate

            world.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0); // Spawn the particle
        }
    }

    public void spawnParticleEffectOnceAtPlayer(LivingEntity targetAtStrike) {
        if (targetAtStrike instanceof PlayerEntity player) {
            // Ensure the effect is only triggered once
            if (hasSpawnedEffect) {
                return; // Skip spawning if the effect has already been triggered
            }


            if (player.isOnGround()) {
                double playerX = player.getX();
                double playerZ = player.getZ();
                double playerY = player.getY();

                // Get the ground level (Y-coordinate) for the player at their X, Z position
                ServerWorld world = player.getWorld() instanceof ServerWorld ? (ServerWorld) player.getWorld() : null;
                if (world != null) {
                    // Get the ground Y position at the player's X and Z coordinates


                    // Cast playerX and playerZ to int for BlockPos
                    BlockPos groundPos = new BlockPos((int) playerX, (int) playerY, (int) playerZ);

                    // Now spawn the particles at the correct ground level
                    spawnCircleParticles(world, groundPos, ParticleTypes.DRAGON_BREATH, 1.5, 30);

                    // Set the flag to prevent multiple spawns
                    hasSpawnedEffect = true;
                }
            }

        }
    }

    public void resetEffectFlag() {
        hasSpawnedEffect = false;
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
