package net.headnutandpasci.arcaneabyss.entity.ai.goal;

import net.headnutandpasci.arcaneabyss.entity.slime.ArcaneBossSlime;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;

public class SlimePushGoal extends Goal {
    private final ArcaneBossSlime bossSlime;

    public SlimePushGoal(ArcaneBossSlime bossSlime) {
        this.bossSlime = bossSlime;
    }

    @Override
    public boolean canStart() {
        return bossSlime.isInState(ArcaneBossSlime.State.PUSH) && bossSlime.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        this.bossSlime.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 3.0F, 1.0F);
        ((ServerWorld) this.bossSlime.getWorld()).spawnParticles(ParticleTypes.POOF, this.bossSlime.getX(), this.bossSlime.getY(), this.bossSlime.getZ(), 50, 3.0D, 0.0D, 3.0D, 0.0D);
        this.bossSlime.getPlayerNearby().forEach(this::pushNearbyPlayers);
        this.bossSlime.stopAttacking(100);
    }

    private void pushNearbyPlayers(PlayerEntity player) {
        double knockbackStrength = 15.0D;
        int damageAmount;

        if (player.isBlocking()) {
            player.disableShield(true);
            damageAmount = (int) (this.bossSlime.getAttackDamage() * 1F);
        } else {
            damageAmount = (int) (this.bossSlime.getAttackDamage() * 2F);
        }
        double x = player.getX() - this.bossSlime.getX();
        double z = player.getZ() - this.bossSlime.getZ();
        double a = Math.max(x * x + z * z, 0.001);
        this.pushPlayer(player, x / a * knockbackStrength, 0.2, z / a * knockbackStrength);
        /*player.push(x / a * knockbackStrength, 0.2, z / a * knockbackStrength);*/
        player.damage(this.bossSlime.getDamageSources().mobAttack(this.bossSlime), damageAmount);
    }

    public void pushPlayer(PlayerEntity player, double x, double y, double z) {
        player.addVelocity(x, y, z);
        player.velocityModified = true;
    }
}
