package net.headnutandpasci.arcaneabyss.particle;

import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static ParticleType<SlimeParticleEffect> SLIME;

    public static void init() {
        ArcaneAbyss.LOGGER.info("Registering particles");
        SLIME = ParticleTypes.register(new Identifier(ArcaneAbyss.MOD_ID, "slime").toString(),
                false,
                SlimeParticleEffect.PARAMETERS_FACTORY,
                particleType -> SlimeParticleEffect.CODEC);
    }
}
