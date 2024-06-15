package net.headnutandpasci.arcaneabyss.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.item.Moditems;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

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




    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        //Item Models
        itemModelGenerator.register(Moditems.RUBY, Models.GENERATED);
        itemModelGenerator.register(Moditems.RAW_RUBY, Models.GENERATED);
        itemModelGenerator.register(Moditems.RUBY_SWORD, Models.GENERATED);
        itemModelGenerator.register(Moditems.RUBY_AXE, Models.GENERATED);
        itemModelGenerator.register(Moditems.RUBY_PICKAXE, Models.GENERATED);
        itemModelGenerator.register(Moditems.RUBY_HOE, Models.GENERATED);
    }
}
