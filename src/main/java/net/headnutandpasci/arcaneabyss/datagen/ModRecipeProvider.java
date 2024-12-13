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

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.RING_OF_DEFENSE)
                .pattern("ODO")
                .pattern("O O")
                .pattern("OOO")
                .input('O', ModItems.OBSIDIANSTEEL_INGOT)
                .input('D', Items.DIAMOND_BLOCK)
                .criterion(hasItem(ModItems.OBSIDIANSTEEL_INGOT), conditionsFromItem(ModItems.OBSIDIANSTEEL_INGOT))
                .criterion(hasItem(Items.DIAMOND_BLOCK), conditionsFromItem(Items.DIAMOND_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.RING_OF_DEFENSE)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.STOMP_RING)
                .pattern("OCO")
                .pattern("O O")
                .pattern("OOO")
                .input('O', ModItems.OBSIDIANSTEEL_INGOT)
                .input('C', ModItems.SLIME_CRYSTALLISATION)
                .criterion(hasItem(ModItems.OBSIDIANSTEEL_INGOT), conditionsFromItem(ModItems.OBSIDIANSTEEL_INGOT))
                .criterion(hasItem(ModItems.SLIME_CRYSTALLISATION), conditionsFromItem(ModItems.SLIME_CRYSTALLISATION))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.STOMP_RING)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.BULWARK_STOMP_RING)
                .pattern("S")
                .pattern("N")
                .pattern("D")
                .input('S', ModItems.STOMP_RING)
                .input('D', ModItems.RING_OF_DEFENSE)
                .input('N', Items.NETHERITE_INGOT)
                .criterion(hasItem(ModItems.STOMP_RING), conditionsFromItem(ModItems.STOMP_RING))
                .criterion(hasItem(ModItems.RING_OF_DEFENSE), conditionsFromItem(ModItems.RING_OF_DEFENSE))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.BULWARK_STOMP_RING)));

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
