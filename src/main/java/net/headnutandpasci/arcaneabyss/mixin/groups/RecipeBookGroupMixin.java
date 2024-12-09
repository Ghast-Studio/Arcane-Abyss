package net.headnutandpasci.arcaneabyss.mixin.groups;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import net.headnutandpasci.arcaneabyss.ArcaneAbyssModClient;
import net.headnutandpasci.arcaneabyss.recipe.SlimeSteelRecipe;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Mixin(ClientRecipeBook.class)
public abstract class RecipeBookGroupMixin {

    @Shadow private Map<RecipeBookGroup, List<RecipeResultCollection>> resultsByGroup;

    @Shadow
    private static Map<RecipeBookGroup, List<List<Recipe<?>>>> toGroupedMap(Iterable<Recipe<?>> recipes) {
        return null;
    }

    @Inject(at = @At("TAIL"), method = "reload")
    public void getGroups(Iterable<Recipe<?>> recipes, DynamicRegistryManager registryManager, CallbackInfo ci) {
        /*Map<RecipeBookGroup, List<List<Recipe<?>>>> map = toGroupedMap(recipes);
        Map<RecipeBookGroup, List<RecipeResultCollection>> map2 = Maps.newHashMap();
        ImmutableList.Builder<RecipeResultCollection> builder = ImmutableList.builder();
        map.forEach((recipeBookGroup, list) -> {
            if(recipeBookGroup.equals(ArcaneAbyssModClient.SEARCH_GROUP)) {
                List<Recipe<?>> recipe = list.get(0);
                map2.put(recipeBookGroup, (List<RecipeResultCollection>) new RecipeResultCollection(registryManager, recipe).collect(ImmutableList.toImmutableList()));

            }
        });
        resultsByGroup.put(ArcaneAbyssModClient.MAIN_GROUP, );*/
    }
}
