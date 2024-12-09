package net.headnutandpasci.arcaneabyss.mixin.groups;

import net.headnutandpasci.arcaneabyss.recipe.SlimeSteelRecipe;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.Recipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RecipeResultCollection.class)
public abstract class RecipeBookGroupMixin {

    @Shadow
    @Final
    private List<Recipe<?>> recipes;

    @Inject(at = @At("HEAD"), method = "isInitialized", cancellable = true)
    public void getGroups(CallbackInfoReturnable<Boolean> cir) {
        this.recipes.stream().filter(recipe -> recipe instanceof SlimeSteelRecipe).findFirst().ifPresent(recipe -> {
            cir.setReturnValue(true);
        });
    }
}
