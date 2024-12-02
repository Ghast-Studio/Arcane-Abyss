package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;


public class SlimeResetGoal extends Goal{
    private final ArcaneBossSlime bossSlime;
    private final double range;

    public SlimeResetGoal(ArcaneBossSlime bossSlime, double range) {
        this.bossSlime = bossSlime;
        this.range = range;
    }

    @Override
    public boolean canStart() {
        if (!bossSlime.isSleeping()) {
            World world = bossSlime.getWorld();
            BlockPos blockPos = bossSlime.getBlockPos();
            Box box = new Box(blockPos).expand(range);

            // Get a list of PlayerEntity instances within the Box
            List<PlayerEntity> playerList = world.getEntitiesByClass(PlayerEntity.class, box, player -> true);
            // Remove players that are not alive
            playerList.removeIf(player -> !player.isAlive());

            // Return true if the list is empty, false otherwise
            return playerList.isEmpty();
        }
        return false;
    }

    @Override
    public void start() {
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

        bossSlime.setState(ArcaneBossSlime.State.SPAWNING);
        bossSlime.setAwakeningTicks(0);
        bossSlime.getBossBar().clearPlayers();
        bossSlime.setAttackTimer(0);
        bossSlime.setPhase(0);
        bossSlime.setTarget(null);
        bossSlime.setHealth(bossSlime.getMaxHealth());
    }
}
