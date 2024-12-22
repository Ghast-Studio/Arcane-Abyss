package net.headnutandpasci.arcaneabyss.block.entity;

import net.headnutandpasci.arcaneabyss.block.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class DungeonLeverBlockEntity extends BlockEntity {
    private List<BlockPos> targetOffsets;

    public DungeonLeverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.DUNGEON_LEVER_ENTITY, pos, state);
        this.targetOffsets = List.of(BlockPos.ofFloored(0, 0, 0));
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        long[] longBlocks = nbt.getLongArray("target_offsets");
        this.targetOffsets = new ArrayList<>();
        for (long longBlock : longBlocks) {
            this.targetOffsets.add(BlockPos.fromLong(longBlock));
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        long[] longBlocks = this.targetOffsets.stream().mapToLong(BlockPos::asLong).toArray();
        nbt.putLongArray("target_offsets", longBlocks);
    }

    public List<BlockPos> getTargetOffsets() {
        return targetOffsets;
    }
}
