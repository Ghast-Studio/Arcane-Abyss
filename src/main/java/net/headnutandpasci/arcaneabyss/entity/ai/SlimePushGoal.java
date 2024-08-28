package net.headnutandpasci.arcaneabyss.entity.ai;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.entity.slime.boss.black.BlackSlimeEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Box;

import java.util.List;

public class SlimePushGoal extends Goal {
    private final BlackSlimeEntity slime;

    public SlimePushGoal(BlackSlimeEntity slime) {
        this.slime = slime;
    }

    @Override
    public boolean canStart() {
        return slime.isAttacking(BlackSlimeEntity.State.PUSH) && slime.getTarget() != null;
    }

    @Override
    public void start() {
        super.start();
        /*chaosSpawnerEntity.triggerSmashAttackAnimation();*/
        this.slime.setAttackTimer(100);
    }

    @Override
    public void tick() {
        if (this.slime.getAttackTimer() == 66) { // Only even number tick works for some reason
            ArcaneAbyss.LOGGER.info("SlimePushGoal push tick");
            this.slime.playSound(SoundEvents.ENTITY_ENDER_DRAGON_GROWL, 3.0F, 1.0F);
            ((ServerWorld) this.slime.getWorld()).spawnParticles(ParticleTypes.POOF, this.slime.getX(), this.slime.getY(), this.slime.getZ(), 50, 3.0D, 0.0D, 3.0D, 0.0D);
            Box box = (new Box(this.slime.getBlockPos())).expand(8);
            List<PlayerEntity> targets = this.slime.getWorld().getEntitiesByClass(PlayerEntity.class, box, (entity) -> true);
            targets.forEach(this::pushNearbyPlayers);
        }

        if (this.slime.getAttackTimer() == 0) {
            this.slime.stopAttacking(0);
        }
    }

    private void pushNearbyPlayers(PlayerEntity player) {
        double knockbackStrength = 12.0D;
        int damageAmount;



        if (player.isBlocking()) {
            player.disableShield(true);
            damageAmount = (int) (this.slime.getAttackDamage() * 1F);
        } else {
            damageAmount = (int) (this.slime.getAttackDamage() * 2F);
        }
        double x = player.getX() - this.slime.getX();
        double z = player.getZ() - this.slime.getZ();
        double a = Math.max(x * x + z * z, 0.001);
        this.pushPlayer(player,x / a * knockbackStrength, 0.2, z / a * knockbackStrength);
        /*player.push(x / a * knockbackStrength, 0.2, z / a * knockbackStrength);*/
        player.damage(this.slime.getDamageSources().mobAttack(this.slime), damageAmount);
    }

    public void pushPlayer(PlayerEntity player, double x, double y, double z) {
        player.addVelocity(x, y, z);
        player.velocityModified = true;
    }
}
