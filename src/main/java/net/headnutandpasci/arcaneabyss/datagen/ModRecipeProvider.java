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

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.DEFENSE_RING)
                .pattern("ODO")
                .pattern("O O")
                .pattern("OOO")
                .input('O', ModItems.OBSIDIANSTEEL_INGOT)
                .input('D', Items.DIAMOND_BLOCK)
                .criterion(hasItem(ModItems.OBSIDIANSTEEL_INGOT), conditionsFromItem(ModItems.OBSIDIANSTEEL_INGOT))
                .criterion(hasItem(Items.DIAMOND_BLOCK), conditionsFromItem(Items.DIAMOND_BLOCK))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.DEFENSE_RING)));

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
                .input('D', ModItems.DEFENSE_RING)
                .input('N', Items.NETHERITE_INGOT)
                .criterion(hasItem(ModItems.STOMP_RING), conditionsFromItem(ModItems.STOMP_RING))
                .criterion(hasItem(ModItems.DEFENSE_RING), conditionsFromItem(ModItems.DEFENSE_RING))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.BULWARK_STOMP_RING)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.OBSIDIANSTEEL_INGOT)
                .pattern("OCO")
                .pattern("   ")
                .pattern("   ")
                .input('O', Items.IRON_INGOT)
                .input('C', Items.OBSIDIAN)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .criterion(hasItem(Items.OBSIDIAN), conditionsFromItem(Items.OBSIDIAN))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.OBSIDIANSTEEL_INGOT)));

        ShapedRecipeJsonBuilder.create(RecipeCategory.COMBAT, ModItems.SLIME_STAFF)
                .pattern("N")
                .pattern("R")
                .pattern("R")
                .input('N', ModItems.SLIME_STEEL_BALL)
                .input('R', ModItems.SLIMESTEEL_INGOT)
                .criterion(hasItem(ModItems.SLIMESTEEL_INGOT), conditionsFromItem(ModItems.SLIMESTEEL_INGOT))
                .criterion(hasItem(ModItems.SLIME_STEEL_BALL), conditionsFromItem(ModItems.SLIME_STEEL_BALL))
                .offerTo(exporter, new Identifier(getRecipeName(ModItems.SLIME_STAFF)));

    }
}
