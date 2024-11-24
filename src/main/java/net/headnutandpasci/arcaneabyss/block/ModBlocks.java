package net.headnutandpasci.arcaneabyss.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.block.custom.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;


public class ModBlocks {

    public static Block RUBY_BLOCK;
    public static Block RAW_RUBY_BLOCK;
    public static Block DUNGEON_WALL_BLOCK;
    public static Block DUNGEON_BRICK;
    public static Block DUNGEON_BRICK_CRACKED;
    public static Block DUNGEON_BRICK_WALL;
    public static Block DUNGEON_BRICK_STAIR;
    public static Block DUNGEON_BRICK_SLAB;
    public static Block DUNGEON_STONE;
    public static Block DUNGEON_STONE_CHISELED;
    public static Block DUNGEON_COBBLE;
    public static Block DUNGEON_COBBLE_STAIR;
    public static Block DUNGEON_COBBLE_SLAB;
    public static Block SLIMESTEEL_MACHINE;

    private static Block registerBlock(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(ArcaneAbyss.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, new Identifier(ArcaneAbyss.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }

    public static void registerModBlocks() {
        RUBY_BLOCK = registerBlock("ruby_block",
                new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.AMETHYST_BLOCK)));

        RAW_RUBY_BLOCK = registerBlock("raw_ruby_block",
                new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

        DUNGEON_WALL_BLOCK = registerBlock("dungeon_wall_block",
                new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK).noBlockBreakParticles()));
        DUNGEON_BRICK = registerBlock("dungeon_brick",
                new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK).noBlockBreakParticles()));
        DUNGEON_BRICK_CRACKED = registerBlock("dungeon_brick_cracked",
                new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK).noBlockBreakParticles()));
        DUNGEON_STONE = registerBlock("dungeon_stone",
                new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK).noBlockBreakParticles()));
        DUNGEON_STONE_CHISELED = registerBlock("dungeon_stone_chiseled",
                new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK).noBlockBreakParticles()));
        DUNGEON_COBBLE = registerBlock("dungeon_cobble",
                new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK).noBlockBreakParticles()));




        SLIMESTEEL_MACHINE = registerBlock("slimesteel_machine",
                new SlimeSteelMachineBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));


        DUNGEON_BRICK_STAIR = registerBlock("dungeon_brick_stair",
                new DungeonBrickStair(Blocks.BEDROCK.getDefaultState(),
                        FabricBlockSettings.copyOf(Blocks.BEDROCK).noBlockBreakParticles()));
        DUNGEON_COBBLE_STAIR = registerBlock("dungeon_cobble_stair",
                new DungeonCobbleStair(Blocks.BEDROCK.getDefaultState(),
                        FabricBlockSettings.copyOf(Blocks.BEDROCK).noBlockBreakParticles()));


        DUNGEON_COBBLE_SLAB = registerBlock("dungeon_cobble_slab",
                new DungeonCobbleSlab(FabricBlockSettings.copyOf(Blocks.BEDROCK).noBlockBreakParticles()));
        DUNGEON_BRICK_SLAB = registerBlock("dungeon_brick_slab",
                new DungeonBrickSlab(FabricBlockSettings.copyOf(Blocks.BEDROCK).noBlockBreakParticles()));


        DUNGEON_BRICK_WALL = registerBlock("dungeon_brick_wall",
                new DungeonBrickWall(FabricBlockSettings.copyOf(Blocks.DEEPSLATE).sounds(BlockSoundGroup.DEEPSLATE)));






       ;




        ArcaneAbyss.LOGGER.info("Registering ModBlocks for" + ArcaneAbyss.MOD_ID);
    }
}
