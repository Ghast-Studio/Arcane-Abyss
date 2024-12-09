package net.headnutandpasci.arcaneabyss.mixin.groups;

import net.headnutandpasci.arcaneabyss.recipe.SlimeSteelRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeBook;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RecipeBook.class)
public abstract class RecipeBookMixin {
    @Shadow public abstract void add(Recipe<?> recipe);

    @Inject(at = @At("TAIL"), method = "contains(Lnet/minecraft/recipe/Recipe;)Z", cancellable = true)
    public void contains(Recipe<?> recipe, CallbackInfoReturnable<Boolean> cir) {
        if(recipe instanceof SlimeSteelRecipe) {
            this.add(recipe);
            cir.setReturnValue(true);
        }
    }
}
