package net.headnutandpasci.arcaneabyss.entity.particles;

import net.headnutandpasci.arcaneabyss.entity.particles.SlimeviathanParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;

public class SlimeviathanParticleFactory<TestParticle extends ParticleEffect> implements ParticleFactory<TestParticle>{
    private final SpriteProvider spriteProvider;

    public SlimeviathanParticleFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(TestParticle parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        SlimeviathanParticle particle = new SlimeviathanParticle(world, x, y, z, velocityX, velocityY, velocityZ);
        particle.setSprite(this.spriteProvider);
        return particle;
    }
}
