package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneSlimeEntity;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;


public class SlimeResetGoal extends Goal{
    private final BlackSlimeEntity blackSlimeEntity;
    private final double range;

    public SlimeResetGoal(BlackSlimeEntity blackSlimeEntity, double range) {
        this.blackSlimeEntity = blackSlimeEntity;
        this.range = range;
    }

    @Override
    public boolean canStart() {
        if (!blackSlimeEntity.isSleeping()) {
            World world = blackSlimeEntity.getWorld();
            BlockPos blockPos = blackSlimeEntity.getBlockPos();
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
        World world = blackSlimeEntity.getWorld();
        if (world instanceof ServerWorld) {
            for (int i = 0; i < 50; ++i) {
                ((ServerWorld) world).spawnParticles(
                        ParticleTypes.POOF,
                        blackSlimeEntity.getX() + (world.random.nextDouble() * 2 - 1),
                        blackSlimeEntity.getY() + world.random.nextDouble(),
                        blackSlimeEntity.getZ() + (world.random.nextDouble() * 2 - 1),
                        1,
                        0.0D, 0.0D, 0.0D,
                        0.0D
                );
            }
        }
        blackSlimeEntity.setState(BlackSlimeEntity.State.SPAWNING);
        blackSlimeEntity.setAwakeningTick(0);
        blackSlimeEntity.getBossBar().clearPlayers();
        blackSlimeEntity.setAttackTick(0);
        blackSlimeEntity.setPhase(0);
        blackSlimeEntity.setTarget(null);
        blackSlimeEntity.setHealth(blackSlimeEntity.getMaxHealth());
        if(blackSlimeEntity.getMoveControl() instanceof ArcaneSlimeEntity.ArcaneSlimeMoveControl moveControl) {
            moveControl.setDisabled(true);
        }


    }
}
