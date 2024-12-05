package net.headnutandpasci.arcaneabyss.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.item.ModItems;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    private static final List<ItemConvertible> RUBY_SMELTABLES = List.of(ModItems.RAW_RUBY);
    private static final List<ItemConvertible> SLIME_COOKABLES = List.of(ModItems.SLIME_FLESH);

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {

        offerSmelting(exporter, RUBY_SMELTABLES, RecipeCategory.MISC, ModItems.RUBY, 0.7f, 200, "ruby");
        offerBlasting(exporter, RUBY_SMELTABLES, RecipeCategory.MISC, ModItems.RUBY, 0.7f, 100, "ruby");
        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, ModItems.RUBY, RecipeCategory.DECORATIONS, ModBlocks.RUBY_BLOCK);
        offerSmelting(exporter, SLIME_COOKABLES,RecipeCategory.FOOD, ModItems.COOKED_SLIME_MEAT,0.7f,200,"slime_meat");


        createArmorRecipe(exporter, ModItems.RUBY_HELMET, "RRR", "R R", RecipeCategory.COMBAT, "ruby_helmet");
        createArmorRecipe(exporter, ModItems.RUBY_CHESTPLATE, "R R", "RRR", "RRR", RecipeCategory.COMBAT, "ruby_chestplate");
        createArmorRecipe(exporter, ModItems.RUBY_LEGGINGS, "RRR", "R R", "R R", RecipeCategory.COMBAT, "ruby_leggings");
        createArmorRecipe(exporter, ModItems.RUBY_BOOTS, "R R", "R R", RecipeCategory.COMBAT, "ruby_boots");

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.RUBY_SWORD, 1)
                .pattern("S")
                .pattern("S")
                .pattern("R")
                .input('S', ModItems.SLIMESTEEL_INGOT)
                .input('R', ModItems.RUBY)
                .criterion(hasItem(ModItems.RUBY), conditionsFromItem(ModItems.RUBY))
                .criterion(hasItem(ModItems.SLIMESTEEL_INGOT), conditionsFromItem(ModItems.SLIMESTEEL_INGOT))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.RUBY_SWORD)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.RUBY_AXE, 1)
                .pattern("RR")
                .pattern("RS")
                .pattern(" S")
                .input('S', ModItems.RUBY)
                .input('R', ModItems.SLIMESTEEL_INGOT)
                .criterion(hasItem(ModItems.RUBY), conditionsFromItem(ModItems.RUBY))
                .criterion(hasItem(ModItems.SLIMESTEEL_INGOT), conditionsFromItem(ModItems.SLIMESTEEL_INGOT))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.RUBY_AXE)));


        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.RUBY_HOE, 1)
                .pattern("RR ")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', ModItems.RUBY)
                .input('R', ModItems.SLIMESTEEL_INGOT)
                .criterion(hasItem(ModItems.RUBY), conditionsFromItem(ModItems.RUBY))
                .criterion(hasItem(ModItems.SLIMESTEEL_INGOT), conditionsFromItem(ModItems.SLIMESTEEL_INGOT))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.RUBY_HOE)));


        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, ModItems.RUBY_PICKAXE, 1)
                .pattern("RRR")
                .pattern(" S ")
                .pattern(" S ")
                .input('S', ModItems.RUBY)
                .input('R', ModItems.SLIMESTEEL_INGOT)
                .criterion(hasItem(ModItems.RUBY), conditionsFromItem(ModItems.RUBY))
                .criterion(hasItem(ModItems.SLIMESTEEL_INGOT), conditionsFromItem(ModItems.SLIMESTEEL_INGOT))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.RUBY_PICKAXE)));

    }


    private void createArmorRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible result, String row1, String row2, String row3, RecipeCategory category, String recipeName) {
        ShapedRecipeJsonBuilder.create(category, result)
                .pattern(row1)
                .pattern(row2)
                .pattern(row3)
                .input('R', ModItems.SLIMESTEEL_INGOT)
                .criterion(hasItem(ModItems.SLIMESTEEL_INGOT), conditionsFromItem(ModItems.SLIMESTEEL_INGOT))
                .offerTo(exporter, new Identifier(getRecipeName(result)));
    }

    private void createArmorRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible result, String row1, String row2, RecipeCategory category, String recipeName) {
        ShapedRecipeJsonBuilder.create(category, result)
                .pattern(row1)
                .pattern(row2)
                .input('R', ModItems.SLIMESTEEL_INGOT)
                .criterion(hasItem(ModItems.SLIMESTEEL_INGOT), conditionsFromItem(ModItems.SLIMESTEEL_INGOT))
                .offerTo(exporter, new Identifier(getRecipeName(result)));
    }
}
