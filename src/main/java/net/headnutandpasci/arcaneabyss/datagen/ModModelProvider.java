package net.headnutandpasci.arcaneabyss.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.item.ModItems;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        // Dungeon Blocks
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_WALL_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_BRICK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_BRICK_CRACKED);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_BRICK_SLAB);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_BRICK_STAIR);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_STONE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_STONE_CHISELED);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_COBBLE);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_COBBLE_SLAB);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_COBBLE_STAIR);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_BRICK_WALL);

        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.DUNGEON_LEVER);

        // Slimesteel Blocks
        blockStateModelGenerator.registerSimpleState(ModBlocks.SLIMESTEEL_MACHINE);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        //Item Models
        itemModelGenerator.register(ModItems.SLIMESTEEL_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.SLIME_CRYSTALLISATION, Models.GENERATED);
        itemModelGenerator.register(ModItems.OBSIDIANSTEEL_INGOT, Models.GENERATED);
        itemModelGenerator.register(ModItems.SLIME_FLESH, Models.GENERATED);
        itemModelGenerator.register(ModItems.SLIME_JUICE, Models.GENERATED);

        itemModelGenerator.register(ModItems.COOKED_SLIME_MEAT, Models.GENERATED);
        itemModelGenerator.register(ModItems.SLIME_STEEL_BALL, Models.GENERATED);

        itemModelGenerator.register(ModItems.DEFENSE_RING, Models.GENERATED);
        itemModelGenerator.register(ModItems.STOMP_RING, Models.GENERATED);
        itemModelGenerator.register(ModItems.BULWARK_STOMP_RING, Models.GENERATED);
        itemModelGenerator.register(ModItems.TELEPORT_BELT, Models.GENERATED);

        itemModelGenerator.register(ModItems.DEFENSE_NECKLACE, Models.GENERATED);

        itemModelGenerator.register(ModItems.SLIME_SWORD, Models.HANDHELD);
    }
}
