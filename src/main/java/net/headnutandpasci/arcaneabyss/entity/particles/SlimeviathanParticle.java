package net.headnutandpasci.arcaneabyss.entity.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.particle.ParticleTextureSheet;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class SlimeviathanParticle extends SpriteBillboardParticle {

    public SlimeviathanParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);

        // Set motion of the particle (similar to End Rod)
        this.velocityX = velocityX * 0.1;
        this.velocityY = velocityY * 0.1;
        this.velocityZ = velocityZ * 0.1;

        // Set the particle's color, size, and lifespan
        this.scale = 0.5f; // Similar to End Rod
        this.maxAge = 80;  // Lasts for 80 ticks (4 seconds)

        // Set the color to white (you can adjust this)
        this.setColor(1.0f, 1.0f, 1.0f);
    }

    @Override
    public void tick() {
        super.tick();

        // End Rod effect fades out gradually
        this.alpha = MathHelper.clamp(((float) this.maxAge - (float) this.age) / this.maxAge, 0.0F, 1.0F);

        // Add some movement (like end rod particles floating upwards)
        this.velocityY += 0.004D;

        // Update the particle position based on velocity
        this.move(this.velocityX, this.velocityY, this.velocityZ);
    }

    @Override
    public ParticleTextureSheet getType() {
        // Return the particle texture sheet to use (usually translucent)
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }
}
