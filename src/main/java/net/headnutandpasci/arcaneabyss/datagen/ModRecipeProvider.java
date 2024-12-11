package net.headnutandpasci.arcaneabyss.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.headnutandpasci.arcaneabyss.item.ModItems;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    private static final List<ItemConvertible> SLIME_COOKABLES = List.of(ModItems.SLIME_FLESH);

    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        offerSmelting(exporter, SLIME_COOKABLES, RecipeCategory.FOOD, ModItems.COOKED_SLIME_MEAT, 0.7f, 200, "slime_meat");
        offerMultipleOptions(exporter, RecipeSerializer.SMOKING, SLIME_COOKABLES, RecipeCategory.FOOD, ModItems.COOKED_SLIME_MEAT, 0.7f, 200, "slime_meat", "_from_smoking");

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.SLIME_SWORD)
                .pattern("R")
                .pattern("R")
                .pattern("N")
                .input('R', ModItems.SLIMESTEEL_INGOT)
                .input('N', Items.NETHERITE_INGOT)
                .criterion(hasItem(ModItems.SLIMESTEEL_INGOT), conditionsFromItem(ModItems.SLIMESTEEL_INGOT))
                .criterion(hasItem(Items.NETHERITE_INGOT), conditionsFromItem(Items.NETHERITE_INGOT))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SLIME_SWORD)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.SLIME_STAFF)
                .pattern("N")
                .pattern("R")
                .pattern("R")
                .input('N', ModItems.SLIME_STEEL_BALL)
                .input('R', ModItems.SLIMESTEEL_INGOT)
                .criterion(hasItem(ModItems.SLIMESTEEL_INGOT), conditionsFromItem(ModItems.SLIMESTEEL_INGOT))
                .criterion(hasItem(ModItems.SLIME_STEEL_BALL), conditionsFromItem(ModItems.SLIME_STEEL_BALL))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SLIME_STAFF)));

        //createArmorRecipe(exporter, ModItems.RUBY_HELMET, "RRR", "R R", RecipeCategory.COMBAT, "ruby_helmet");
        //createArmorRecipe(exporter, ModItems.RUBY_CHESTPLATE, "R R", "RRR", "RRR", RecipeCategory.COMBAT, "ruby_chestplate");
        //createArmorRecipe(exporter, ModItems.RUBY_LEGGINGS, "RRR", "R R", "R R", RecipeCategory.COMBAT, "ruby_leggings");
        //createArmorRecipe(exporter, ModItems.RUBY_BOOTS, "R R", "R R", RecipeCategory.COMBAT, "ruby_boots");
    }


/*    private void createArmorRecipe(Consumer<RecipeJsonProvider> exporter, ItemConvertible result, String row1, String row2, String row3, RecipeCategory category, String recipeName) {
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
    }*/
}
