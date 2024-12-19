package net.headnutandpasci.arcaneabyss.util;

import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ParticleUtil {
    private ParticleUtil() {
    }

    public static void particleSphere(World world, Vec3d pos, ParticleEffect effect, int count, int radius) {
        for (int i = 0; i < count; i++) {
            double theta = Math.random() * 2 * Math.PI;
            double phi = Math.random() * Math.PI;

            double xOffset = radius * Math.sin(phi) * Math.cos(theta);
            double yOffset = radius * Math.sin(phi) * Math.sin(theta);
            double zOffset = radius * Math.cos(phi);

            world.addParticle(
                    effect,
                    pos.getX() + xOffset,
                    pos.getY() + yOffset,
                    pos.getZ() + zOffset,
                    0,
                    0,
                    0
            );
        }
    }

}
