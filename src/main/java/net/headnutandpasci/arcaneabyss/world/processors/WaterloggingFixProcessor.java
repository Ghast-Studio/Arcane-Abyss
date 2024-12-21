package net.headnutandpasci.arcaneabyss.world.processors;

import com.mojang.serialization.Codec;
import net.headnutandpasci.arcaneabyss.world.structures.ModStructures;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.processor.StructureProcessor;
import net.minecraft.structure.processor.StructureProcessorType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

public class WaterloggingFixProcessor extends StructureProcessor {
    public static final Codec<WaterloggingFixProcessor> CODEC = Codec.unit(WaterloggingFixProcessor::new);

    // TODO: Check if the block is waterlogged and remove the waterlogging
    @Override
    public @Nullable StructureTemplate.StructureBlockInfo process(WorldView world,
                                                                  BlockPos pos,
                                                                  BlockPos pivot,
                                                                  StructureTemplate.StructureBlockInfo original,
                                                                  StructureTemplate.StructureBlockInfo current,
                                                                  StructurePlacementData data) {
        BlockState existingState = original.state();
        BlockState currentState = current.state();
        BlockPos currentPos = current.pos();

        boolean isWaterAtPos = existingState.getFluidState().isOf(Fluids.WATER);

        if (isWaterAtPos && currentState.contains(Properties.WATERLOGGED)) {
            return new StructureTemplate.StructureBlockInfo(
                    currentPos,
                    currentState.with(Properties.WATERLOGGED, false),
                    current.nbt()
            );
        }

        return current;
    }

    @Override
    protected StructureProcessorType<?> getType() {
        return ModStructures.WATERLOGGING_FIX_PROCESSOR;
    }
}
