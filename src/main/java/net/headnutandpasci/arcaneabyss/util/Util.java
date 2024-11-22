package net.headnutandpasci.arcaneabyss.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;

public class Util {

    public static void pushPlayer(LivingEntity attacker, PlayerEntity target, float baseDamage, double powah) {
        float damageAmount = baseDamage;

        if (target.isBlocking()) {
            target.disableShield(true);
            damageAmount *= 0.3F;
        }

        Vec3d direction = new Vec3d(target.getX() - attacker.getX(), 0, target.getZ() - attacker.getZ()).normalize();

        target.addVelocity(direction.x * powah, 1, direction.z * powah);
        target.velocityModified = true;
        target.damage(attacker.getDamageSources().mobAttack(attacker), damageAmount);
    }

    public static void spawnCircleParticles(ServerWorld world, Vec3d center, ParticleEffect particle, double radius, int particleCount, boolean spawnOnGround) {
        double centerX = center.getX();
        double centerY = center.getY();
        double centerZ = center.getZ();

        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double x = centerX + radius * Math.cos(angle);
            double z = centerZ + radius * Math.sin(angle);
            double y = spawnOnGround ? world.getTopY(Heightmap.Type.MOTION_BLOCKING, (int) x, (int) z) : centerY;

            world.spawnParticles(particle, x, y, z, 1, 0, 0, 0, 0);
        }
    }
}
