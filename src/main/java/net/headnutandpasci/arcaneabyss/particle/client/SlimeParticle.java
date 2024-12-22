package net.headnutandpasci.arcaneabyss.particle.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.headnutandpasci.arcaneabyss.particle.SlimeParticleEffect;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import org.jetbrains.annotations.Nullable;

@Environment(value = EnvType.CLIENT)
public class SlimeParticle extends SpriteBillboardParticle {
    private final float sampleU;
    private final float sampleV;

    public SlimeParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, SlimeParticleEffect effect, SpriteProvider provider) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.velocityMultiplier = 0.96f;
        this.ascending = true;
        this.velocityX *= 0.1f;
        this.velocityY *= 0.1f;
        this.velocityZ *= 0.1f;
        this.sampleU = this.random.nextFloat() * 3.0F;
        this.sampleV = this.random.nextFloat() * 3.0F;
        this.red = effect.getTint().x();
        this.green = effect.getTint().y();
        this.blue = effect.getTint().z();
        this.setSprite(provider);
    }

    protected float getMinU() {
        return this.sprite.getFrameU((this.sampleU + 1.0F) / 4.0F * 16.0F);
    }

    protected float getMaxU() {
        return this.sprite.getFrameU(this.sampleU / 4.0F * 16.0F);
    }

    protected float getMinV() {
        return this.sprite.getFrameV(this.sampleV / 4.0F * 16.0F);
    }

    protected float getMaxV() {
        return this.sprite.getFrameV((this.sampleV + 1.0F) / 4.0F * 16.0F);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_OPAQUE;
    }

    @Environment(value = EnvType.CLIENT)
    public static class Factory
            implements ParticleFactory<SlimeParticleEffect> {
        private final SpriteProvider provider;

        public Factory(SpriteProvider provider) {
            this.provider = provider;
        }

        @Override
        public @Nullable Particle createParticle(SlimeParticleEffect effect, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new SlimeParticle(world, x, y, z, velocityX, velocityY, velocityZ, effect, this.provider);
        }
    }
}
