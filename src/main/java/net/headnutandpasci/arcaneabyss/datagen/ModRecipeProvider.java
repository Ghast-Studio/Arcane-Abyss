package net.headnutandpasci.arcaneabyss.datagen;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.headnutandpasci.arcaneabyss.block.ModBlocks;
import net.headnutandpasci.arcaneabyss.item.Moditems;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends FabricRecipeProvider {
    private static List<ItemConvertible> RUBY_SMELTABLES = List.of(Moditems.RAW_RUBY);


    public ModRecipeProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        offerSmelting(exporter, RUBY_SMELTABLES, RecipeCategory.MISC, Moditems.RUBY, 0.7f, 200, "ruby");
        offerBlasting(exporter, RUBY_SMELTABLES, RecipeCategory.MISC, Moditems.RUBY, 0.7f, 100, "ruby");
        offerReversibleCompactingRecipes(exporter, RecipeCategory.BUILDING_BLOCKS, Moditems.RUBY, RecipeCategory.DECORATIONS, ModBlocks.RUBY_BLOCK);


        ShapedRecipeJsonBuilder.create(RecipeCategory.TOOLS, Moditems.RUBY_SWORD,1)
                .pattern("S")
                .pattern("S")
                .pattern("R")
                .input('S', Moditems.RUBY)
                .input('R', Items.STICK)
                .criterion(hasItem(Items.STICK), conditionsFromItem(Items.STICK))
                .criterion(hasItem(Moditems.RUBY), conditionsFromItem(Moditems.RUBY))
                .offerTo(exporter, new Identifier(getRecipeName(Moditems.RUBY_SWORD)));
        }
    }

