package net.headnutandpasci.arcaneabyss.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.item.Moditems;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;
import net.minecraft.item.ArmorItem;

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
        blockStateModelGenerator.registerSimpleState(ModBlocks.SlIMESTEEL_MASCHINE);



    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        //Item Models
        itemModelGenerator.register(Moditems.RUBY, Models.GENERATED);
        itemModelGenerator.register(Moditems.RAW_RUBY, Models.GENERATED);

        itemModelGenerator.register(Moditems.TOMATO, Models.GENERATED);

        itemModelGenerator.register(Moditems.RUBY_SWORD, Models.HANDHELD);
        itemModelGenerator.register(Moditems.RUBY_AXE, Models.HANDHELD);
        itemModelGenerator.register(Moditems.RUBY_PICKAXE, Models.HANDHELD);
        itemModelGenerator.register(Moditems.RUBY_HOE, Models.HANDHELD);

        itemModelGenerator.registerArmor(((ArmorItem) Moditems.RUBY_HELMET));
        itemModelGenerator.registerArmor(((ArmorItem) Moditems.RUBY_CHESTPLATE));
        itemModelGenerator.registerArmor(((ArmorItem) Moditems.RUBY_LEGGING));
        itemModelGenerator.registerArmor(((ArmorItem) Moditems.RUBY_BOOTS));
    }
}
