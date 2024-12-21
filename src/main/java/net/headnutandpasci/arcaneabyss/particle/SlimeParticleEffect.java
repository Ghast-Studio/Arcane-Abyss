package net.headnutandpasci.arcaneabyss.particle;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.Locale;

public class SlimeParticleEffect implements ParticleEffect {
    public static final Vector3f GREEN = Vec3d.unpackRgb(0x00FF00).toVector3f();
    public static final SlimeParticleEffect DEFAULT = new SlimeParticleEffect(GREEN);

    public static final Codec<SlimeParticleEffect> CODEC = Codecs.validate(RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codecs.VECTOR_3F.fieldOf("tint").forGetter(effect -> effect.tint))
            .apply(instance, SlimeParticleEffect::new)), SlimeParticleEffect::validate).codec();

    public static final ParticleEffect.Factory<SlimeParticleEffect> PARAMETERS_FACTORY = new ParticleEffect.Factory<>() {
        @Override
        public SlimeParticleEffect read(ParticleType<SlimeParticleEffect> type, StringReader reader) throws CommandSyntaxException {
            reader.expect(' ');
            float r = (float) reader.readDouble();
            reader.expect(' ');
            float g = (float) reader.readDouble();
            reader.expect(' ');
            float b = (float) reader.readDouble();
            return new SlimeParticleEffect(new Vector3f(r, g, b));
        }

        @Override
        public SlimeParticleEffect read(ParticleType<SlimeParticleEffect> type, PacketByteBuf buf) {
            return new SlimeParticleEffect(new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat()));
        }
    };

    private final Vector3f tint;

    public SlimeParticleEffect(Vector3f tint) {
        this.tint = tint;
    }

    public SlimeParticleEffect(float r, float g, float b) {
        this(new Vector3f(r, g, b));
    }

    public SlimeParticleEffect(int rgb) {
        this(Vec3d.unpackRgb(rgb).toVector3f());
    }

    private static DataResult<SlimeParticleEffect> validate(SlimeParticleEffect effect) {
        return DataResult.success(effect);
    }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.SLIME;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeFloat(this.tint.x);
        buf.writeFloat(this.tint.y);
        buf.writeFloat(this.tint.z);
    }

    @Override
    public String asString() {
        return String.format(Locale.ROOT, "%s %.2f %.2f %.2f", Registries.PARTICLE_TYPE.getId(this.getType()), (float) this.tint.x, (float) this.tint.y, (float) this.tint.z);
    }

    public Vector3f getTint() {
        return tint;
    }
}
