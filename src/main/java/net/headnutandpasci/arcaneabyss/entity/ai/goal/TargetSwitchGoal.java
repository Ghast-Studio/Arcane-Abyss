package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.Random;

public class TargetSwitchGoal extends Goal {

    private final MobEntity mob;
    private final long switchInterval;
    private long lastSwitchTime;

    public TargetSwitchGoal(MobEntity mob, long switchInterval) {
        this.mob = mob;
        this.switchInterval = switchInterval;
        this.lastSwitchTime = System.currentTimeMillis();
    }

    @Override
    public boolean canStart() {
        return System.currentTimeMillis() - lastSwitchTime >= switchInterval && hasNearbyPlayers();
    }

    @Override
    public void start() {
        switchTarget();
        lastSwitchTime = System.currentTimeMillis();
    }

    private void switchTarget() {
        if (mob.getWorld() instanceof ServerWorld serverWorld) {
            List<ServerPlayerEntity> players = serverWorld.getPlayers(player -> player.isAlive() && mob.canSee(player) && !player.isCreative() && !player.isSpectator());
            if (!players.isEmpty()) {
                Random random = new Random();
                PlayerEntity newTarget = players.get(random.nextInt(players.size()));
                mob.setTarget(newTarget);
            }
        }
    }

    private boolean hasNearbyPlayers() {
        if (!(mob.getWorld() instanceof ServerWorld serverWorld)) return false;
        List<ServerPlayerEntity> players = serverWorld.getPlayers(player -> player.isAlive() && mob.canSee(player));
        return !players.isEmpty();
    }
}
