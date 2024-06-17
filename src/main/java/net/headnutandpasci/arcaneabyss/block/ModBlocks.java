package net.headnutandpasci.arcaneabyss.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.headnutandpasci.arcaneabyss.ArcaneAbyss;
import net.headnutandpasci.arcaneabyss.block.custom.SlimeSteelMaschineBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;




public class ModBlocks {

    public static final Block RUBY_BLOCK = registerBlock("ruby_block",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).sounds(BlockSoundGroup.AMETHYST_BLOCK)));
    public static final Block RAW_RUBY_BLOCK = registerBlock("raw_ruby_block",
            new Block(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));

    public static final Block DUNGEON_WALL_BLOCK = registerBlock("dungeon_wall_block",
            new Block(FabricBlockSettings.copyOf(Blocks.BEDROCK).noBlockBreakParticles()));

    public static final Block SlIMESTEEL_MASCHINE = registerBlock("slimesteel_maschine",
            new SlimeSteelMaschineBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK).nonOpaque()));



    private static Block registerBlock(String name, Block block){
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(ArcaneAbyss.MOD_ID, name), block);
    }
    private static Item registerBlockItem(String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier(ArcaneAbyss.MOD_ID, name),
                new BlockItem(block, new FabricItemSettings()));
    }


    public static void registerModBlocks(){
        ArcaneAbyss.LOGGER.info("Registering ModBlocks for" + ArcaneAbyss.MOD_ID);
    }
}
