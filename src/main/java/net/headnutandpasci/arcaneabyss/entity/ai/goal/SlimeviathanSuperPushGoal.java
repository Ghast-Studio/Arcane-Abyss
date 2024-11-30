package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.headnutandpasci.arcaneabyss.util.Util;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;

import java.util.List;

public class SlimeviathanSuperPushGoal extends Goal {
    private final SlimeviathanEntity slime;
    private static final float PUSH_DISTANCE = 5.0f;

    public SlimeviathanSuperPushGoal(SlimeviathanEntity slime) {
        this.slime = slime;
    }

    @Override
    public boolean canStart() {
        // Ensures the goal can start if the slime is in the PUSH state and has a target
        return slime.isAttacking(SlimeviathanEntity.State.PUSH) && slime.getTarget() != null;
    }
    @Override
    public void start() {
        super.start();
        this.slime.setAttackTimer(100);
    }

    @Override
    public void tick() {
        if (this.slime.getAttackTimer() == 66) {
            Box box = new Box(this.slime.getPos().add(-10, -10, -10), this.slime.getPos().add(10, 10, 10));

            List<PlayerEntity> targets = this.slime.getWorld().getEntitiesByClass(PlayerEntity.class, box, (entity) -> true);

            System.out.println("Found " + targets.size() + " players within the box.");

            targets.forEach(player -> {
                if (this.slime.squaredDistanceTo(player) <= 60) {
                    this.slime.playSound(SoundEvents.ENTITY_WITHER_DEATH, 3.0F, 1.0F);
                    ((ServerWorld) this.slime.getWorld()).spawnParticles(ParticleTypes.POOF, this.slime.getX(), this.slime.getY(), this.slime.getZ(), 400, 5.0D, 0.0D, 5.0D, 0.0D); // Reduced particle spread

                    Util.pushPlayer(this.slime, player, 20, (float) (PUSH_DISTANCE * this.slime.getAttackDamage()));
                }
            });
        }
    }
}
