package net.headnutandpasci.arcaneabyss.world.features.entities;

import com.mojang.serialization.Codec;
import net.headnutandpasci.arcaneabyss.world.features.config.EntityTypeConfig;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class SpawnerFeature extends Feature<EntityTypeConfig> {
    public SpawnerFeature(Codec<EntityTypeConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(FeatureContext<EntityTypeConfig> context) {
        StructureWorldAccess world = context.getWorld();
        BlockPos blockPos = context.getOrigin().down();

        this.setBlockState(world, blockPos, Blocks.SPAWNER.getDefaultState());

        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        if (blockEntity instanceof MobSpawnerBlockEntity spawnerBlockEntity) {
            spawnerBlockEntity.setEntityType(context.getConfig().type(), world.getRandom());
        }
        return true;
    }
}