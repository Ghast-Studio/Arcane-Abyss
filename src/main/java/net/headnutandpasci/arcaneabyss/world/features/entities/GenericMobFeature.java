package net.headnutandpasci.arcaneabyss.world.features.entities;

import com.mojang.serialization.Codec;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.world.features.config.EntityTypeConfig;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class GenericMobFeature extends Feature<EntityTypeConfig> {
    public GenericMobFeature(Codec<EntityTypeConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<EntityTypeConfig> context) {
        MobEntity entity = (MobEntity) context.getConfig().type().create(context.getWorld().toServerWorld(), null, null, context.getOrigin(), SpawnReason.COMMAND, true, true);
        if (entity == null) {
            ArcaneAbyss.LOGGER.warn("Failed to create entity {}", context.getConfig().type());
            return false;
        }

        context.getWorld().spawnEntity(entity);
        entity.setPersistent();
        return true;
    }
}