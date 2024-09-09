package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.entity.slime.boss.slimeviathan.SlimeviathanEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class SlimeviathanSuperPushGoal extends Goal {
    private final SlimeviathanEntity slime;
    private static final double PUSH_DISTANCE = 5.0D;

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
                    System.out.println("Pushing player: " + player.getName().getString());
                    this.slime.playSound(SoundEvents.ENTITY_WITHER_DEATH, 3.0F, 1.0F);
                    ((ServerWorld) this.slime.getWorld()).spawnParticles(ParticleTypes.ASH, this.slime.getX(), this.slime.getY(), this.slime.getZ(), 400, 5.0D, 0.0D, 5.0D, 0.0D);
                    pushPlayer(player);

                }
            });
        }
    }

    private void pushPlayer(PlayerEntity player) {
        double knockbackStrength = 20.0D;
        int damageAmount;

        if (player.isBlocking()) {
            player.disableShield(true);
            damageAmount = (int) (this.slime.getAttackDamage() * 1F);
        } else {
            damageAmount = (int) (this.slime.getAttackDamage() * 1.75F);
        }

        Vec3d direction = new Vec3d(player.getX() - slime.getX(), 0, player.getZ() - slime.getZ()).normalize();

        player.addVelocity(direction.x * knockbackStrength, 0.2, direction.z * knockbackStrength);
        player.velocityModified = true;
        player.damage(this.slime.getDamageSources().mobAttack(this.slime), damageAmount);

        System.out.println("Pushed player with velocity: " + direction.x * knockbackStrength + ", 0.2, " + direction.z * knockbackStrength);
    }
}
