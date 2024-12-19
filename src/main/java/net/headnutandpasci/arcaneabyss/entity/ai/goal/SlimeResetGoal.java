package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;


public class SlimeResetGoal extends Goal {
    private static final long RESET_DELAY = 10 * 1000;

    private final ArcaneBossSlime bossSlime;
    private final double range;
    private long noPlayersStartTime;

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

            List<PlayerEntity> playerList = world.getEntitiesByClass(PlayerEntity.class, box, player -> true);

            playerList.removeIf(player -> !player.isAlive());
            playerList.removeIf(player -> player.isCreative() || player.isSpectator());

            if (playerList.isEmpty()) {
                if (noPlayersStartTime == 0) {
                    noPlayersStartTime = System.currentTimeMillis();
                }

                return System.currentTimeMillis() - noPlayersStartTime >= RESET_DELAY;
            } else {
                noPlayersStartTime = 0;
            }
        }
        return false;
    }

    @Override
    public void start() {
        if (bossSlime.isInState(ArcaneBossSlime.State.SPAWNING) || bossSlime.isInState(ArcaneBossSlime.State.AWAKENING))
            return;

        bossSlime.reset();
        noPlayersStartTime = 0;
    }
}
