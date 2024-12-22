package net.headnutandpasci.arcaneabyss.block.custom;

import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.block.entity.DungeonLeverBlockEntity;
import net.headnutandpasci.arcaneabyss.particle.SlimeParticleEffect;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class DungeonLeverBlock extends Block implements BlockEntityProvider {
    public DungeonLeverBlock(Settings settings) {
        super(settings);
    }

    private static void breakBlock(World world, BlockPos target) {
        world.breakBlock(target, false);
        for (int i = 0; i < 10; i++) {
            world.addParticle(new SlimeParticleEffect(0x631580),
                    target.getX() + Math.random() * 2 - 1,
                    target.getY() + Math.random() * 2 - 1,
                    target.getZ() + Math.random() * 2 - 1,
                    0,
                    0,
                    0);

        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof DungeonLeverBlockEntity lever) {
            lever.getTargetOffsets().forEach(targetOffset -> {
                BlockPos target = pos.add(targetOffset);
                BlockState targetState = world.getBlockState(target);
                if (!targetState.isOf(ModBlocks.DUNGEON_STONE)) {
                    return;
                }

                breakBlock(world, target);
            });

            breakBlock(world, pos);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean shouldDropItemsOnExplosion(Explosion explosion) {
        return false;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DungeonLeverBlockEntity(pos, state);
    }
}
