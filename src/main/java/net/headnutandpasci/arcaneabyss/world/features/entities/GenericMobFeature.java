package net.headnutandpasci.arcaneabyss.world.features.entities;

import com.mojang.serialization.Codec;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.world.features.config.EntityTypeConfig;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class GenericMobFeature extends Feature<EntityTypeConfig> {
    public GenericMobFeature(Codec<EntityTypeConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<EntityTypeConfig> context) {
        ServerWorld serverWorld = context.getWorld().toServerWorld();
        BlockPos origin = context.getOrigin();
        EntityType<?> type = context.getConfig().type();

        MobEntity entity = (MobEntity) type.create(serverWorld);
        if (entity == null) {
            ArcaneAbyss.LOGGER.warn("[GenericMobFeature] Failed to create entity {}", type);
            return false;
        }

        try {
            entity.setPersistent();
            entity.refreshPositionAndAngles(origin.getX() + 0.5, origin.getY(), origin.getZ() + 0.5, 0.0F, 0.0F);
            serverWorld.spawnEntity(entity);
            LocalDifficulty localDifficulty = new LocalDifficulty(serverWorld.getDifficulty(), serverWorld.getTimeOfDay(), 1, serverWorld.getMoonSize());
            entity.initialize(serverWorld, localDifficulty, SpawnReason.STRUCTURE, new EntityData() {
            }, new NbtCompound());
        } catch (Exception e) {
            ArcaneAbyss.LOGGER.trace("[GenericMobFeature] Failed to spawn entity", e);
        }

        return true;
    }
}