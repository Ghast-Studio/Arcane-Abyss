package net.headnutandpasci.arcaneabyss.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Identifier;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        //Block Models
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RUBY_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ModBlocks.RAW_RUBY_BLOCK);
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
        blockStateModelGenerator.registerSimpleState(ModBlocks.SLIMESTEEL_MACHINE);
        
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        //Item Models
        itemModelGenerator.register(ModItems.RUBY, Models.GENERATED);
        itemModelGenerator.register(ModItems.RAW_RUBY, Models.GENERATED);
        itemModelGenerator.register(ModItems.SLIMESTEEL_INGOT, Models.GENERATED);

        itemModelGenerator.register(ModItems.TOMATO, Models.GENERATED);

        itemModelGenerator.register(ModItems.RUBY_SWORD, Models.HANDHELD);
        itemModelGenerator.register(ModItems.RUBY_AXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.RUBY_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(ModItems.RUBY_HOE, Models.HANDHELD);

        itemModelGenerator.registerArmor(((ArmorItem) ModItems.RUBY_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.RUBY_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.RUBY_LEGGING));
        itemModelGenerator.registerArmor(((ArmorItem) ModItems.RUBY_BOOTS));
    }





}
