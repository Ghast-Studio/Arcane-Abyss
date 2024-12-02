package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import com.google.common.collect.ImmutableList;
import net.headnutandpasci.arcaneabyss.entity.ModEntities;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
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

    private final SlimeviathanEntity bossSlime;
    private int particleTimer = 0;
    private LivingEntity targetAtStrike;
    private Vec3d targetPosAtStrike;
    private boolean hasSpawnedEffect = false;
    private int cooldownTimer = 0;

    public SlimeviathanSummonPillarGoal(SlimeviathanEntity bossSlime) {
        this.bossSlime = bossSlime;
    }

    @Override
    public boolean canStart() {
        return (bossSlime.isInState(ArcaneBossSlime.State.PILLAR_SUMMON) && bossSlime.hasTarget() && this.canSummonSlimes());
    }

    @Override
    public boolean canStop() {
        return bossSlime.getSummonedPillarIds().isEmpty();
    }

    @Override
    public boolean shouldContinue() {
        return bossSlime.isInState(ArcaneBossSlime.State.PILLAR_SUMMON) && bossSlime.hasTarget() && !this.bossSlime.getSummonedPillarIds().isEmpty();
    }


    @Override
    public void start() {
        this.targetAtStrike = bossSlime.getTarget();
        if (this.targetAtStrike == null) this.stop();

        this.particleTimer = 50;

        ServerWorld world = bossSlime.getWorld() instanceof ServerWorld ? ((ServerWorld) bossSlime.getWorld()) : null;
        if (world != null) {
            world.spawnParticles(ParticleTypes.FLAME, bossSlime.getX(), bossSlime.getY(), bossSlime.getZ(), 20, 3.0D, 3.0D, 3.0D, 0.0D);
        }

        for (int i = 0; i < 4; i++) {
            WeightedRandomBag<Integer> mobWeightBag = new WeightedRandomBag<>();
            mobWeightBag.addEntry(1, 1);

            Direction direction = MOB_SUMMON_POS.get(Math.min(i, MOB_SUMMON_POS.size() - 1));
            Optional<Vec3d> summonPos = this.generateMobSpawnPos(bossSlime.getPos(), 10, direction);
            if (summonPos.isEmpty()) continue;

            summonMob(mobWeightBag.getRandom(), BlockPos.ofFloored(summonPos.get()));
            ModEntities.DARK_BLUE_SLIME.spawn(world, BlockPos.ofFloored(summonPos.get()), SpawnReason.REINFORCEMENT);
        }
    }

    @Override
    public void stop() {
        this.targetAtStrike = null;
        this.targetPosAtStrike = null;
        this.particleTimer = 0;
        this.bossSlime.stopAttacking(100);
    }

    @Override
    public void tick() {
        ServerWorld world = bossSlime.getWorld() instanceof ServerWorld ? ((ServerWorld) bossSlime.getWorld()) : null;
        if (world == null) return;
        if (cooldownTimer > 0) cooldownTimer--;


        if (particleTimer > 0) {
            particleTimer--;

            if (targetPosAtStrike != null) {
                LivingEntity targetAtStrike = bossSlime.getTarget();
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
            if (bossSlime.getTarget() != null) {
                targetAtStrike = bossSlime.getTarget();
                targetPosAtStrike = targetAtStrike.getPos();
            }

            cooldownTimer = 20;
        }
    }

    private boolean canSummonSlimes() {
        return this.bossSlime.getSummonedPillarIds().isEmpty();
    }

    private void summonMob(int mobIndex, BlockPos summonPos) {
        ServerWorld world = bossSlime.getWorld() instanceof ServerWorld ? ((ServerWorld) bossSlime.getWorld()) : null;
        if (world == null) return;


        BlockPos groundPos = findSolidGround(world, summonPos, bossSlime.getBlockPos().getY());


        if (groundPos == null) return;


        world.spawnParticles(ParticleTypes.CLOUD, groundPos.getX() + 0.5, groundPos.getY(), groundPos.getZ() + 0.5, 10, 0.5D, 0.5D, 0.5D, 0.0D);

        ArcaneSlimeEntity baseSlime = switch (mobIndex) {
            case 1 -> ModEntities.SLIME_PILLAR.spawn(world, groundPos, SpawnReason.REINFORCEMENT);
            default -> null;
        };

        if (baseSlime == null) return;


        this.disableDrops(baseSlime);


        SlimePillarEntity previousSlime = (SlimePillarEntity) baseSlime;

        for (int i = 1; i < 4; i++) {
            BlockPos stackedPos = groundPos.up(i);
            SlimePillarEntity currentSlime = ModEntities.SLIME_PILLAR.spawn(world, stackedPos, SpawnReason.REINFORCEMENT);

            if (currentSlime != null) {
                this.disableDrops(currentSlime);


                currentSlime.setParent(previousSlime);
                previousSlime.setChild(currentSlime);

                this.bossSlime.getSummonedPillarIds().add(currentSlime.getId());

                previousSlime = currentSlime;
            }
        }
    }

    private Optional<Vec3d> generateMobSpawnPos(Vec3d targetPos, int distance, Direction direction) {
        RaycastContext raycastContext = new RaycastContext(targetPos, targetPos.offset(direction, distance), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, bossSlime);
        BlockHitResult blockHitResult = this.bossSlime.getWorld().raycast(raycastContext);

        if (blockHitResult.getType() == BlockHitResult.Type.BLOCK) {
            return Optional.empty();
        }

        return Optional.of(targetPos.offset(direction, distance));
    }

    private BlockPos findSolidGround(ServerWorld world, BlockPos targetPos, int bossYLevel) {

        BlockPos.Mutable mutablePos = targetPos.mutableCopy();
        mutablePos.setY(bossYLevel);


        int previousY = mutablePos.getY();


        for (int y = bossYLevel; y >= world.getBottomY(); y--) {
            mutablePos.setY(y);


            if (world.getBlockState(mutablePos).isSolidBlock(world, mutablePos)) {
                int x = mutablePos.getX();
                int yFound = mutablePos.getY();
                int z = mutablePos.getZ();


                System.out.println("Found solid ground at - X: " + x + ", Y: " + yFound + ", Z: " + z);
                System.out.println("Previous Y was: " + previousY);

                return mutablePos.toImmutable();
            }
        }

        return null;
    }

    public void spawnCircleParticles(ServerWorld world, BlockPos center, ParticleEffect particle, double radius, int particleCount) {

        double centerX = center.getX();
        double centerY = center.getY();
        double centerZ = center.getZ();


        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);
            double y = centerY;

            world.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    public void spawnParticleEffectOnceAtPlayer(LivingEntity targetAtStrike) {
        if (targetAtStrike instanceof PlayerEntity player) {

            if (hasSpawnedEffect) {
                return;
            }


            if (player.isOnGround()) {
                double playerX = player.getX();
                double playerZ = player.getZ();
                double playerY = player.getY();


                ServerWorld world = player.getWorld() instanceof ServerWorld ? (ServerWorld) player.getWorld() : null;
                if (world != null) {


                    BlockPos groundPos = new BlockPos((int) playerX, (int) playerY, (int) playerZ);

                    spawnCircleParticles(world, groundPos, ParticleTypes.DRAGON_BREATH, 1.5, 30);


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
