package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;


public class SlimeResetGoal extends Goal {
    private final ArcaneBossSlime bossSlime;
    private final double range;
    private long noPlayersStartTime;
    private static final long RESET_DELAY = 10 * 1000; // 10 seconds in milliseconds

    public SlimeResetGoal(ArcaneBossSlime bossSlime, double range) {
        this.bossSlime = bossSlime;
        this.range = range;
        this.noPlayersStartTime = 0;
    }

    @Override
    public boolean canStart() {
        if (!bossSlime.isInState(ArcaneBossSlime.State.SPAWNING) || !bossSlime.isInState(ArcaneBossSlime.State.AWAKENING)) {
            World world = bossSlime.getWorld();
            BlockPos blockPos = bossSlime.getBlockPos();
            Box box = new Box(blockPos).expand(range);

            // Get a list of PlayerEntity instances within the Box
            List<PlayerEntity> playerList = world.getEntitiesByClass(PlayerEntity.class, box, player -> true);

            // Remove players that are not alive and creative or spectator
            playerList.removeIf(player -> !player.isAlive());
            playerList.removeIf(player -> player.isCreative() || player.isSpectator());

            // If no players are in the area
            if (playerList.isEmpty()) {
                // If this is the first time no players were detected, record the start time
                if (noPlayersStartTime == 0) {
                    noPlayersStartTime = System.currentTimeMillis();
                }

                // Check if 10 seconds have passed since no players were detected
                return System.currentTimeMillis() - noPlayersStartTime >= RESET_DELAY;
            } else {
                // Reset the no players start time if players are found
                noPlayersStartTime = 0;
            }
        }
        return false;
    }

    @Override
    public void start() {
        if (bossSlime.isInState(ArcaneBossSlime.State.SPAWNING) || bossSlime.isInState(ArcaneBossSlime.State.AWAKENING))
            return;

        World world = bossSlime.getWorld();
        if (world instanceof ServerWorld) {
            for (int i = 0; i < 50; ++i) {
                ((ServerWorld) world).spawnParticles(
                        ParticleTypes.POOF,
                        bossSlime.getX() + (world.random.nextDouble() * 2 - 1),
                        bossSlime.getY() + world.random.nextDouble(),
                        bossSlime.getZ() + (world.random.nextDouble() * 2 - 1),
                        1,
                        0.0D, 0.0D, 0.0D,
                        0.0D
                );
            }
        }

        bossSlime.reset();
        noPlayersStartTime = 0;
    }
}
