package net.headnutandpasci.arcaneabyss.world.features.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.world.gen.feature.FeatureConfig;

public record EntityTypeConfig(EntityType<?> type) implements FeatureConfig {
    public static final Codec<EntityTypeConfig> CODEC = RecordCodecBuilder.create((configInstance) -> configInstance.group(
            Registries.ENTITY_TYPE.getCodec().fieldOf("entity_type").forGetter(config -> config.type)
    ).apply(configInstance, EntityTypeConfig::new));
}
