package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;


public class SlimeviathanResetGoal extends Goal{
    private final SlimeviathanEntity slimeviathanEntity;
    private final double range;

    public SlimeviathanResetGoal(SlimeviathanEntity slimeviathanEntity, double range) {
        this.slimeviathanEntity = slimeviathanEntity;
        this.range = range;
    }

    @Override
    public boolean canStart() {
        if (!slimeviathanEntity.isSleeping()) {
            World world = slimeviathanEntity.getWorld();
            BlockPos blockPos = slimeviathanEntity.getBlockPos();
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
        World world = slimeviathanEntity.getWorld();
        if (world instanceof ServerWorld) {
            for (int i = 0; i < 50; ++i) {
                ((ServerWorld) world).spawnParticles(
                        ParticleTypes.POOF,
                        slimeviathanEntity.getX() + (world.random.nextDouble() * 2 - 1),
                        slimeviathanEntity.getY() + world.random.nextDouble(),
                        slimeviathanEntity.getZ() + (world.random.nextDouble() * 2 - 1),
                        1,
                        0.0D, 0.0D, 0.0D,
                        0.0D
                );
            }
        }
        slimeviathanEntity.setState(SlimeviathanEntity.State.SPAWNING);
        slimeviathanEntity.setAwakeningTick(0);
        slimeviathanEntity.getBossBar().clearPlayers();
        slimeviathanEntity.setAttackTick(0);
        slimeviathanEntity.setPhase(0);
        slimeviathanEntity.setTarget(null);
        slimeviathanEntity.setHealth(slimeviathanEntity.getMaxHealth());
        if(slimeviathanEntity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl moveControl) {
            moveControl.setDisabled(true);
        }


    }
}
